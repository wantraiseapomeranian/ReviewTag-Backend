package com.kh.finalproject.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dao.ReviewDao;
import com.kh.finalproject.dao.ReviewLikeDao;
import com.kh.finalproject.dto.ReviewDto;
import com.kh.finalproject.error.TargetNotfoundException;

@Service
public class ReviewService {

	@Autowired
	private ReviewDao reviewDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private ReviewLikeDao reviewLikeDao;

	@Transactional
	// 리뷰 작성시 신뢰도 +1
	public void addReview(ReviewDto reviewDto) {
		reviewDao.insert(reviewDto);
		String writer = reviewDto.getReviewWriter();
		
		memberDao.updateReliability(writer, 1);
	}

	// 리뷰 삭제시 신뢰도 -1
	public void deleteReview(Long reviewContents, Long reviewNo) {
		
	    ReviewDto review = reviewDao.selectOne(reviewContents, reviewNo);
	    if (review == null) {
	        throw new TargetNotfoundException();
	    }
	    
	    String writer = review.getReviewWriter();
	    memberDao.updateReliability(writer, -1);
	}
	
	// 좋아요에 대한 신뢰도 갱신 (3좋아요 1신뢰도)
	public void LikeReviewRel(Long reviewNo) {
		String writer = reviewDao.findWriterByReviewNo(reviewNo);
		int totalLike = reviewLikeDao.countTotalLikeByWriter(writer);
		int reliability = totalLike/3;
		
		memberDao.updateReliabilitySet(writer, reliability);
	}


}
