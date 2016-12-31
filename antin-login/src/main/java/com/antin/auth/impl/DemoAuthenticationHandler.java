package com.antin.auth.impl;

import com.antin.auth.IAuthenticationHandler;
import com.antin.model.Credential;
import com.antin.model.DemoLoginUser;
import com.antin.model.LoginUser;

import java.util.Set;

/**
 * 示例性的鉴权处理器，当用户名和密码都为admin时授权通过，返回的也是一个示例性Credential实例
 *
 * @author Administrator
 */
public class DemoAuthenticationHandler implements IAuthenticationHandler {
    /**
     * 验证用户名和密码
     *
     * @param credential
     * @return
     * @throws Exception
     */
    @Override
    public LoginUser authenticate(Credential credential) throws Exception {
        if ("admin".equals(credential.getParameter("name"))
                && "admin".equals(credential.getParameter("passwd"))) {
            DemoLoginUser user = new DemoLoginUser();
            user.setLoginName("admin");
            return user;
        } else {
            credential.setError("帐号或密码错误");
            return null;
        }
    }

    /**
     * 验证自动登录标识是否有效
     *
     * @param auto
     * @return
     * @throws Exception
     */
    @Override
    public LoginUser validateAutoToken(String auto) throws Exception {
        return null;
    }

    /**
     * 生成自动登录标识
     *
     * @param loginUser
     * @return
     * @throws Exception
     */
    @Override
    public String createAutoToken(LoginUser loginUser) throws Exception {
        return null;
    }

    /**
     * 更新持久化的用户
     *
     * @param loginUser
     * @throws Exception
     */
    @Override
    public void clearAutoToken(LoginUser loginUser) throws Exception {
    }

    /**
     * 用户权限
     *
     * @param loginUser//TODO
     * @return
     * @throws Exception
     */
    @Override
    public Set<String> authedRoles(LoginUser loginUser) throws Exception {
        return null;
    }
}
