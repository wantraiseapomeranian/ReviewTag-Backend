package com.kh.finalproject.restcontroller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.kh.finalproject.service.DailyQuestService;
import com.kh.finalproject.vo.DailyQuestVO;

@RestController
@RequestMapping("/point/quest")
@CrossOrigin
public class DailyQuestRestController {

    @Autowired private DailyQuestService dailyQuestService;

    // 1. 퀘스트 목록 조회
    @GetMapping("/list")
    public List<DailyQuestVO> list(@RequestAttribute(value="loginId", required=false) String loginId) {
        if(loginId == null) return List.of();
        return dailyQuestService.getQuestList(loginId);
    }

    // 2. 보상 받기
    @PostMapping("/claim")
    public String claim(@RequestAttribute("loginId") String loginId, @RequestBody Map<String, String> body) {
        try {
            String type = body.get("type");
            int reward = dailyQuestService.claimReward(loginId, type);
            return "success:" + reward;
        } catch (Exception e) {
            return "fail:" + e.getMessage();
        }
    }
    
    // 3. (테스트용) 퀴즈 정답 시 진행도 강제 증가
    // 실제로는 각 기능(리뷰작성, 룰렛 등)이 수행될 때 Service에서 questProgress를 호출해야 함
    @PostMapping("/progress")
    public String progress(@RequestAttribute("loginId") String loginId, @RequestBody Map<String, String> body) {
        dailyQuestService.questProgress(loginId, body.get("type"));
        return "success";
    }
}