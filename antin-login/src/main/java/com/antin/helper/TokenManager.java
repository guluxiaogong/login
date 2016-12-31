package com.antin.helper;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.antin.model.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 存储VT_USER信息，并提供操作方法
 *
 * @author Administrator
 */
public class TokenManager {

    private static Logger logger = LoggerFactory.getLogger(TokenManager.class);

    private static final Timer timer = new Timer(true);

    private static final ConfigHelper CONFIG_HELPER = SpringContextUtil.getBean(ConfigHelper.class);

    /**
     * /定期清理过期的令牌
     */
    static {
        long timeout = CONFIG_HELPER.getTokenTimeout() * 60 * 1000;
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                for (Entry<String, TokenModel> entry : DATA_MAP.entrySet()) {
                    String token = entry.getKey();
                    TokenModel tokenModel = entry.getValue();
                    Date expired = tokenModel.expired;
                    Date now = new Date();

                    // 当前时间大于过期时间
                    if (now.compareTo(new Date(expired.getTime() + timeout)) > 0) {
                        // 因为令牌支持自动延期服务
                        logger.debug("清除过期token：" + token);
                        // 已过期，清除对应token
                        DATA_MAP.remove(token);
                    }
                }
            }
        }, 60 * 1000, 60 * 1000);
    }

    /**
     * 避免静态类被实例化
     */
    private TokenManager() {
    }

    // 复合结构体，含loginUser与过期时间expried两个成员
    private static class TokenModel {
        private LoginUser loginUser; // 登录 用户对象
        private Date expired; // 过期时间
    }

    // 令牌存储结构
    private static final Map<String, TokenModel> DATA_MAP = new ConcurrentHashMap<String, TokenModel>();

    /**
     * 验证令牌有效性
     *
     * @param token
     * @return
     */
    public static LoginUser validate(String token) {
        TokenModel tokenModel = DATA_MAP.get(token);
        if (tokenModel != null) {
            tokenModel.expired = new Date();// 更新最后访问时间
            return tokenModel.loginUser;
        }
        return null;
    }

    /**
     * 用户授权成功后将授权信息存入
     *
     * @param token
     * @param loginUser
     */
    public static void addToken(String token, LoginUser loginUser) {
        TokenModel tokenModel = new TokenModel();
        tokenModel.loginUser = loginUser;
        tokenModel.expired = new Date();

        DATA_MAP.put(token, tokenModel);
    }

    /**
     * 移除token
     *
     * @param token
     */
    public static void invalid(String token) {
        if (token != null) {
            DATA_MAP.remove(token);
        }
    }

}
