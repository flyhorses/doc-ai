package org.example.modules.chat.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.agent.DocAiAgent;
import org.example.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "聊天", description = "用来和AI进行聊天交互")
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private DocAiAgent docAiAgent;

    @PostMapping
    public Result<String> chat(@RequestParam("userId") Long userId, @RequestParam("message") String message) {
        String reply = docAiAgent.chat(userId, message);
        return Result.success(reply);
    }
}
