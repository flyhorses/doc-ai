package org.example.infrastructure.storage;

import java.io.File;
import java.io.InputStream;

public interface StorageService {

    String store(InputStream inputStream, String fileName, Long userId, String md5);

    File getFile(String storagePath);
    
    byte[] readFile(String storagePath);
    
    String getFileName(String storagePath);
}
