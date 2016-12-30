package com.antin.helper;

import com.antin.model.Credential;
import com.antin.model.LoginUser;
import com.antin.handler.IPreLoginHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/30.
 */
@Component
public class LoginHelper {
    @Autowired
    private ConfigHelper configHelper;

    /**
     * 验证令牌和自动登录标识
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
            LoginUser loginUser = configHelper.getAuthenticationHandler().validateAutoToken(auto);
            if (loginUser != null) {
                authSuccess(response, loginUser, true);//自动登录标识有效，生成令牌
                return true;
            }
        }
        return false;
    }

    /**
     * 获取鉴权对象
     *
     * @param request
     * @param session
     * @return
     */
    public Credential getCredential(HttpServletRequest request, HttpSession session) {
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
        return credential;
    }

    /**
     * 授权成功后的操作
     * (向cookie中写入令牌、自动登录标识、用户名)
     *
     * @param response
     * @param loginUser
     * @param rememberMe
     * @throws Exception
     */
    public void authSuccess(HttpServletResponse response, LoginUser loginUser, Boolean rememberMe) throws Exception {
        // 生成令牌
        String token = StringUtil.uniqueKey();
        // 生成自动登录标识
        if (rememberMe != null && rememberMe) {
            String auto = configHelper.getAuthenticationHandler().createAutoToken(loginUser);
            CookieUtil.setCookie("auto", auto, configHelper.getAutoLoginExpDays() * 24 * 60 * 60, configHelper.isSecureMode(), response, null);
        }
        // 存入Map
        TokenManager.addToken(token, loginUser);
        // 写 Cookie
        Cookie cookie = new Cookie("token", token);

        // 是否仅https模式，如果是，设置cookie secure为true
        if (configHelper.isSecureMode()) {
            cookie.setSecure(true);
        }
        response.addCookie(cookie);

        Cookie loginUserCookie = new Cookie("loginName", loginUser.toString());
        response.addCookie(loginUserCookie);

    }
}