package com.kh.finalproject.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kh.finalproject.dto.PointHistoryDto;

@Repository
public class PointHistoryDao {

    @Autowired
    private SqlSession sqlSession;

    /* 1. 등록 C */
    public int insert(PointHistoryDto pointHistoryDto) { 
        return sqlSession.insert("pointhistory.insert", pointHistoryDto);
    }
    
    public int insertHistory(PointHistoryDto pointHistoryDto) { 
        return sqlSession.insert("pointhistory.inserthistory", pointHistoryDto);
    }
    /* 2. 수정 U */
    public boolean update(PointHistoryDto pointHistoryDto) {
        return sqlSession.update("pointhistory.update", pointHistoryDto) > 0;
    }

    /* 3.(1) 목록 출력 R */
    public List<PointHistoryDto> selectListByMemberId(String memberId) {
        return sqlSession.selectList("pointhistory.selectListByMemberId", memberId);
    }

    /* 3.(2) 번호 기준 조회 R */
    public PointHistoryDto selectOneNumber(long pointHistoryNo) {
        return sqlSession.selectOne("pointhistory.selectOneNumber", pointHistoryNo);
    }

    /* 4. 삭제 D */
    public boolean delete(long pointHistoryNo) {
        return sqlSession.delete("pointhistory.delete", pointHistoryNo) > 0;
    }
 // PointHistoryDao.java

    public String selectCurrentNickStyle(String memberId) {
        return sqlSession.selectOne("pointhistory.selectCurrentNickStyle", memberId);
    }

 // 1. 전체 개수 조회 (필터 포함)
    public int countHistory(String memberId, String type) {
        // [수정] 파라미터가 2개 이상이므로 Map에 담아야 합니다.
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("type", type);
        
        return sqlSession.selectOne("pointhistory.countHistory", params);
    }

    // 2. 페이징 목록 조회 (필터 포함)
    public List<PointHistoryDto> selectListByMemberIdPaging(String memberId, int startRow, int endRow, String type) {
        
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("startRow", startRow);
        params.put("endRow", endRow);
        params.put("type", type); // ★ [추가] 필터 조건(type)도 맵에 추가
        
        return sqlSession.selectList("pointhistory.selectListByMemberIdPaging", params);
    }

}