package com.kh.finalproject.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberIconDto {
    private int memberIconId;
    private String memberIconMember;
    private int memberIconIcon;
    private Timestamp memberIconObtainTime;
    
    // 조인을 위한 추가 필드 (화면에 보여줄 때 필요)
    private String iconName;
    private String iconRarity;
    private String iconSrc;
    private String isEquipped;	
}

