package com.kh.finalproject.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dao.ReviewReportDao;
import com.kh.finalproject.dto.ReviewReportDto;
import com.kh.finalproject.vo.TokenVO;

@CrossOrigin
@RestController
@RequestMapping("/review/report")
public class ReviewReportRestController {
	
	@Autowired
	private ReviewReportDao reviewReportDao;
	
	@PostMapping("/")
	public void insertReviewReport(
			@RequestBody ReviewReportDto reviewReportDto,
			@RequestAttribute TokenVO tokenVO
			) {
		
		//신고자 아이디 설정
		reviewReportDto.setReviewReportMemberId(tokenVO.getLoginId());

		//신고 타입이 'OTHER'가 아닐 경우 content null 처리
		if (!"OTHER".equals(reviewReportDto.getReviewReportType())) {
			reviewReportDto.setReviewReportContent(null);
		}

		//등록 실행
		reviewReportDao.insertReviewReport(reviewReportDto);
	}
}
