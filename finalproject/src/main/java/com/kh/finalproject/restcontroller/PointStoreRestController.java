package com.kh.finalproject.restcontroller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dao.PointHistoryDao;
import com.kh.finalproject.dao.PointInventoryDao;
import com.kh.finalproject.dao.PointItemDao;
import com.kh.finalproject.dto.MemberDto;
import com.kh.finalproject.dto.PointHistoryDto;
import com.kh.finalproject.dto.PointInventoryDto;
import com.kh.finalproject.dto.PointItemDto;
import com.kh.finalproject.dto.PointWishlistDto;
import com.kh.finalproject.service.PointService;
import com.kh.finalproject.vo.*;

@RestController
@RequestMapping("/point/store") // 기본 주소
@CrossOrigin
public class PointStoreRestController {

    @Autowired private PointService pointService;
    @Autowired private PointItemDao pointItemDao;
    @Autowired private PointInventoryDao pointInventoryDao;
    @Autowired private MemberDao memberDao;
    @Autowired private PointHistoryDao pointHistoryDao;
    // 1. 상품 목록 조회
    // [수정] "/" 대신 "" (빈 문자열)을 사용하면 슬래시 유무 상관없이 매핑됩니다.
    // URL: /point/store
    @GetMapping("/") 
    public List<PointItemDto> list() { return pointItemDao.selectList(); }

    // 2. 구매
    // URL: /point/store/buy
    @PostMapping("/buy")
    public String buy(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointBuyVO vo) {
        if(loginId == null) return "fail:로그인 정보 없음";
        try {
            pointService.purchaseItem(loginId, vo.getBuyItemNo());
            return "success";
        } catch (Exception e) { e.printStackTrace(); return "fail:" + e.getMessage(); }
    }

    // 3. 선물
    // URL: /point/store/gift
    @PostMapping("/gift")
    public String gift(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointGiftVO vo) {
        if(loginId == null) return "fail:로그인 정보 없음";
        try {
            pointService.giftItem(loginId, vo.getTargetId(), vo.getItemNo());
            return "success";
        } catch (Exception e) { e.printStackTrace(); return "fail"; }
    }

    // 4. 취소 (환불)
    // URL: /point/store/cancel
    @PostMapping("/cancel")
    public String cancel(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointCancelVO vo) {
        if(loginId == null) return "fail:로그인 정보 없음";
        try {
            pointService.cancelItem(loginId, vo.getInventoryNo());
            return "success";
        } catch (Exception e) { e.printStackTrace(); return "fail"; }
    }

    // 5. 내 보관함 조회
    // URL: /point/store/inventory/my
    @GetMapping("/inventory/my")
    public List<PointInventoryDto> myInventory(
            @RequestAttribute(value="loginId", required=false) String loginId) {
        if(loginId == null) return List.of();
        return pointInventoryDao.selectListByMemberId(loginId);
    }

    // 6. [관리자] 상품 등록
    // URL: /point/store/item/add
    @PostMapping("/item/add")
    public String addItem(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointItemDto itemDto) {
        if(loginId == null) return "fail";
        try {
            pointService.addItem(loginId, itemDto);
            return "success";
        } catch (Exception e) { e.printStackTrace(); return "fail"; }
    }
    
    // 7. [관리자] 상품 수정
    // URL: /point/store/item/edit
    @PostMapping("/item/edit")
    public String editItem(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointItemDto itemDto) {
        if(loginId == null) return "fail";
        try {
            pointService.editItem(loginId, itemDto);
            return "success";
        } catch (Exception e) { e.printStackTrace(); return "fail"; }
    }
    
    // 8. [관리자] 상품 삭제
    // URL: /point/store/item/delete
    @PostMapping("/item/delete")
    public String deleteItem(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointItemDto itemDto) {
        if(loginId == null) return "fail";
        try {
            pointService.deleteItem(loginId, itemDto.getPointItemNo());
            return "success";
        } catch (Exception e) { 
            e.printStackTrace(); 
            return "fail"; 
        }
    }
    
    // 9. 인벤토리 아이템 삭제 (버리기)
    // URL: /point/store/inventory/delete
    @PostMapping("/inventory/delete")
    public String discardItem(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointCancelVO vo) {
        if(loginId == null) return "fail";
        try {
            pointService.discardItem(loginId, (int)vo.getInventoryNo());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    
    // 10. [인벤토리] 아이템 사용
    // URL: /point/store/inventory/use
    @PostMapping("/inventory/use")
    public String useItem(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointUseVO vo // ★ 핵심: @RequestBody로 VO를 받아야 JSON이 해석됨
    ) {
        if(loginId == null) return "fail:로그인 필요";
        
        try {
            // 서비스로 값 전달
            pointService.useItem(loginId, vo.getInventoryNo(), vo.getExtraValue());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail:" + e.getMessage();
        }
    }
    
    // 11. 찜 토글
    // URL: /point/store/wish/toggle
    @PostMapping("/wish/toggle")
    public boolean toggleWish(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointItemWishVO vo) {
        if(loginId == null) return false;
        return pointService.toggleWish(loginId, vo.getItemNo());
    }

    // 12. 내가 찜한 아이템 번호 조회
    // URL: /point/store/wish/check
    @GetMapping("/wish/check")
    public List<Integer> myWishItemNos(
            @RequestAttribute(value="loginId", required=false) String loginId) {
        if(loginId == null) return List.of();
        return pointService.getMyWishItemNos(loginId);
    }

    // 13. 내 찜 목록 전체 조회
    // URL: /point/store/wish/my
    @GetMapping("/wish/my")
    public List<PointWishlistDto> myWishlist(
            @RequestAttribute(value="loginId", required=false) String loginId) {
        if(loginId == null) return List.of();
        return pointService.getMyWishlist(loginId);
    }
    
    // 14. 찜 삭제
    // URL: /point/store/wish/delete
    @PostMapping("/wish/delete")
    public void deleteWish(
            @RequestAttribute(value="loginId", required=false) String loginId, 
            @RequestBody PointItemWishVO vo) {
        if(loginId == null) return;
        pointService.deleteWish(loginId, vo.getItemNo());
    }
    
    @PostMapping("/roulette/start")
    public String startRoulette(
            @RequestAttribute(value="loginId", required=false) String loginId) {
        
        if(loginId == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        // 방금 만든 Service 메서드 호출
        pointService.useRouletteTicket(loginId);
        
        return "ok"; // 성공 시 클라이언트에 "ok" 문자열 반환
    }
    @GetMapping("/my-info")
    public MemberPointVO getMyInfo(
            @RequestAttribute(value="loginId", required=false) String loginId) {
        
        if (loginId == null) return null; // 비로그인

        // 방금 만든 서비스 메서드 호출
        return pointService.getMyPointInfo(loginId);
    }
}