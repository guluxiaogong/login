package com.antin.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.antin.service.IAuthenticationHandler;
import com.antin.service.IPreLoginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * 应用配置信息
 *
 * @author Administrator
 */
public class Config implements ResourceLoaderAware {

    private static Logger logger = LoggerFactory.getLogger(Config.class);

    private ResourceLoader resourceLoader;

    private IAuthenticationHandler authenticationHandler; // 鉴权处理器

    private IPreLoginHandler preLoginHandler; // 登录前预处理器

    private String loginViewName = "/login.html"; // 登录页面视图名称

    private String indexViewName = "/index.html";//首页

    private int tokenTimeout = 30; // 令牌有效期，单位为分钟，默认30分钟

    private boolean secureMode = false; // 是否必须为https

    private int autoLoginExpDays = 365; // 自动登录状态有效期限，默认一年


    /**
     * 重新加载配置，以支持热部署
     */
    public void refreshConfig() throws Exception {

        // 加载config.properties
        Properties configProperties = new Properties();

        try {
            Resource resource = resourceLoader
                    .getResource("classpath:config.properties");
            configProperties.load(resource.getInputStream());
        } catch (IOException e) {
            logger.warn("在classpath下未找到配置文件config.properties");
        }

        // vt有效期参数
        String configTokenTimeout = (String) configProperties.get("tokenTimeout");
        if (configTokenTimeout != null) {
            try {
                tokenTimeout = Integer.parseInt(configTokenTimeout);
                logger.debug("config.properties设置tokenTimeout={}", tokenTimeout);
            } catch (NumberFormatException e) {
                logger.warn("tokenTimeout参数配置不正确");
            }
        }

        // 是否仅https安全模式下运行
        String configScureMode = configProperties.getProperty("secureMode");
        if (configScureMode != null) {
            this.secureMode = Boolean.parseBoolean(configScureMode);
            logger.debug("config.properties设置secureMode={}", this.secureMode);
        }

        // 自动登录有效期
        String configAutoLoginExpDays = configProperties.getProperty("autoLoginExpDays");
        if (configAutoLoginExpDays != null) {
            try {
                autoLoginExpDays = Integer.parseInt(configAutoLoginExpDays);
                logger.debug("config.properties设置autoLoginExpDays={}",
                        autoLoginExpDays);
            } catch (NumberFormatException e) {
                logger.warn("autoLoginExpDays参数配置不正确");
            }
        }
    }

    /**
     * 应用停止时执行，做清理性工作，如通知客户端logout
     */
    public void destroy() {

    }

    /**
     * 获取当前鉴权处理器
     *
     * @return
     */
    public IAuthenticationHandler getAuthenticationHandler() {
        return authenticationHandler;
    }

    public void setAuthenticationHandler(
            IAuthenticationHandler authenticationHandler) {
        this.authenticationHandler = authenticationHandler;
    }

    /**
     * 获取登录前预处理器
     *
     * @return
     */
    public IPreLoginHandler getPreLoginHandler() {
        return preLoginHandler;
    }

    public void setPreLoginHandler(IPreLoginHandler preLoginHandler) {
        this.preLoginHandler = preLoginHandler;
    }

    /**
     * 获取登录页面视图名称
     *
     * @return
     */
    public String getLoginViewName() {
        return loginViewName;
    }

    public void setLoginViewName(String loginViewName) {
        this.loginViewName = loginViewName;
    }

    /**
     * 登录后首面
     *
     * @return
     */
    public String getIndexViewName() {
        return indexViewName;
    }

    public void setIndexViewName(String indexViewName) {
        this.indexViewName = indexViewName;
    }

    /**
     * 获取令牌有效期，单位为分钟
     *
     * @return
     */
    public int getTokenTimeout() {
        return tokenTimeout;
    }

    public void setTokenTimeout(int tokenTimeout) {
        this.tokenTimeout = tokenTimeout;
    }

    @Override
    public void setResourceLoader(ResourceLoader loader) {
        this.resourceLoader = loader;
    }

    public boolean isSecureMode() {
        return secureMode;
    }

    public int getAutoLoginExpDays() {
        return autoLoginExpDays;
    }

}
