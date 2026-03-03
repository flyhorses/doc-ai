package org.example.modules.document.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.common.result.Result;
import org.example.modules.document.service.DocumentSkillService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "文档管理", description = "用来进行文档上传")
@RestController
@RequestMapping("/api/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentSkillService documentSkillService;

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<Map<String,Object>> upload(@RequestParam("file") MultipartFile file,
                                             @RequestParam("userId") Long userId )
    {
        return Result.success(documentSkillService.storeAndCheckDuplicateFile(file, userId));
    }


}
