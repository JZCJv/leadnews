package com.heima.model.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    /**
     * 手机号
     */
    private  String phone;
    /**
     * 密码
     */
    private String password;
}
