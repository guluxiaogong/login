package com.antin.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.antin.model.ClientSystem;
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
    private static final Config config = SpringContextUtil
            .getBean(Config.class);

    static {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                for (Entry<String, TokenModel> entry : DATA_MAP.entrySet()) {
                    String vt = entry.getKey();
                    TokenModel tm = entry.getValue();
                    Date expired = tm.expired;
                    Date now = new Date();

                    // 当前时间大于过期时间
                    if (now.compareTo(expired) > 0) {
                        // 因为令牌支持自动延期服务，并且应用客户端缓存机制后，
                        // 令牌最后访问时间是存储在客户端的，所以服务端向所有客户端发起一次timeout通知，
                        // 客户端根据lastAccessTime + tokenTimeout计算是否过期，<br>
                        // 若未过期，用各客户端最大有效期更新当前过期时间
                        List<ClientSystem> clientSystems = config
                                .getClientSystems();
                        Date maxClientExpired = expired;
                        for (ClientSystem clientSystem : clientSystems) {
                            Date clientExpired = clientSystem.noticeTimeout(vt,
                                    config.getTokenTimeout());
                            if (clientExpired != null
                                    && clientExpired.compareTo(now) > 0) {
                                maxClientExpired = maxClientExpired.compareTo(clientExpired) < 0 ? clientExpired : maxClientExpired;
                            }
                        }

                        if (maxClientExpired.compareTo(now) > 0) { // 客户端最大过期时间大于当前
                            logger.debug("更新过期时间到" + maxClientExpired);
                            tm.expired = maxClientExpired;
                        } else {
                            logger.debug("清除过期token：" + vt);
                            // 已过期，清除对应token
                            DATA_MAP.remove(vt);
                        }
                    }
                }
            }
        }, 60 * 1000, 60 * 1000);
    }

    // 避免静态类被实例化
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
        TokenModel tm = DATA_MAP.get(token);
        return tm == null ? null : tm.loginUser;
    }

    /**
     * 用户授权成功后将授权信息存入
     *
     * @param token
     * @param loginUser
     */
    public static void addToken(String token, LoginUser loginUser) {
        TokenModel tm = new TokenModel();
        tm.loginUser = loginUser;

        tm.expired = new Date(new Date().getTime()
                + config.getTokenTimeout() * 60 * 1000);

        DATA_MAP.put(token, tm);
    }

    public static void invalid(String token) {
        if (token != null) {
            DATA_MAP.remove(token);
        }
    }
}
