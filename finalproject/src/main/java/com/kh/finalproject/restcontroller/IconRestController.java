package com.kh.finalproject.restcontroller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kh.finalproject.dto.IconDto;
import com.kh.finalproject.dto.MemberIconDto;
import com.kh.finalproject.service.IconService;
import com.kh.finalproject.vo.PointUseVO;

@RestController
@RequestMapping("/icon")
@CrossOrigin
public class IconRestController {

    @Autowired private IconService iconService;

    // [관리자] 전체 목록
    @GetMapping("/admin/list")
    public List<IconDto> adminList() {
        return iconService.getAllIcons();
    }

    // [관리자] 등록
    @PostMapping("/admin/add")
    public String add(@RequestBody IconDto dto) {
        iconService.addIcon(dto);
        return "success";
    }

    // [관리자] 수정
    @PostMapping("/admin/edit")
    public String edit(@RequestBody IconDto dto) {
        iconService.editIcon(dto);
        return "success";
    }

    // [관리자] 삭제
    @DeleteMapping("/admin/delete/{iconId}")
    public String delete(@PathVariable int iconId) {
        iconService.removeIcon(iconId);
        return "success";
    }

    // [사용자] 뽑기 실행
    @PostMapping("/icon/draw")
    public IconDto drawIcon(
            @RequestAttribute(value="loginId", required=false) String loginId,
            @RequestBody PointUseVO vo) { // ★ 인벤토리 번호를 받기 위해 VO 사용
        
        if(loginId == null) throw new RuntimeException("로그인 필요");
        
        // 서비스로 티켓 번호(inventoryNo) 전달
        return iconService.drawRandomIcon(loginId, vo.getInventoryNo());
    }
    // [사용자] 내 아이콘함 조회
    @GetMapping("/my")
    public List<MemberIconDto> myIcons(@RequestAttribute("loginId") String loginId) {
        return iconService.getMyIcons(loginId);
    }
}