package org.example.infrastructure.storage;

import dev.langchain4j.agent.tool.P;
import org.example.common.constant.FileConstant;
import org.example.common.exception.ServiceException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.zip.DataFormatException;
@Service
public class LocalStorageServiceImpl implements StorageService {
    @Override
    public String store(InputStream inputStream, String fileName, Long userId, String md5) {
        try {
            //第一步，获取文件名
            String mainName = fileName.substring(0, fileName.lastIndexOf("."));
            //第二步，获取文件扩展名
            String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
            //第三步，获取时间并且格式化
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            //第四步，获取md5的前八位
            String md5Prefix = md5.substring(0, 8);
            //第五步，拼接最终的文件名
            String finalFileName = String.format("%s_%s.%s",mainName,md5Prefix,extension);

            //第六步，拼接最终路径
            Path savePath = Paths.get(FileConstant.FILE_ROOT_PATH,datePath,md5Prefix,finalFileName);

            //创建目录
            Files.createDirectories(savePath.getParent());
            //文件上传
            Files.copy(inputStream,savePath);
            //返回完整目录
            return savePath.toString();
        } catch (IOException e) {
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public File getFile(String storagePath) {
        File file = new File(storagePath);
        if (!file.exists() || !file.isFile())
        {
            throw new ServiceException("文件不存在");
        }
        return file;
    }
}
