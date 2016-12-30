package com.antin.persist;

import java.io.FileOutputStream;

import com.antin.helper.MD5;
import com.antin.model.DemoLoginUser;
import jdk.nashorn.internal.runtime.Context;
import org.springframework.stereotype.Repository;

/**
 * 登录用户信息持久化服务，相当于DAO对象的模拟
 *
 * @author Administrator
 */
@Repository
public class UserPersistenceObject {

    /**
     * 更新当前登录用户的auto标识
     *
     * @param loginName
     * @param auto
     * @throws Exception
     */
    public void updateLoginToken(String loginName, String auto) throws Exception {

        //将信息写入存储文件test，格式为auto=loginName，如：02564fc6a02a35c689cbdf898458d2da=admin
        FileOutputStream fos = new FileOutputStream("/testssss");
        fos.write((auto + "=" + loginName).getBytes());
        fos.close();
    }

    /**
     * 按登录账号查询用户信息
     *
     * @param uname
     * @return
     */
    public DemoLoginUser getUser(String uname) {
        if ("admin".equals(uname)) {
            DemoLoginUser loginUser = new DemoLoginUser();
            loginUser.setLoginName("admin");
            loginUser.setPassword(MD5.encode("admin"));
            return loginUser;
        }
        return null;
    }


}