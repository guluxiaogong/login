package com.antin.action;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.antin.helper.*;
import com.antin.model.Credential;
import com.antin.model.LoginUser;
import com.antin.auth.IPreLoginHandler;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginAction {

    @Autowired
    private LoginHelper loginHelper;

    @Autowired
    private ConfigHelper configHelper;

    /**
     * 主页面
     *
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return configHelper.getIndexViewName();
    }

    /**
     * 测试用
     *
     * @return
     */
    @RequestMapping("/test")
    public String test() {
        return "/html/test.html";
    }

    /**
     * 测试用
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/test1")
    public Map<String, String> test1() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "zhangsan");
        map.put("age", "15");
        return map;
    }

    /**
     * 登录入口
     *
     * @param backUrl
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login")
    public Object login(String backUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {

        //验证令牌和验证自动登录标识
        if (loginHelper.validateTokenAndAuto(request, response)) {
            response.sendRedirect("index");//直接请求/login时，且存在令牌或自动登录标识时直接重定向到到主页面
            return null;
        }

        //直接请求/login或拦截重定向请求到这直接返回登录页
        String loginVieName = configHelper.getLoginViewName();
        if (backUrl != null && !backUrl.startsWith("/login"))//带有backUrl时返回回调url，以便验证成功后直接跳转到该url
            loginVieName += "?" + backUrl;

        return loginVieName;
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
        //生成鉴权对象
        Credential credential = loginHelper.getCredential(request, session);

        //鉴权对象验证
        LoginUser loginUser = configHelper.getAuthenticationHandler().authenticate(credential);

        Map<String, String> map = new HashMap<>();
        if (loginUser != null) {//验证成功

            session.setAttribute(IPreLoginHandler.VERIFICATION_CODE, null);//验证成功后清除验证码（因为表单提交时密码是和验证码绑定加密传到后台校验，为防止密码串被截获登录，这里清除验证码）

            //生成令牌，向cookie中写入令牌、自动登录表示、用户名
            loginHelper.authSuccess(response, loginUser, rememberMe);

            map.put("code", "1");
            map.put("backUrl", backUrl);

        } else {//验证失败
            map.put("code", "0");
            map.put("errorMsg", credential.getError());
        }
        return map;
    }

    /**
     * 验证码
     *
     * @param session
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("/preLogin")
    public Object preLogin(HttpSession session) throws Exception {

        IPreLoginHandler preLoginHandler = configHelper.getPreLoginHandler();
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
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String token = CookieUtil.getCookie("token", request);

        // 清除自动登录信息
        LoginUser loginUser = TokenManager.validate(token);
        if (loginUser != null) {
            // 清除服务端自动登录状态
            configHelper.getAuthenticationHandler().clearAutoToken(loginUser);
            // 清除自动登录cookie
            CookieUtil.deleteCookie("auto", response, null);
        }

        // 移除server端token
        TokenManager.invalid(token);

        // 移除客户端token cookie
        CookieUtil.deleteCookie("token", response, null);

        response.sendRedirect("login");
    }

}
