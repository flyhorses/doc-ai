package org.example.modules.document.service;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.example.common.constant.FileConstant;
import org.example.common.exception.ServiceException;
import org.example.common.utils.Md5Utils;
import org.example.infrastructure.storage.StorageService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentSkillService {
    private final StorageService storageService;
    private final StringRedisTemplate redisTemplate;

    @Tool("存储文件时使用的方法，存储文件并进行MD5查重，仅支持PDF/DOCX/DOC/TXT格式，必须传入userID")
    public Map<String,Object> storeAndCheckDuplicateFile(MultipartFile file, Long userId)
    {
        try {
            //校验用户Id不能为空
            if(userId == null || userId <= 0)
            {
                throw new ServiceException("userId不能为空");
            }
            //校验文件名格式必须正确
            String fileName = file.getOriginalFilename();
            if(fileName == null || !fileName.contains("."))
            {
                throw new ServiceException("文件格式不正确");
            }
            //强制校验文件格式
            String suffix = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
            List<String> suffixList = List.of(FileConstant.ALLOW_FILE_SUFFIX);
            if(!suffixList.contains(suffix)){
                throw new ServiceException("文件格式不支持");
            }
            //强制校验文件是否为空
            if(file.isEmpty())
            {
                throw new ServiceException("文件不能为空");
            }


            //计算文件md5
            String md5 = Md5Utils.md5Hex(file.getBytes());

            //查重redis
            Boolean exist = redisTemplate.opsForHash().hasKey(FileConstant.FILE_MD5_HASH_KEY, md5);
            if(exist)
            {
                throw new ServiceException("文件已存在，无需重复上传");
            }
            String storagePath = storageService.store(file.getInputStream(),fileName,userId,md5);

            redisTemplate.opsForHash().put(FileConstant.FILE_MD5_HASH_KEY,md5,storagePath);

            Map<String ,Object> result = new HashMap<>();

            result.put("success", true);
            result.put("fileMd5", md5);
            result.put("storagePath", storagePath);
            result.put("fileName",fileName);
            result.put("fileSuffix",suffix);
            result.put("fileSize",file.getSize());
            result.put("userId",userId);
            return result;
        } catch (ServiceException e) {
            throw e;
        }catch (Exception e){
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }
}
