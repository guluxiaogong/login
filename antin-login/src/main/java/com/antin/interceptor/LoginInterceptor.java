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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/29.
 */
public class LoginInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Autowired
    private LoginHelper loginHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        if (loginHelper.validateTokenAndAuto(request, response)) //验证令牌和验证自动登录标识
            return true;

        //判断是否是ajax请求（如果是ajax请求返回验证失败信息，不能直接返回页面，而是由js跳转）
        if ("XMLHttpRequest".equals(request.getHeader("x-requested-with")))
            //response.sendError(400);// 400 状态表示请求格式错误，服务器没有理解请求，此处返回400状态表示未登录时服务器拒绝此ajax请求
            response.getWriter().write("{\\\"code\\\":\\\"0\\\"}");
        else
            response.sendRedirect(redirectUrl(request)); //令牌存在或无效且自动登录标识不存在或无效跳转到登录页面
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
    private String redirectUrl(HttpServletRequest request) throws UnsupportedEncodingException {
        //将所有请求参数重新拼接成queryString
        StringBuilder builder = new StringBuilder();
        // ? a= 1&a=2&b=xx [1,2][] ?a=1&a=2&b=xxx
        Enumeration<String> paraNames = request.getParameterNames();//不管是get还是post请求都有效，request.getQueryString()只是get请求
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
        String qstr = builder.toString();

        String backUrl = request.getRequestURL() + qstr; // 回调url
        logger.info("回调url={}", backUrl);
        return request.getContextPath() + "/login?backUrl=" + URLEncoder.encode(backUrl, "utf-8");
    }

}
