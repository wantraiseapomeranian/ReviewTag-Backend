package com.kh.finalproject.dto.board;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @Builder @AllArgsConstructor
public class BoardResponseDto {
	private Integer boardNo;
	private String memberId;
	private String responseType; //(좋아요 || 싫어요)
}
