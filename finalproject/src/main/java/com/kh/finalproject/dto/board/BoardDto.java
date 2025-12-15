package com.kh.finalproject.dto.board;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @Builder @AllArgsConstructor
public class BoardDto {
	private Integer boardNo; //시퀀스로 생성(게시글 번호)
	private String boardTitle; //게시글 제목
	private String boardWriter; //글쓴이
	private Long boardContentsId; //컨텐츠 아이디
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime boardWtime, boardEtime; //작성시각, 수정시각
	private String boardText; //게시글 내용
	private Integer boardViewCount; //게시글 조회수
	private Integer boardLike, boardUnlike; //좋아요 & 싫어요
	private Integer boardReplyCount; // 댓글 수
	private String boardNotice; // 공지사항 여부 (Y || N)
}
