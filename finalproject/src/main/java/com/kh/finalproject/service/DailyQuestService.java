package com.kh.finalproject.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.finalproject.configuration.DailyQuestProperties;
import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dao.PointGetQuestDao;
import com.kh.finalproject.dto.MemberDto;
import com.kh.finalproject.vo.DailyQuestVO;


@Service
public class DailyQuestService {

    @Autowired private DailyQuestProperties questProps; // yml 설정값
    @Autowired private PointGetQuestDao questDao;       // 로그 DB
    @Autowired private MemberDao memberDao;             // 포인트 지급용

    // 오늘 날짜 문자열 (YYYYMMDD)
    private String getTodayStr() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    // 1. 퀘스트 목록 조회 (설정 + DB 진행상황 병합)
    public List<DailyQuestVO> getQuestList(String memberId) {
        String today = getTodayStr();
        
        // 1) DB에서 오늘자 수행 기록 가져오기
        // [{type=ROULETTE, count=1, rewardYn=N}, ...] 형태
        List<Map<String, Object>> logs = questDao.selectTodayLogs(memberId, today);
        
        // 검색 편의를 위해 Map으로 변환 (Key: type)
        Map<String, Map<String, Object>> logMap = logs.stream()
            .collect(Collectors.toMap(
                m -> (String) m.get("type"), 
                m -> m
            ));

        List<DailyQuestVO> result = new ArrayList<>();

        // 2) yml에 정의된 퀘스트 목록을 순회하며 VO 생성
        for (DailyQuestProperties.QuestDetail q : questProps.getList()) {
            Map<String, Object> log = logMap.get(q.getType());
            
            int current = 0;
            boolean claimed = false;

            if (log != null) {
                // DB에서 가져온 값 (Oracle NUMBER는 BigDecimal로 올 수 있음 주의)
                current = Integer.parseInt(String.valueOf(log.get("count")));
                claimed = "Y".equals(log.get("rewardYn"));
            }

            boolean done = current >= q.getTarget();

            result.add(DailyQuestVO.builder()
                    .type(q.getType())
                    .title(q.getTitle())
                    .current(current)
                    .target(q.getTarget())
                    .reward(q.getReward())
                    .done(done)
                    .claimed(claimed)
                    // 아래 정보는 yml에 없으면 여기서 하드코딩하거나 프론트에서 매핑
                    .desc(getDescByType(q.getType())) 
                    .icon(getIconByType(q.getType()))
                    .action(getActionByType(q.getType()))
                    .build());
        }
        return result;
    }

    // 2. 퀘스트 진행 (Controller나 다른 Service에서 호출)
    // 예: 룰렛 돌릴 때 -> dailyQuestService.questProgress(id, "ROULETTE");
    @Transactional
    public void questProgress(String memberId, String type) {
        // 설정에 있는 퀘스트인지 확인
        boolean isValid = questProps.getList().stream()
                .anyMatch(q -> q.getType().equals(type));
        
        if(isValid) {
            questDao.upsertQuestLog(memberId, type, getTodayStr());
        }
    }

    // 3. 보상 수령
    @Transactional
    public int claimReward(String memberId, String type) {
        // 1) 해당 퀘스트 설정 찾기
        DailyQuestProperties.QuestDetail targetQuest = questProps.getList().stream()
                .filter(q -> q.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("존재하지 않는 퀘스트입니다."));

        // 2) DB 기록 조회 (목표 달성했는지, 이미 받았는지 확인)
        List<Map<String, Object>> logs = questDao.selectTodayLogs(memberId, getTodayStr());
        Map<String, Object> myLog = logs.stream()
                .filter(m -> m.get("type").equals(type))
                .findFirst()
                .orElse(null);

        if (myLog == null) throw new RuntimeException("진행 기록이 없습니다.");
        
        int current = Integer.parseInt(String.valueOf(myLog.get("count")));
        String rewardYn = (String) myLog.get("rewardYn");

        if (current < targetQuest.getTarget()) throw new RuntimeException("아직 목표를 달성하지 못했습니다.");
        if ("Y".equals(rewardYn)) throw new RuntimeException("이미 보상을 수령했습니다.");

        // 3) 보상 지급 처리 (DB 업데이트)
        int updated = questDao.updateRewardStatus(memberId, type, getTodayStr());
        
        if (updated > 0) {
            // 4) 실제 포인트 지급 (MemberDao 활용)
            memberDao.upPoint(MemberDto.builder()
                    .memberId(memberId)
                    .memberPoint(targetQuest.getReward())
                    .build());
            // TODO: PointHistoryDao에도 이력 남기기 권장
            return targetQuest.getReward();
        }
        return 0;
    }

    // --- [Helper] 프론트 표시용 하드코딩 매퍼 ---
    private String getIconByType(String type) {
        switch(type) {
            case "REVIEW": return "✍️";
            case "QUIZ": return "🧠";
            case "LIKE": return "❤️";
            case "ROULETTE": return "🎰";
            default: return "❓";
        }
    }
    private String getDescByType(String type) {
        switch(type) {
            case "REVIEW": return "한줄평 남기기";
            case "QUIZ": return "오늘의 영화 퀴즈";
            case "LIKE": return "게시글 좋아요 누르기";
            case "ROULETTE": return "룰렛 1회 돌리기";
            default: return "일일 퀘스트";
        }
    }
    private String getActionByType(String type) {
        switch(type) {
            case "REVIEW": return "link";
            case "QUIZ": return "quiz";
            case "LIKE": return "link";
            case "ROULETTE": return "roulette";
            default: return "none";
        }
    }
}