package com.antin.interceptor;

import com.antin.helper.Config;
import com.antin.helper.CookieUtil;
import com.antin.helper.StringUtil;
import com.antin.helper.TokenManager;
import com.antin.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletRequest;
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

    private String excludes;

    @Autowired
    private Config config;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
       System.out.println("-------preHandle----"+request.getRequestURI());
        // 如果是不需要拦截的请求，直接通过
        if (requestIsExclude(request))
            return true;

        //验证令牌有效性
        String token = CookieUtil.getCookie("token", request);
        if (token != null) {  // 令牌存在
            LoginUser loginUser = TokenManager.validate(token);
            if (loginUser != null)  //令牌有效
                return true;
        }

        //令牌不存在或无效跳转到登录页面
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
        String location = "/login?backUrl=" + URLEncoder.encode(backUrl, "utf-8");
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

    /**
     * 过滤不需要拦截的请求
     *
     * @param request
     * @return
     */
    private boolean requestIsExclude(ServletRequest request) {

        // 没有设定excludes时，所以经过filter的请求都需要被处理
        if (StringUtil.isEmpty(excludes))
            return false;

        // 获取去除context path后的请求路径
        String contextPath = request.getServletContext().getContextPath();
        String uri = ((HttpServletRequest) request).getRequestURI();
        uri = uri.substring(contextPath.length());

        // 正则模式匹配的uri被排除，不需要拦截
        boolean isExcluded = uri.matches(excludes);

        if (isExcluded)
            logger.debug("request path: {} is excluded!", uri);

        return isExcluded;
    }
}
