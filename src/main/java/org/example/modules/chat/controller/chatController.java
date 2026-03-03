package org.example.modules.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.common.assistant.Assistant;
import org.example.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "聊天", description = "用来和AI进行聊天交互")
@RestController
@RequestMapping("/api/chat")
public class chatController {
    @Autowired
    private Assistant assistant;
    @PostMapping()
    public Result<String> chat(@RequestParam("userId") Long userId, @RequestParam("message") String message)
    {
        String reply = assistant.chat(userId, message);
        return Result.success(reply);
    }

}
