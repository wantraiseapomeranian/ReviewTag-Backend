package com.kh.finalproject.service;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.finalproject.configuration.JwtProperties;
import com.kh.finalproject.dao.MemberTokenDao;
import com.kh.finalproject.dto.MemberDto;
import com.kh.finalproject.dto.MemberTokenDto;
import com.kh.finalproject.vo.TokenVO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

	@Autowired
	private JwtProperties  jwtProperties;
	@Autowired
	private MemberTokenDao memberTokenDao;
	
	/// AccessToken 생성 ////////////////////////////////////////////
	public String generateAccessToken(MemberDto memberDto) {
		String keyStr = jwtProperties.getKeyStr();
		SecretKey key = Keys.hmacShaKeyFor(keyStr.getBytes(StandardCharsets.UTF_8));
	
		// 만료시간 설정
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime(); // 현재시각
		calendar.add(Calendar.MINUTE, jwtProperties.getExpiration());
		Date expire = calendar.getTime(); // 만료시각
		
		//Jwt 토큰 생성
		return Jwts.builder()
				.signWith(key)
				.expiration(expire)
				.issuedAt(now)
				.issuer(jwtProperties.getIssuer()) // 발행자
				.claim("loginId", memberDto.getMemberId())
				.claim("loginLevel", memberDto.getMemberLevel())
				//.claim("loginPoint", memberDto.getMemberPoint())
			.compact();
	}
	public String generateAccessToken(TokenVO tokenVO) {
		return generateAccessToken(MemberDto.builder()
				.memberId(tokenVO.getLoginId())
				.memberLevel(tokenVO.getLoginLevel())
				.build());
	}
	

	/// RefreshToken 생성 ////////////////////////////////////////////
	public String generateRefreshToken(MemberDto memberDto) {
		String keyStr = jwtProperties.getKeyStr();
		SecretKey key = Keys.hmacShaKeyFor(keyStr.getBytes(StandardCharsets.UTF_8));
	
		// 만료시간 설정
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		calendar.add(Calendar.MINUTE, jwtProperties.getRefreshExpiration());
		Date expire = calendar.getTime(); // 만료시각
		
		//Jwt 토큰 생성
		String token = Jwts.builder()
				.signWith(key)
				.expiration(expire)
				.issuedAt(now)
				.issuer(jwtProperties.getIssuer()) // 발행자
				.claim("loginId", memberDto.getMemberId())
				.claim("loginLevel", memberDto.getMemberLevel())
				//.claim("loginPoint", memberDto.getMemberPoint())
			.compact();
		
		// 같은 아이디로 저장된 발행 내역을 모두 삭제
		memberTokenDao.deleteByTarget(memberDto.getMemberId());
		// DB 저장 (액세스 토큰과 달라지는 작업)
		memberTokenDao.insert(MemberTokenDto.builder()
				.memberTokenTarget(memberDto.getMemberId())
				.memberTokenValue(token)
			.build());
		
		return token;
	}
	
	public String generateRefreshToken(TokenVO tokenVO) {
		return generateAccessToken(MemberDto.builder()
				.memberId(tokenVO.getLoginId())
				.memberLevel(tokenVO.getLoginLevel())
				.build());
	}
	
	/// parse ////////////////////////////////////////////
	
	
	
	/// 토큰 만료까지 남은 시간 구하기 ////////////////////////////////////////////
	
	
	// checkRefresh Token
	
	
}
