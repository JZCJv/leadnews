package com.heima;

import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.Payload;
import com.heima.utils.common.RsaUtils;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtTest {

    public static final String publicKeyPath = "D:\\A\\RsaKey\\rsa-key.public";
    public static final String privateKeyPath = "D:\\A\\RsaKey\\rsa-key.private";


    /**
     * 参数一：登录用户信息
     * 参数二：私钥key
     * 参数三：过期时间（分）
     */
    @Test
    public void  testGenToken() throws Exception {
        User loginUser = new User("CJZ",1);
        PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        //在几分钟内生成令牌过期
        String token = JwtUtils.generateTokenExpireInMinutes(loginUser, privateKey, 1);
        System.out.println("token = " + token);
    }

    /**
     * 验证token
     */
    @Test
    public void verifyToken() throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);


        /**
         * 参数一：token
         * 参数二：公钥key
         * 参数三：登录用户类型
         */
        String token ="eyJhbGciOiJSUzI1NiJ9.eyJ1c2VyIjoie1wibmFtZVwiOlwiQ0paXCIsXCJpZFwiOjF9IiwianRpIjoiTUdJM1lXUTJNRGd0T1dJMVlTMDBPVE13TFdJME4yUXRNemsyWVRRNE16UmlPRGMzIiwiZXhwIjoxNjYxNzY3NDc4fQ.Rp6pMvRZLbRZ5jeYNfyZSDOPFcyl5YEbu-MuJwiQAKgAqK75I6AlfAfRBLm6DBSrDN3i-TVDvIbIKkTZjK6P1w3vuDV9Dia6cWu9MBV0vLVTgDuV6kQZRtKqCvBM5a4Turho1gxA9VwRuZWatSpIuPDWqfUUi9kEMBZw_YWD-s3zQ__kJ1IanQcFbPSDVkmvYRhKFDHMHXwuJI2Tq2R5Q4RT1-iYbJXlM-GIhIY2uriT5RNb80MfHqbVELRU_X49aagRH0YAKGPG3lNiYyfiWZ1hWZyJPvFV2keQ0IpN-mwjY2wVSoAtw5xpV-eEBGRHMX5GMwzHGc_CBZVBynGVDQ";
        try {
            Payload<User> payload = JwtUtils.getInfoFromToken(token, publicKey, User.class);
            System.out.println("已经登录");
            User loginUser = payload.getInfo();
            System.out.println("loginUser = " + loginUser);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("登录失败");
        }
    }



}
