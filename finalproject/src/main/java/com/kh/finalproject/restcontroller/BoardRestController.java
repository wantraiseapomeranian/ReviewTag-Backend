package com.kh.finalproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dao.BoardDao;
import com.kh.finalproject.dto.BoardDto;

@CrossOrigin
@RestController
@RequestMapping("/board")
public class BoardRestController {

	@Autowired
	private BoardDao boardDao;
	
	// 게시글 등록
	@PostMapping("/")
	public void insert(
			@RequestBody BoardDto boardDto
			) {
		boardDao.insert(boardDto);
	}
	
	//전체 조회
	@GetMapping("/")
	public List<BoardDto> selectList(){
		return boardDao.selectList();
	}
	
	// 컨텐츠별 조회
	@GetMapping("/{contentsId}")
	public List<BoardDto> selesctListByContents(@PathVariable long contentsId){
		return boardDao.selesctListByContents(contentsId);
	}
	
	//상세 조회
	@GetMapping("/{boardNo}")
	public BoardDto selectOne(@PathVariable int boardNo) {
		return boardDao.selectOne(boardNo);
	}
	
	
	
}
