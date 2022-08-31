package com.heima;


import com.heima.utils.common.BCrypt;
import org.junit.Test;

public class BCryptTest {


    @Test
    public void TESTMD5() {
        ////md5加密  DegestUtils：spring框架提供的工具类
    }
    //加密
    @Test
    public void testBCrypt() {
        String gensalt = BCrypt.gensalt();//这个是盐  29个字符，随机生成
        System.out.println("gensalt = " + gensalt);
        String password = BCrypt.hashpw("admin", gensalt);
        System.out.println("password = " + password);//加密后的字符串前29位就是盐
        String gensalt1 = BCrypt.gensalt();



    }

    //验证
    @Test
    public void TestPassword() {

        boolean checkpw = BCrypt.checkpw("admin", "$2a$10$3soCNnP4eUbluZblatoio..10C2Umbfa.ScQCmQXO5JcCK/3Z3dmy");
        System.out.println("checkpw = " + checkpw);
    }
}
