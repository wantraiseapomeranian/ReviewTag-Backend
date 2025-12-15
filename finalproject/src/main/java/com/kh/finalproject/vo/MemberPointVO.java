package com.kh.finalproject.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MemberPointVO {
    private String memberId;
    private String nickname;
    private int point;
    private String level;     // 회원 등급
    private String nickStyle; // ★ 핵심: 치장용 CSS 클래스명 (예: "nick-rainbow")
    private String iconSrc;
}