package com.antin.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.antin.helper.Config;
import com.antin.helper.CookieUtil;
import com.antin.helper.StringUtil;
import com.antin.helper.TokenManager;
import com.antin.model.Credential;
import com.antin.model.LoginUser;
import com.antin.service.IPreLoginHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @Autowired
    private Config config;

    /**
     * 登录入口
     *
     * @param request
     * @param backUrl
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, String backUrl, HttpServletResponse response) throws Exception {

        //获取自动登录标识存在
        String auto = CookieUtil.getCookie("auto", request);

        if (auto != null) {
            //验证自动标识是否有效
            LoginUser loginUser = config.getAuthenticationHandler().validateAutoToken(auto);
            if (loginUser != null) {
                authSuccess(response, loginUser, true);//自动登录标识有效，生成令牌
                return validateSuccess(backUrl, loginUser, response);
            }
        }
        return config.getLoginViewName() + "?" + backUrl;
    }

    /**
     * 登录验证
     *
     * @param backUrl
     * @param rememberMe
     * @param request
     * @param session
     * @param response
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public Object login(String backUrl, Boolean rememberMe, HttpServletRequest request, HttpSession session,
                        HttpServletResponse response) throws Exception {

        final Map<String, String[]> params = request.getParameterMap();

        //验证码
        final Object verificationCode = session.getAttribute(IPreLoginHandler.VERIFICATION_CODE);

        //鉴权对象
        Credential credential = new Credential() {
            @Override
            public String getParameter(String name) {
                String[] tmp = params.get(name);
                return tmp != null && tmp.length > 0 ? tmp[0] : null;
            }

            @Override
            public String[] getParameterValue(String name) {
                return params.get(name);
            }

            @Override
            public Object getVerificationCode() {
                return verificationCode;
            }
        };

        LoginUser loginUser = config.getAuthenticationHandler().authenticate(credential);

        Map<String, String> map = new HashMap<>();
        if (loginUser == null) {//验证失败
            map.put("code", "0");
            map.put("errorMsg", credential.getError());
            return map;
        } else {//验证成功
            authSuccess(response, loginUser, rememberMe);

            Cookie loginUserCookie = new Cookie("userName", loginUser.toString());
            response.addCookie(loginUserCookie);

            map.put("code", "1");
            map.put("backUrl", backUrl);

            return map;
        }
    }

    @ResponseBody
    @RequestMapping("/preLogin")
    public Object preLogin(HttpSession session) throws Exception {
        IPreLoginHandler preLoginHandler = config.getPreLoginHandler();
        if (preLoginHandler == null) {
            throw new Exception("没有配置preLoginHandler,无法执行预处理");
        }

        return preLoginHandler.handle(session);
    }

    /**
     * 注销
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String token = CookieUtil.getCookie("token", request);
       // String auto = CookieUtil.getCookie("auto", request);

        // 清除自动登录信息
        LoginUser loginUser = TokenManager.validate(token);
        if (loginUser != null) {
            // 清除服务端自动登录状态
            config.getAuthenticationHandler().clearAutoToken(loginUser);
            // 清除自动登录cookie
            Cookie autoCookie = new Cookie("auto", null);
            autoCookie.setMaxAge(0);
            response.addCookie(autoCookie);
        }

        // 移除server端token
        TokenManager.invalid(token);

        // 移除客户端token cookie
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);

        return config.getLoginViewName();
    }

    // 令牌验证成功或登录成功后的操作
    private String validateSuccess(String backUrl, LoginUser loginUser, HttpServletResponse response)
            throws Exception {

        Cookie loginUserCookie = new Cookie("userName", loginUser.toString());
        response.addCookie(loginUserCookie);

        if (backUrl != null) {//如果有带访问页面验证成功后就跳转到该页面
            response.sendRedirect(backUrl);
            return null;
        } else //否则跳转的主页面
            return config.getIndexViewName();


    }

    // 授权成功后的操作
    private String authSuccess(HttpServletResponse response, LoginUser loginUser, Boolean rememberMe) throws Exception {
        // 生成令牌
        String token = StringUtil.uniqueKey();
        // 生成自动登录标识
        if (rememberMe != null && rememberMe) {
            String auto = config.getAuthenticationHandler().createAutoToken(loginUser);
            setLtCookie(auto, response);
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

    // 写lt cookie
    private void setLtCookie(String auto, HttpServletResponse response) {
        Cookie autoCookie = new Cookie("auto", auto);
        autoCookie.setMaxAge(config.getAutoLoginExpDays() * 24 * 60 * 60);
        if (config.isSecureMode())
            autoCookie.setSecure(true);

        response.addCookie(autoCookie);
    }
}
