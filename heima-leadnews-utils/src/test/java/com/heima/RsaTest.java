package com.heima;

import com.heima.utils.common.RsaUtils;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaTest {
    public static final String publicKeyPath = "D:\\A\\RsaKey\\rsa-key.public";
    public static final String privateKeyPath = "D:\\A\\RsaKey\\rsa-key.private";


    /**
     * 参数一：公钥文件路径
     * 参数二：私钥文件路径
     * 参数三：密钥
     * 参数四：文件大小
     */
    @Test
    public void testGen() throws Exception {
        RsaUtils.generateKey(publicKeyPath, privateKeyPath, "12345", 1024);

    }

    /**
     * 读取公钥
     */
    @Test
    public void testGetPublicKey() throws Exception {

        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);
        System.out.println("publicKey = " + publicKey);


    }

    /**
     * 读取私钥
     */
    @Test
    public void testGetPrivateKey() throws Exception {

        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        System.out.println("privateKey = " + privateKey);

    }




}

