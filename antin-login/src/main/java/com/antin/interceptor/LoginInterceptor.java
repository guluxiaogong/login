package com.antin.interceptor;

import com.antin.helper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * Created by Administrator on 2016/12/29.
 */
public class LoginInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private ConfigHelper configHelper;

    @Autowired
    private LoginHelper loginHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        if (loginHelper.validateTokenAndAuto(request, response)) //验证令牌和验证自动登录标识
            return true;

        //令牌存在或无效且自动登录标识不存在或无效跳转到登录页面
        response.sendRedirect(getLocation(request));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {


    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    /**
     * 回调url构造
     */
    private String getLocation(HttpServletRequest request) throws UnsupportedEncodingException {
        String qstr = makeQueryString(request); // 将所有请求参数重新拼接成queryString
        String backUrl = request.getRequestURL() + qstr; // 回调url
        String location = request.getContextPath() + "/login?backUrl=" + URLEncoder.encode(backUrl, "utf-8");
        return location;
    }

    /**
     * 将所有请求参数重新拼接成queryString
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    private String makeQueryString(HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        // ? a= 1&a=2&b=xx [1,2][] ?a=1&a=2&b=xxx
        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String paraName = paraNames.nextElement();
            String[] paraVals = request.getParameterValues(paraName);
            for (String paraVal : paraVals) {
                builder.append("&").append(paraName).append("=").append(URLEncoder.encode(paraVal, "utf-8"));
            }
        }

        if (builder.length() > 0) {
            builder.replace(0, 1, "?");
        }

        return builder.toString();
    }

}
