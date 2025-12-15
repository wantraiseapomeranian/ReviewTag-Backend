package com.kh.finalproject.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kh.finalproject.dto.MemberIconDto;

@Repository
public class MemberIconDao {

    @Autowired 
    private SqlSession sqlSession;

    // 내 보유 아이콘 목록 조회
    public List<MemberIconDto> selectMyIcons(String memberId) {
        return sqlSession.selectList("memberIcon.selectMyIcons", memberId);
    }

    // 유저가 이미 해당 아이콘을 가졌는지 확인 (중복 체크)
    public int checkUserHasIcon(String memberId, int iconId) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("iconId", iconId);
        return sqlSession.selectOne("memberIcon.checkUserHasIcon", params);
    }

    // 유저에게 아이콘 지급 (보유 목록 추가)
    public int insertMemberIcon(String memberId, int iconId) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("iconId", iconId);
        return sqlSession.insert("memberIcon.insertMemberIcon", params);
    }
    public void unequipAllIcons(String memberId) {
        sqlSession.update("memberIcon.unequipAllIcons", memberId);
    }

    // 2. 특정 아이콘 장착 (Y로 변경)
    public void equipIcon(String memberId, int iconId) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("iconId", iconId);
        sqlSession.update("memberIcon.equipIcon", params);
    }
    public String selectEquippedIconSrc(String memberId) {
        Map<String, Object> param = new HashMap<>();
        param.put("memberId", memberId);
        return sqlSession.selectOne("memberIcon.selectEquippedIconSrc", param);
    }
}