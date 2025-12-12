package com.kh.finalproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.finalproject.dao.*;
import com.kh.finalproject.dto.*;
import com.kh.finalproject.vo.MemberPointVO;
import com.kh.finalproject.vo.PointHistoryPageVO;
import com.kh.finalproject.vo.PointItemWishVO;
import com.kh.finalproject.vo.TokenVO;

@Service
public class PointService {

    @Autowired private PointItemDao pointItemDao;
    @Autowired private MemberDao memberDao;
    @Autowired private PointInventoryDao pointInventoryDao;
    @Autowired private PointHistoryDao pointHistoryDao;
    @Autowired private PointWishlistDao pointWishlistDao;
    @Autowired private MemberIconDao memberIconDao;
    // 등급 점수 변환
    private int getLevelWeight(String level) {
        if (level == null) return 0;
        switch (level) {
            case "관리자": return 99;
            case "우수회원": return 2;
            case "일반회원": return 1;
            default: return 0;
        }
    }

    // [통합 결제 로직]
    private void processTransaction(String senderId, String receiverId, long itemNo, String type) {
        // 1. 상품 & 재고 체크
        PointItemDto item = pointItemDao.selectOneNumber(itemNo);
        if (item == null) throw new RuntimeException("상품 없음");
        if (item.getPointItemStock() <= 0) throw new RuntimeException("품절");


        if (item.getPointItemUniques() == 1) { // 1이면 '1회 한정' 아이템
            // 받는 사람(receiverId)이 이미 가지고 있는지 검사
            int count = pointInventoryDao.selectCountMyItem(receiverId, itemNo);
            if (count > 0) {
                throw new RuntimeException("이미 보유하고 있는 아이템입니다. (중복 구매 불가)");
            }
        }
        // ==========================================================

        // 2. 구매자(Sender) DB 정보 조회 
        MemberDto sender = memberDao.selectOne(senderId);

        // 3. 등급 체크
        int userScore = getLevelWeight(sender.getMemberLevel());
        int reqScore = getLevelWeight(item.getPointItemReqLevel());

        if (!"관리자".equals(sender.getMemberLevel()) && userScore < reqScore) {
            throw new RuntimeException("등급 부족 (" + item.getPointItemReqLevel() + " 이상)");
        }

        // 4. 포인트 체크
        if (sender.getMemberPoint() < item.getPointItemPrice()) {
            throw new RuntimeException("포인트 부족");
        }

        // 5. 결제 진행 (포인트 차감, 재고 차감)
        sender.setMemberPoint(sender.getMemberPoint() - item.getPointItemPrice());
        memberDao.updatePoint(sender);

        item.setPointItemStock(item.getPointItemStock() - 1);
        pointItemDao.update(item);

        // 6. 인벤토리 지급
        PointInventoryDto inven = new PointInventoryDto();
        inven.setPointInventoryMemberId(receiverId);
        inven.setPointInventoryItemNo((int)itemNo);
        inven.setPointInventoryItemAmount(1);
        inven.setPointInventoryItemType(item.getPointItemType());
        pointInventoryDao.insert(inven);

        // 7. 내역 기록
        PointHistoryDto history = new PointHistoryDto();
        history.setPointHistoryMemberId(senderId);
        history.setPointHistoryAmount(-item.getPointItemPrice());
        history.setPointHistoryReason(type + ": " + item.getPointItemName());
        history.setPointHistoryItemNo((int)itemNo);
        pointHistoryDao.insert(history);
    }

    // [구매]
    @Transactional
    public void purchaseItem(String loginId, long itemNo) {
        processTransaction(loginId, loginId, itemNo, "구매");
    }

    // [선물]
    @Transactional
    public void giftItem(String senderId, String targetId, long itemNo) {
        if (senderId.equals(targetId)) throw new RuntimeException("본인 선물 불가");
        if (memberDao.selectOne(targetId) == null) throw new RuntimeException("회원 없음");
        processTransaction(senderId, targetId, itemNo, "선물(" + targetId + ")");
    }

    // [취소/환불]
    @Transactional
    public void cancelItem(String loginId, long inventoryNo) {
        PointInventoryDto inven = pointInventoryDao.selectOneNumber((int)inventoryNo);
        if (!inven.getPointInventoryMemberId().equals(loginId)) throw new RuntimeException("권한 없음");
        
        PointItemDto item = pointItemDao.selectOneNumber(inven.getPointInventoryItemNo());
        MemberDto member = memberDao.selectOne(loginId);

        // 환불 및 복구
        member.setMemberPoint(member.getMemberPoint() + item.getPointItemPrice());
        memberDao.updatePoint(member);
        
        item.setPointItemStock(item.getPointItemStock() + 1);
        pointItemDao.update(item);
        
        pointInventoryDao.delete((int)inventoryNo);

        // 기록
        PointHistoryDto history = new PointHistoryDto();
        history.setPointHistoryMemberId(loginId);
        history.setPointHistoryAmount(item.getPointItemPrice());
        history.setPointHistoryReason("취소: " + item.getPointItemName());
        history.setPointHistoryItemNo(item.getPointItemNo());
        pointHistoryDao.insert(history);
    }
    
    // [관리자] 상품 등록
    @Transactional
    public void addItem(String loginId, PointItemDto itemDto) {
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null || !member.getMemberLevel().equals("관리자")) {
            throw new RuntimeException("관리자 권한이 없습니다.");
        }
        pointItemDao.insert(itemDto);
    }
 // [관리자] 상품 수정
    @Transactional
    public void editItem(String loginId, PointItemDto itemDto) {
        // 1. 관리자 체크
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null || !member.getMemberLevel().equals("관리자")) {
            throw new RuntimeException("관리자 권한이 없습니다.");
        }
        // 2. 수정 실행
        pointItemDao.update(itemDto);
    }
 // [관리자] 상품 삭제
    @Transactional
    public void deleteItem(String loginId, int itemNo) {
        // 1. 관리자 체크
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null || !member.getMemberLevel().equals("관리자")) {
            throw new RuntimeException("관리자 권한이 없습니다.");
        }
        // 2. 삭제 실행
        pointItemDao.delete(itemNo);
    }
    //인벤토리 아이템폐기
    @Transactional
    public void discardItem(String loginId, int inventoryNo) {
        // 1. 내 아이템인지 확인
        PointInventoryDto inven = pointInventoryDao.selectOneNumber(inventoryNo);
        if (inven == null) throw new RuntimeException("아이템이 없습니다.");
        if (!inven.getPointInventoryMemberId().equals(loginId)) throw new RuntimeException("본인 아이템만 삭제할 수 있습니다.");

        // 아이템 정보 조회 (로그용)
        PointItemDto item = pointItemDao.selectOneNumber(inven.getPointInventoryItemNo());

        // 2. 삭제 실행
        pointInventoryDao.delete(inventoryNo);

        // 3. 내역 기록 (변동액 0)
        PointHistoryDto history = new PointHistoryDto();
        history.setPointHistoryMemberId(loginId);
        history.setPointHistoryAmount(0); // 0원 처리
        history.setPointHistoryReason("아이템 삭제(폐기): " + item.getPointItemName());
        history.setPointHistoryItemNo(item.getPointItemNo());
        pointHistoryDao.insert(history);
    }

// 트랜잭션은 서비스에서 걸어야 안전합니다.

    @Transactional
    public void useItem(String loginId, int inventoryNo, String extraValue) {
        // 1. 내 아이템인지 확인 (인벤토리 조회)
        PointInventoryDto inven = pointInventoryDao.selectOneNumber(inventoryNo);
        if (inven == null || !inven.getPointInventoryMemberId().equals(loginId)) {
            throw new RuntimeException("아이템이 존재하지 않거나 권한이 없습니다.");
        }

        // 2. 아이템 정보 조회 (유형 확인용)
        PointItemDto item = pointItemDao.selectOneNumber(inven.getPointInventoryItemNo());
        String type = item.getPointItemType();

        // 3. 변수 초기화
        int changePoint = 0; // 포인트 변화량 (기본 0)
        String historyReason = "아이템 사용: " + item.getPointItemName(); // 기본 사유

        // 4. ★ 유형별 기능 실행 (Switch Case)
        switch (type) {
            case "CHANGE_NICK": // [A] 닉네임 변경권
                if (extraValue == null || extraValue.trim().isEmpty()) {
                    throw new RuntimeException("변경할 닉네임을 입력해주세요.");
                }

                // 중복 검사
                MemberDto existMember = memberDao.selectOneByMemberNickname(extraValue);
                if (existMember != null) {
                    throw new RuntimeException("이미 사용 중인 닉네임입니다. 😢");
                }

                // 닉네임 변경 실행
                MemberDto memberNick = new MemberDto();
                memberNick.setMemberId(loginId);
                memberNick.setMemberNickname(extraValue);
                
                boolean result = memberDao.updateNickname(memberNick);
                if(!result) throw new RuntimeException("닉네임 변경에 실패했습니다.");
                
                historyReason = "닉네임 변경권 사용 (" + extraValue + ")";
                break;

            case "DECO_NICK": // [B] 닉네임 치장권
                String styleKeyword = "";
                if ("1".equals(extraValue)) styleKeyword = "무지개";
                else if ("2".equals(extraValue)) styleKeyword = "골드";
                else if ("3".equals(extraValue)) styleKeyword = "네온";
                else styleKeyword = "기본"; // 혹은 에러 처리

                // 히스토리에 '[착용] 키워드' 형식으로 저장
                PointHistoryDto equipLog = new PointHistoryDto();
                equipLog.setPointHistoryMemberId(loginId);
                equipLog.setPointHistoryAmount(0);
                equipLog.setPointHistoryReason("[착용] " + styleKeyword + " (" + item.getPointItemName() + ")");
                equipLog.setPointHistoryItemNo(item.getPointItemNo());
                
                pointHistoryDao.insertHistory(equipLog);
                // 소모품이므로 break하여 아래에서 delete 실행
                break;

            case "ICON_GACHA": // [C] 아이콘 뽑기권
                // 뽑기 로직은 프론트에서 /icon/draw API를 따로 호출하여 처리함.
                // 여기서는 '티켓 사용(소모)'에 대한 로그만 남김.
                historyReason = "아이콘 뽑기 티켓 사용";
                // break하여 아래에서 delete 실행 (티켓 삭제)
                break;

            case "VOUCHER": // [D] 포인트 충전권
                changePoint = item.getPointItemPrice();
                historyReason = "포인트 상품권 사용 [" + item.getPointItemName() + "]";
                break;

            case "RANDOM_POINT": // [E] 랜덤 박스
                int min = 100;
                int max = 2000;
                changePoint = (int)(Math.random() * (max - min + 1)) + min;
                historyReason = "랜덤박스 당첨 [" + item.getPointItemName() + "]";
                break;
            
            case "LEVEL_UP": 
                // memberDao.levelUp(loginId); 
                break;

            default:
                break;
        }

        // 5. 포인트 변화가 있다면 반영 (VOUCHER, RANDOM 해당)
        if (changePoint != 0) {
            MemberDto member = memberDao.selectOne(loginId);
            member.setMemberPoint(member.getMemberPoint() + changePoint);
            memberDao.updatePoint(member);
        }

        // 6. 아이템 소모 (인벤토리에서 삭제)
        // ★ 중요: DECO_NICK, ICON_GACHA 등 소모품들은 모두 여기서 삭제됩니다.
        pointInventoryDao.delete(inventoryNo);

        // 7. 내역 기록 (통합 처리)
        // (DECO_NICK은 위에서 별도 로그를 남겼지만, "사용했다"는 사실 자체를 남기려면 아래도 실행. 
        //  중복 로그가 싫다면 if문으로 제외 가능)
        if (!type.equals("DECO_NICK")) { 
            PointHistoryDto history = new PointHistoryDto();
            history.setPointHistoryMemberId(loginId);
            history.setPointHistoryAmount(changePoint);
            history.setPointHistoryReason(historyReason);
            history.setPointHistoryItemNo(item.getPointItemNo());
            
            pointHistoryDao.insertHistory(history); 
        }
    }
    @Transactional
    public boolean toggleWish(String loginId, int itemNo) {
        // DAO에 전달할 VO 생성
        PointItemWishVO vo = PointItemWishVO.builder()
                            .memberId(loginId)
                            .itemNo(itemNo)
                            .build();

        // 찜 여부 확인
        int count = pointWishlistDao.checkWish(vo); 
        
        if (count > 0) {
            pointWishlistDao.delete(vo); // 이미 찜했으면 삭제
            return false;
        } else {
            pointWishlistDao.insert(vo); // 찜하지 않았으면 추가
            return true;
        }
    }

    // 내 찜 아이템 번호 리스트 조회
    public List<Integer> getMyWishItemNos(String loginId) {
        return pointWishlistDao.selectMyWishItemNos(loginId);
    }
 // 내 찜 목록 전체 조회
    public List<PointWishlistDto> getMyWishlist(String loginId) {
        return pointWishlistDao.selectMyWishlist(loginId);
    }
    //찜목록 삭제
    @Transactional
    public void deleteWish(String loginId, int itemNo) {
        // ★ [디버깅용 로그 추가] 콘솔창에 이 값이 찍히는지 확인하세요!
        System.out.println(">>> 찜 삭제 요청 도착!");
        System.out.println("요청자(ID): " + loginId);
        System.out.println("지울 상품번호(ItemNo): " + itemNo);

        // VO 생성
        PointItemWishVO vo = PointItemWishVO.builder()
                            .memberId(loginId)
                            .itemNo(itemNo)
                            .build();
        
        // 삭제 실행 (이게 실행돼도 조건 안맞으면 0개 삭제됨)
        pointWishlistDao.delete(vo);
        
        System.out.println(">>> 삭제 쿼리 실행 완료");
    }
    
    @Transactional
    public void addAttendancePoint(String loginId, int amount, String memo) {

        // 1) 현재 회원 정보 조회
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null) {
            throw new IllegalStateException("회원이 존재하지 않습니다: " + loginId);
        }

        // 2) 현재 포인트 + 지급 포인트 계산
        int newPoint = member.getMemberPoint() + amount;
        member.setMemberPoint(newPoint);

        // 3) 업데이트
        boolean result = memberDao.updatePoint(member);
        if (!result) {
            throw new IllegalStateException("포인트 업데이트 실패: " + loginId);
        }

        // 4) 포인트 히스토리 기록 (있다면)
        PointHistoryDto dto = PointHistoryDto.builder()
                .pointHistoryMemberId(loginId)
                .pointHistoryAmount(amount)
                .pointHistoryReason(memo)
                .build();
        pointHistoryDao.insertHistory(dto);

        System.out.println(loginId + "님에게 " + amount + "포인트 지급 완료 ▶ 현재 포인트: " + newPoint);
    }
 
    
    @Transactional
    public void donatePoints(String loginId, String targetId, int amount) {

        // 받는 사람 확인
        MemberDto receiver = memberDao.selectOne(targetId);
        if (receiver == null) {
            throw new IllegalArgumentException("받는 회원이 존재하지 않습니다.");
        }

        // 보내는 사람 정보
        MemberDto sender = memberDao.selectOne(loginId);
        if (sender.getMemberPoint() < amount) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        // 변경할 값 계산
        int senderNewPoint = sender.getMemberPoint() - amount;
        int receiverNewPoint = receiver.getMemberPoint() + amount;

        // 보내는 사람 포인트 수정
        MemberDto sendUpdate = new MemberDto();
        sendUpdate.setMemberId(loginId);
        sendUpdate.setMemberPoint(senderNewPoint);
        memberDao.updatePoint(sendUpdate);

        // 받는 사람 포인트 수정
        MemberDto recvUpdate = new MemberDto();
        recvUpdate.setMemberId(targetId);
        recvUpdate.setMemberPoint(receiverNewPoint);
        memberDao.updatePoint(recvUpdate);

        // 히스토리 기록 (보낸 사람)
        pointHistoryDao.insertHistory((PointHistoryDto.builder()
                .pointHistoryMemberId(loginId)
                .pointHistoryAmount(-amount)
                .pointHistoryReason("포인트 후원 (" + targetId + "님에게)")
                .build()));

        // 히스토리 기록 (받은 사람)
        pointHistoryDao.insertHistory((PointHistoryDto.builder()
                .pointHistoryMemberId(targetId)
                .pointHistoryAmount(amount)
                .pointHistoryReason("포인트 후원 받음 (" + loginId + "님으로부터)")
                .build()));
    }
    
 // -------------------------------------------------------------------------
    // [추가 기능] 룰렛 페이지 전용: '룰렛 이용권' 사용 (수정 없이 바로 사용 가능)
    // -------------------------------------------------------------------------
    @Transactional
    public void useRouletteTicket(String loginId) {
        
        // 1. 룰렛 이용권의 아이템 번호 (DB에 저장된 번호로 수정하세요. 예: 101)
        final int ROULETTE_TICKET_ITEM_NO = 6; 

        // 2. 내 인벤토리 목록을 전부 가져옵니다. (DAO에 있는 메서드 활용)
        List<PointInventoryDto> myInventoryList = pointInventoryDao.selectListByMemberId(loginId);
        
        // 3. 목록 중에서 '룰렛 이용권'을 찾습니다.
        PointInventoryDto targetTicket = null;
        
        for (PointInventoryDto item : myInventoryList) {
            // 가져온 아이템 번호가 룰렛 이용권 번호와 같다면?
            if (item.getPointInventoryItemNo() == ROULETTE_TICKET_ITEM_NO) {
                targetTicket = item;
                break; // 찾았으니 반복문 종료
            }
        }

        // 4. 아이템이 없으면 에러 처리
        if (targetTicket == null) {
            throw new RuntimeException("사용할 수 있는 룰렛 이용권이 없습니다.");
        }

        // 5. 아이템 삭제 (찾아낸 인벤토리 고유 번호로 삭제)
        pointInventoryDao.delete(targetTicket.getPointInventoryNo());

        // 6. 사용 기록 남기기
        PointHistoryDto history = new PointHistoryDto();
        history.setPointHistoryMemberId(loginId);
        history.setPointHistoryAmount(0); // 포인트 차감 없음
        history.setPointHistoryReason("룰렛 이용권 사용"); // 사유
        history.setPointHistoryItemNo(ROULETTE_TICKET_ITEM_NO);
        
        pointHistoryDao.insertHistory(history);

        System.out.println("룰렛 이용권 사용 완료! (인벤토리 번호: " + targetTicket.getPointInventoryNo() + ")");
    }
    public String getUserNickStyle(String loginId) {
        // 1. 가장 최근 히스토리 조회
        String reason = pointHistoryDao.selectCurrentNickStyle(loginId);
        
        // 2. 기록이 없으면 기본값
        if (reason == null) return "";
        
        // 3. 기록 내용(reason)을 보고 스타일 변환
        // (예: "아이템 사용: 무지개 닉네임" -> "nick-rainbow")
        if (reason.contains("무지개")) return "nick-rainbow";
        if (reason.contains("골드")) return "nick-gold";
        if (reason.contains("네온")) return "nick-neon";
        
        return "";
    }
 // [내 정보 조회] 포인트, 레벨, 치장스타일 포함
    public MemberPointVO getMyPointInfo(String loginId) {
        
        // 1. 회원 정보 조회
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null) return null;

        // 2. 아이콘 이미지 조회 (이제 String으로 잘 받아옵니다!)
        String equippedIconSrc = memberIconDao.selectEquippedIconSrc(loginId);
        
        // null이면 기본 이미지
        if (equippedIconSrc == null) {
            equippedIconSrc = "https://i.postimg.cc/Wb3VBy9v/null.png"; 
        }
  
        // 3. 닉네임 스타일
        String style = "";
        if ("관리자".equals(member.getMemberLevel())) {
            style = "nick-admin"; 
        } else {
            String activeItem = pointHistoryDao.selectCurrentNickStyle(loginId); 
            if (activeItem != null) {
                if (activeItem.contains("무지개")) style = "nick-rainbow";
                else if (activeItem.contains("골드")) style = "nick-gold";
                else if (activeItem.contains("네온")) style = "nick-neon";
            }
        }

        // 4. 반환
        return MemberPointVO.builder()
                .memberId(member.getMemberId())
                .nickname(member.getMemberNickname())
                .point(member.getMemberPoint())
                .level(member.getMemberLevel())
                .nickStyle(style)
                .iconSrc(equippedIconSrc) 
                .build();
    }
@Transactional
public void unequipNickStyle(String loginId) {
    // [착용] 해제 로그를 남김으로써, selectCurrentNickStyle 조회 시 
    // 스타일이 없는 로그가 최신이 되도록 함
    PointHistoryDto history = new PointHistoryDto();
    history.setPointHistoryMemberId(loginId);
    history.setPointHistoryAmount(0);
    history.setPointHistoryReason("[착용] 해제 (기본 닉네임 복귀)");
    history.setPointHistoryItemNo(0); // 아이템 번호 없음
    
    pointHistoryDao.insertHistory(history);
}

public PointHistoryPageVO getHistoryList(String loginId, int page, String type) {
    int size = 10;
    
    // 1. 개수 조회 시 type 전달
    int totalCount = pointHistoryDao.countHistory(loginId, type);
    int totalPage = (totalCount + size - 1) / size;
    
    int startRow = (page - 1) * size + 1;
    int endRow = page * size;

    // 2. 목록 조회 시 type 전달
    List<PointHistoryDto> list = pointHistoryDao.selectListByMemberIdPaging(loginId, startRow, endRow, type);
    
    return PointHistoryPageVO.builder()
            .list(list)
            .totalPage(totalPage)
            .currentPage(page)
            .totalCount(totalCount)
            .build();
}

}


