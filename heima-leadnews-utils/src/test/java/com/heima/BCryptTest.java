package com.heima;

import com.heima.utils.common.BCrypt;
import org.junit.Test;

public class BCryptTest {

    /**
     * 加密
     */
    @Test
    public void testEncode(){
        //产生随机盐
        String salt = BCrypt.gensalt();
        System.out.println(salt);
        //加密
        String pwd = BCrypt.hashpw("123456", salt);
        System.out.println(pwd);
    }

    /**
     * 验证
     */
    @Test
    public void testMatch(){
        String pwd = "$2a$10$1ZNMO89VzYrpzw2K.cF8QeJ/smPrpFG3zkYPH7b0aueoIcr205n6q";
        boolean flag = BCrypt.checkpw("12345", pwd);
        System.out.println(flag);
    }
}