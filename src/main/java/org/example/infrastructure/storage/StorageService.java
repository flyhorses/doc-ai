package org.example.infrastructure.storage;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

public interface StorageService {

    String store(InputStream inputStream,String fileName, Long userId, String md5);

    File getFile(String storagePath);
}
