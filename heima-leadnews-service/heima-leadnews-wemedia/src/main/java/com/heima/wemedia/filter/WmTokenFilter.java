package com.heima.wemedia.filter;

import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.ThreadLocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "wmTokenFilter", urlPatterns = "/*")
public class WmTokenFilter extends GenericFilter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //类型转换
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求头的userId
        String userId = request.getHeader("userId");

        //把用户信息存入ThreadLocal
        if (StringUtils.isNotEmpty(userId)) {

            WmUser user = new WmUser();
            user.setId(Integer.parseInt(userId));
            ThreadLocalUtils.set(user);
        }

        try {
            //放行
            filterChain.doFilter(request, response);
        } finally {
            //不论业务执行成功与否，都必须把数据从ThreadLocal移除
            ThreadLocalUtils.remove();
        }


    }
}
