package com.kh.finalproject.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.finalproject.dao.IconDao;
import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dao.MemberIconDao;
import com.kh.finalproject.dao.PointHistoryDao;
import com.kh.finalproject.dao.PointInventoryDao;
import com.kh.finalproject.dto.IconDto;
import com.kh.finalproject.dto.MemberDto;
import com.kh.finalproject.dto.MemberIconDto;
import com.kh.finalproject.dto.PointHistoryDto;
import com.kh.finalproject.vo.IconPageVO;

@Service
public class IconService {

    // ★ [수정] DAO 2개 주입
    @Autowired private IconDao iconDao;           // 아이콘 정보 (관리자용)
    @Autowired private MemberIconDao memberIconDao; // 보유 현황 (사용자용)
    @Autowired private PointInventoryDao pointInventoryDao;
    @Autowired private MemberDao memberDao;       // 포인트 환급용
    @Autowired private PointHistoryDao pointHistoryDao;

    // -----------------------------------------------------
    // 1. [관리자] CRUD 기능 -> IconDao 사용
    // -----------------------------------------------------
    public List<IconDto> getAllIcons() { 
        return iconDao.selectList(); 
    }
    public IconPageVO getIconList(int page, String type) {
        int size = 10; // 한 페이지에 10개씩
        
        // 1. 필터에 맞는 전체 개수 조회
        int totalCount = iconDao.countIcons(type);
        
        // 2. 전체 페이지 수 계산
        int totalPage = (totalCount + size - 1) / size;
        
        // 3. 현재 페이지에 해당하는 ROWNUM 범위 계산
        int startRow = (page - 1) * size + 1;
        int endRow = page * size;

        // 4. 데이터 조회
        List<IconDto> list = iconDao.selectListPaging(startRow, endRow, type);
        
        // 5. VO 포장
        return IconPageVO.builder()
                .list(list)
                .totalPage(totalPage)
                .currentPage(page)
                .totalCount(totalCount)
                .build();
    }
    @Transactional
    public void addIcon(IconDto dto) { 
        iconDao.insert(dto); 
    }
    
    @Transactional
    public void editIcon(IconDto dto) { 
        iconDao.update(dto); 
    }
    
    @Transactional
    public void removeIcon(int iconId) { 
        iconDao.delete(iconId); 
    }

    // -----------------------------------------------------
    // 2. [사용자] 뽑기 및 조회 -> IconDao + MemberIconDao 혼합 사용
    // -----------------------------------------------------
    
    // 내 보유 아이콘 목록
    public List<MemberIconDto> getMyIcons(String memberId) {
        // ★ [수정] MemberIconDao 사용
        return memberIconDao.selectMyIcons(memberId);
    }	

    // 대망의 [아이콘 뽑기] 로직
    @Transactional
    public IconDto drawRandomIcon(String memberId, int inventoryNo) {
        
      boolean isDeleted = pointInventoryDao.delete(inventoryNo);
     
        if (!isDeleted) {
            throw new RuntimeException("티켓을 찾을 수 없거나 이미 사용되었습니다.");
        }

        // 2. [뽑기 로직] (기존과 동일)
        List<IconDto> allIcons = iconDao.selectList();
        
        // EVENT 등급 제외 필터링
        List<IconDto> gachaPool = allIcons.stream()
                .filter(i -> !"EVENT".equalsIgnoreCase(i.getIconRarity()))
                .toList();

        if(gachaPool.isEmpty()) throw new RuntimeException("뽑기 가능한 아이콘이 없습니다.");

        // 확률 로직
        double random = Math.random() * 100; 
        String targetRarity = "COMMON";
        if (random < 0.5) targetRarity = "LEGENDARY";
        else if (random < 3.0) targetRarity = "UNIQUE";
        else if (random < 10.0) targetRarity = "EPIC";
        else if (random < 40.0) targetRarity = "RARE";

        // 등급 필터링
        String finalRarity = targetRarity;
        List<IconDto> pool = gachaPool.stream()
                .filter(i -> i.getIconRarity().equalsIgnoreCase(finalRarity))
                .toList();

        if (pool.isEmpty()) { // 꽝 방지
            pool = gachaPool.stream().filter(i -> i.getIconRarity().equalsIgnoreCase("COMMON")).toList();
        }

        // 아이콘 선택
        IconDto picked = pool.get((int)(Math.random() * pool.size()));

        // 3. [지급 로직] 중복 체크 및 지급
        int count = memberIconDao.checkUserHasIcon(memberId, picked.getIconId());
        
        if (count > 0) {
            // 중복 -> 포인트 환급 (500P)
            memberDao.updatePoint(MemberDto.builder().memberId(memberId).memberPoint(500).build()); // 포인트 증가 로직 확인 필요 (기존 MemberDao 활용)
            
            pointHistoryDao.insertHistory(PointHistoryDto.builder()
                    .pointHistoryMemberId(memberId)
                    .pointHistoryAmount(500)
                    .pointHistoryReason("아이콘 중복 환급 [" + picked.getIconName() + "]")
                    .build());
            
            picked.setIconName(picked.getIconName() + " (중복 500P 환급)");
        } else {
            // 신규 -> 지급 (MemberIconDao)
            memberIconDao.insertMemberIcon(memberId, picked.getIconId());
        }

        return picked;
    }
 // 아이콘 장착 (트랜잭션 필수)
    @Transactional
public void equipIcon(String memberId, int iconId) {

    // 1. 보유한 아이콘인지 확인
    int hasIcon = memberIconDao.checkUserHasIcon(memberId, iconId);
    if (hasIcon == 0) throw new RuntimeException("보유하지 않은 아이콘입니다.");

    // 2. 기존 장착 해제 (MEMBER_ICONS 테이블)
    memberIconDao.unequipAllIcons(memberId);

    // 3. 새 아이콘 장착
    memberIconDao.equipIcon(memberId, iconId);
}

// [해제] 모든 아이콘을 'N'으로 변경
@Transactional
public void unequipIcon(String memberId) {
    memberIconDao.unequipAllIcons(memberId);
}
}