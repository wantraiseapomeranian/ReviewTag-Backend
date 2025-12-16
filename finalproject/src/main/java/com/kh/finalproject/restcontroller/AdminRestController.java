package com.kh.finalproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dao.MemberTokenDao;
import com.kh.finalproject.dto.BoardDto;
import com.kh.finalproject.dto.MemberDto;
import com.kh.finalproject.dto.QuizDto;
import com.kh.finalproject.error.TargetNotfoundException;
import com.kh.finalproject.service.AdminService;
import com.kh.finalproject.service.QuizService;
import com.kh.finalproject.service.TokenService;
import com.kh.finalproject.vo.PageResponseVO;
import com.kh.finalproject.vo.PageVO;
import com.kh.finalproject.vo.QuizReportDetailVO;
import com.kh.finalproject.vo.QuizReportStatsVO;
import com.kh.finalproject.vo.TokenVO;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminRestController {
	
	@Autowired
	private QuizService quizService;
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private MemberDao memberDao;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private MemberTokenDao memberTokenDao;
	
	//회원 목록 조회(관리자 제외)
	@GetMapping("/members") 
	public PageResponseVO  getMemberList(
			@RequestParam int page,
			@RequestParam(required = false) String type, 
			@RequestParam(required = false) String keyword
			){
		PageVO pageVO = new PageVO();
		pageVO.setPage(page);
		
		if(type != "" && keyword != "") { // 검색일때
			int totalCount =memberDao.countSearchMember(type, keyword);
			pageVO.setTotalCount(totalCount);
			List<MemberDto> list = memberDao.selectAdminMemberList(type, keyword, pageVO);
			return new PageResponseVO<>(list, pageVO);
		} else { // 검색이 아닐때
			int totalCount =memberDao.countMember();
			pageVO.setTotalCount(totalCount);
			List<MemberDto> list = memberDao.selectListExceptAdmin(pageVO);
			return new PageResponseVO<>(list, pageVO);
		}
	}
	
	
	//회원 상세 조회
	@GetMapping("/members/{memberId}")
    public MemberDto getMemberDetail(
            @PathVariable String memberId
            ) {
        MemberDto member = memberDao.selectOne(memberId);
        
        if(member == null)
            throw new TargetNotfoundException();
        
        return member;
    }
	
	//회원등급변경
	@PatchMapping("/members/{memberId}/memberLevel")
	public void changeLevel(
	        @PathVariable String memberId,
	        @RequestParam String memberLevel) {
	    
	    MemberDto memberDto = memberDao.selectOne(memberId);
	    if(memberDto == null) throw new TargetNotfoundException();

	    memberDto.setMemberLevel(memberLevel);
	    memberDao.updateMemberLevel(memberDto);
	}
	
	//회원 강제 탈퇴
	@DeleteMapping("/members/{memberId}")
	public void delete(@PathVariable String memberId,
			@RequestHeader("Authorization") String bearerToken) {
		//계정 삭제
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto ==null) throw new TargetNotfoundException("존재하지 않는 회원입니다");
		memberDao.delete(memberId);
		//토큰 삭제
		TokenVO tokenVO = tokenService.parse(bearerToken);
		memberTokenDao.deleteByTarget(tokenVO.getLoginId());
	}
	
	//퀴즈 신고 관리 페이지
	@GetMapping("/quizzes/reports")
	public List<QuizReportStatsVO> getReportList(
			@RequestParam String status,
			@RequestAttribute TokenVO tokenVO
			) {
		
		String loginLevel = tokenVO.getLoginLevel();
		
		return adminService.getReportedQuizList(loginLevel, status);
	}
	//퀴즈 신고 상세 내역 페이지
	@GetMapping("/quizzes/{quizId}/reports")
	public List<QuizReportDetailVO> getReportDetail(
			@PathVariable int quizId,
			@RequestAttribute TokenVO tokenVO
			) {
		
		String loginLevel = tokenVO.getLoginLevel();
		
        return adminService.getReportDetails(loginLevel, quizId);
    }
	
	//퀴즈 삭제
	@DeleteMapping("/quizzes/{quizId}")
    public boolean deleteQuiz(
    		@PathVariable long quizId,
            @RequestAttribute TokenVO tokenVO
            ) {
        String loginId = tokenVO.getLoginId();
        String loginLevel = tokenVO.getLoginLevel();
		
        return quizService.deleteQuiz(quizId, loginId, loginLevel);
    }
	
	//퀴즈 상태 변경
	@PatchMapping("/quizzes/{quizId}/status/{status}")
	public boolean changeStatus(
			@PathVariable long quizId,
			@PathVariable String status,
			@RequestAttribute TokenVO tokenVO
			) {
		
		String loginId = tokenVO.getLoginId();
		String loginLevel = tokenVO.getLoginLevel();
		
		//퀴즈 상태 변경
		QuizDto quizDto = QuizDto.builder()
					.quizId(quizId)
					.quizStatus(status)
				.build();
		
		return quizService.changeQuizStatus(quizDto, loginId, loginLevel);
	}
}






