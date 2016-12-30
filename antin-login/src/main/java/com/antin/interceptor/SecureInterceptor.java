package com.antin.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.antin.helper.ConfigHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用于安全模式拦截判断的拦截器
 *
 * @author Administrator
 */
public class SecureInterceptor implements HandlerInterceptor {

    @Autowired
    private ConfigHelper configHelper;

    /**
     * 请求执行前判断是否安全模式
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object object) throws Exception {
        boolean ret = !configHelper.isSecureMode() || request.isSecure();
        if (!ret) {
            response.getWriter().write("must https");
        }
        return ret;
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0,
                                HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
        // do nothing

    }

    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
                           Object arg2, ModelAndView arg3) throws Exception {
        // do nothing

    }


}
