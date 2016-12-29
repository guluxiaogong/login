package com.antin.service;

import com.antin.model.Credential;
import com.antin.model.LoginUser;

import java.util.Set;

/**
 * 鉴权处理器
 *
 * @author Administrator
 */
public interface IAuthenticationHandler {

    /**
     * 验证用户名和密码
     *
     * @param credential
     * @return
     * @throws Exception
     */
    public LoginUser authenticate(Credential credential) throws Exception;

    /**
     * 验证自动登录标识
     *
     * @param auto
     * @return
     * @throws Exception
     */
    public LoginUser validateAutoToken(String auto) throws Exception;

    /**
     * 生成自动登录标识
     *
     * @param loginUser
     * @return
     * @throws Exception
     */
    public String createAutoToken(LoginUser loginUser) throws Exception;

    /**
     * 清除用户自动登录信息
     *
     * @param loginUser
     * @return
     * @throws Exception
     */
    public void clearAutoToken(LoginUser loginUser) throws Exception;

    /**
     * 获取当前登录用户角色
     *
     * @param loginUser
     * @return 返回null表示全部
     * @throws Exception
     */
    public Set<String> authedRoles(LoginUser loginUser) throws Exception;
}
