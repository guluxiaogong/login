package com.antin.controller;

import com.antin.helper.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 只是一个演示性的配置管理控制器
 *
 * @author Administrator
 */
@Controller
public class ConfigManageController {

    @Autowired
    private Config config;

    @RequestMapping("/config")
    public void configPage() {
    }

    @RequestMapping("/config/refresh")
    @ResponseBody
    public boolean config(String code) throws Exception {
        if ("test".equals(code)) {

            config.refreshConfig();
            return true;
        }

        return false;
    }
}
