package com.antin.auth;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * 登录页前置处理器
 *
 * @author Administrator
 */
public interface IPreLoginHandler {
    /**
     * 验证码
     */
    public static final String VERIFICATION_CODE = "verification_code";

    /**
     * 前置处理
     */
    public abstract Map<?, ?> handle(HttpSession session) throws Exception;
}
