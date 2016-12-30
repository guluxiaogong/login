package com.antin.helper;

import com.antin.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/12/30.
 */
@Component
public class LoginHelper {
    @Autowired
    private Config config;

    /**
     * 验证令牌和自动登录识
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public boolean validateTokenAndAuto(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (validateToken(request, response)) //验证令牌
            return true;
        else if (validateAuto(request, response)) //验证自动登录标识
            return true;

        return false;
    }

    /**
     * 验证令牌
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public boolean validateToken(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //验证令牌有效性
        String token = CookieUtil.getCookie("token", request);
        if (token != null) {  // 令牌存在
            LoginUser loginUser = TokenManager.validate(token);
            if (loginUser != null) {  //令牌有效
                TokenManager.updateExpired(token);//更新最近访问时间
                return true;
            }
        }
        return false;
    }

    /**
     * 验证自动登录标识
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public boolean validateAuto(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //获取自动登录标识存在
        String auto = CookieUtil.getCookie("auto", request);
        if (auto != null) {
            //验证自动标识是否有效
            LoginUser loginUser = config.getAuthenticationHandler().validateAutoToken(auto);
            if (loginUser != null) {
                authSuccess(response, loginUser, true);//自动登录标识有效，生成令牌
                return true;
            }
        }
        return false;
    }

    // 授权成功后的操作
    private String authSuccess(HttpServletResponse response, LoginUser loginUser, Boolean rememberMe) throws Exception {
        // 生成令牌
        String token = StringUtil.uniqueKey();
        // 生成自动登录标识
        if (rememberMe != null && rememberMe) {
            String auto = config.getAuthenticationHandler().createAutoToken(loginUser);
            CookieUtil.setCookie("auto", auto, config.getAutoLoginExpDays() * 24 * 60 * 60, config.isSecureMode(), response, null);
        }
        // 存入Map
        TokenManager.addToken(token, loginUser);
        // 写 Cookie
        Cookie cookie = new Cookie("token", token);

        // 是否仅https模式，如果是，设置cookie secure为true
        if (config.isSecureMode()) {
            cookie.setSecure(true);
        }
        response.addCookie(cookie);
        return token;
    }
}