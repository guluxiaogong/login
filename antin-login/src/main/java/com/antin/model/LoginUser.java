package com.antin.model;

import java.io.Serializable;

/**
 * 用户对象
 *
 * @author Administrator
 */
@SuppressWarnings("serial")
public abstract class LoginUser implements Serializable {

    private String loginName;

    private String password;

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return loginName;
    }
}
