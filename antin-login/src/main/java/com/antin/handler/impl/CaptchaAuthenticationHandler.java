package com.antin.handler.impl;

import java.io.FileInputStream;
import java.util.Set;

import com.antin.handler.IAuthenticationHandler;
import com.antin.helper.MD5;
import com.antin.helper.StringUtil;
import com.antin.model.Credential;
import com.antin.model.DemoLoginUser;
import com.antin.model.LoginUser;
import com.antin.persist.UserPersistenceObject;
import org.springframework.beans.factory.annotation.Autowired;


public class CaptchaAuthenticationHandler implements IAuthenticationHandler {

    @Autowired
    private UserPersistenceObject userPersistenceObject;

    /**
     * 验证用户名和密码
     *
     * @param credential
     * @return
     * @throws Exception
     */
    @Override
    public LoginUser authenticate(Credential credential) throws Exception {

        // 获取session中保存的验证码
        String verificationCode = (String) credential.getVerificationCode();
        //表单提交的验证码
        String captcha = credential.getParameter("captcha");
        if (!captcha.equalsIgnoreCase(verificationCode)) {
            credential.setError("验证码错误！");
            return null;
        }
        // 从持久化中查询登录账号对应的用户对象
        DemoLoginUser loginUser = userPersistenceObject.getUser(credential.getParameter("loginName"));//loginName必须唯一
        if (loginUser == null)
            credential.setError("帐号错误！");
        else {
            try {
                String password = credential.getParameter("password");
                String password2 = MD5.encode(loginUser.getPassword() + verificationCode);
                if (password2.equals(password)) {
                    return loginUser;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            credential.setError("密码错误！");
        }
        return null;
    }


    /**
     * 自动登录
     *
     * @param auto
     * @return
     * @throws Exception
     */
    @Override
    public LoginUser validateAutoToken(String auto) throws Exception {


        // 从持久化存储中按auto查找对应loginUser
        FileInputStream fis = new FileInputStream("/testssss");
        byte[] buff = new byte[fis.available()];
        fis.read(buff);
        fis.close();

        String tmp = new String(buff);
        String[] tmps = tmp.split("=");

        // 相当于从存储中找个了与lt匹配的数据记录
        if (auto.equals(tmps[0])) {
            // 将匹配的数据装配成loginUser对象
            DemoLoginUser loginUser = userPersistenceObject.getUser(tmps[1]);
            return loginUser;
        }

        // 没有匹配项则表示自动登录标识无效
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

        DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;

        // 生成一个唯一标识用作auto
        String auto = StringUtil.uniqueKey();

        // 将新lt更新到当前user对应字段
        userPersistenceObject.updateLoginToken(demoLoginUser.getLoginName(), auto);

        return auto;
    }

    /**
     * 更新持久化的用户
     *
     * @param loginUser
     * @throws Exception
     */
    @Override
    public void clearAutoToken(LoginUser loginUser)
            throws Exception {
        DemoLoginUser demoLoginUser = (DemoLoginUser) loginUser;
        userPersistenceObject.updateLoginToken(demoLoginUser.getLoginName(), null);
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
