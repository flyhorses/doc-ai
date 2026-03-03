package org.example.common.utils;
import cn.hutool.crypto.digest.DigestUtil;

import java.io.File;
import java.io.InputStream;


public class Md5Utils {
    public static String md5Hex(byte[] bytes){
        return DigestUtil.md5Hex(bytes);
    }

    public static String md5Hex(InputStream inputStream){
        return DigestUtil.md5Hex(inputStream);
    }

    public static String md5Hex(File file){
        return DigestUtil.md5Hex(file);
    }
}
