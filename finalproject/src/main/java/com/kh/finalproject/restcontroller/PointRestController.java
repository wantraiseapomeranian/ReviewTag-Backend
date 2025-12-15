package com.kh.finalproject.restcontroller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kh.finalproject.dto.IconDto;
import com.kh.finalproject.dto.MemberIconDto;
import com.kh.finalproject.service.IconService;
import com.kh.finalproject.service.PointService;
import com.kh.finalproject.vo.IconPageVO;
import com.kh.finalproject.vo.PointDonateVO;
import com.kh.finalproject.vo.PointHistoryPageVO;
import com.kh.finalproject.vo.PointUseVO;

@RestController
@RequestMapping("/point") // ★ 공통 주소: 프론트에서 /point/... 로 호출
@CrossOrigin
public class PointRestController {

    @Autowired private PointService pointService;
    @Autowired private IconService iconService; // 아이콘 관련 서비스

    // =============================================================
    // [1] 포인트 이용 내역 & 후원
    // =============================================================

    // 이용 내역 조회 (페이징 + 필터링)
    // URL: /point/history?page=1&type=all
    @GetMapping("/history")
    public PointHistoryPageVO history(
            @RequestAttribute(value="loginId", required=false) String loginId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "all") String type) {
        
        if(loginId == null) return null;
        return pointService.getHistoryList(loginId, page, type);
    }

    // 포인트 선물하기
    // URL: /point/donate
    @PostMapping("/donate")
    public String donatePoints(
            @RequestAttribute(value="loginId", required=false) String loginId,
            @RequestBody PointDonateVO vo) {
        
        if (loginId == null) return "fail:로그인이 필요합니다.";
        if (loginId.equals(vo.getTargetId())) return "fail:자신에게는 보낼 수 없습니다.";
        if (vo.getAmount() <= 0) return "fail:올바른 금액을 입력하세요.";
        
        try {
            pointService.donatePoints(loginId, vo.getTargetId(), vo.getAmount());
            return "success";
        } catch(IllegalStateException e) {
            return "fail:" + e.getMessage();
        } catch(Exception e) {
            e.printStackTrace();
            return "fail:서버 오류";
        }
    }

    // =============================================================
    // [2] 아이콘 가챠 (뽑기) & 내 아이콘 조회
    // =============================================================

    // 아이콘 뽑기 실행
    // URL: /point/icon/draw
    @PostMapping("/icon/draw")
    public IconDto drawIcon(
            @RequestAttribute(value="loginId", required=false) String loginId,
            @RequestBody PointUseVO vo) { // ★ 인벤토리 번호를 받기 위해 VO 사용
        
        if(loginId == null) throw new RuntimeException("로그인 필요");
        
        // 서비스로 티켓 번호(inventoryNo) 전달
        return iconService.drawRandomIcon(loginId, vo.getInventoryNo());
    }

    // 내 아이콘 보관함 조회
    // URL: /point/icon/my
    @GetMapping("/icon/my")
    public List<MemberIconDto> myIcons(@RequestAttribute(value="loginId", required=false) String loginId) {
        if(loginId == null) return List.of();
        return iconService.getMyIcons(loginId);
    }

    // =============================================================
    // [3] 아이콘 관리자 기능 (등록/수정/삭제/목록)
    // =============================================================

    // 관리자용 아이콘 전체 목록 조회
    // URL: /point/icon/admin/list
 // (수정) 페이지와 타입(필터)을 받아서 해당 부분만 줌 -> 페이징 됨!
    @GetMapping("/icon/admin/list")
    public IconPageVO adminIconList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "ALL") String type) {
        
        return iconService.getIconList(page, type);
    }
    // 아이콘 등록
    // URL: /point/icon/admin/add
    @PostMapping("/icon/admin/add")
    public String addIcon(@RequestBody IconDto dto) {
        try {
            iconService.addIcon(dto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 아이콘 수정
    // URL: /point/icon/admin/edit
    @PostMapping("/icon/admin/edit")
    public String editIcon(@RequestBody IconDto dto) {
        try {
            iconService.editIcon(dto);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 아이콘 삭제
    // URL: /point/icon/admin/delete/{iconId}
    @DeleteMapping("/icon/admin/delete/{iconId}")
    public String deleteIcon(@PathVariable int iconId) {
        try {
            iconService.removeIcon(iconId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
    @PostMapping("/icon/equip")
    public String equipIcon(
            @RequestAttribute("loginId") String loginId, 
            @RequestBody IconDto dto) { // 프론트에서 { iconId: 10 } 보냄
        try {
            iconService.equipIcon(loginId, dto.getIconId());
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    // 아이콘 해제
    // URL: /point/icon/unequip
    @PostMapping("/icon/unequip")
    public String unequipIcon(@RequestAttribute("loginId") String loginId) {
        try {
            iconService.unequipIcon(loginId);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
