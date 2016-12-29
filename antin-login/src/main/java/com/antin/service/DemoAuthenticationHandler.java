package com.antin.service;

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

    @Override
    public String createAutoToken(LoginUser loginUser) throws Exception {
        return null;
    }

    @Override
    public void clearAutoToken(LoginUser loginUser) throws Exception {
    }

    @Override
    public Set<String> authedRoles(LoginUser loginUser) throws Exception {
        return null;
    }
}
