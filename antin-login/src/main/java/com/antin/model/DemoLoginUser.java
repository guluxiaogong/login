package com.antin.model;

@SuppressWarnings("serial")
public class DemoLoginUser extends LoginUser {

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
