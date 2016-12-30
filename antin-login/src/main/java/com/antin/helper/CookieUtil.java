package com.antin.helper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 操作cookie
 *
 * @author Administrator
 */
public class CookieUtil {

    private CookieUtil() {
    }

    /**
     * 写cookie
     *
     * @param cookieName
     * @param cookieValue
     * @param maxAge
     * @param response
     * @param path
     */
    public static void setCookie(String cookieName, String cookieValue, int maxAge, boolean isSecureMode, HttpServletResponse response, String path) {

        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(maxAge);
        if (path != null)
            cookie.setPath("/");

        if (isSecureMode)
            cookie.setSecure(true);
        response.addCookie(cookie);
    }

    /**
     * 查找特定cookie值
     *
     * @param cookieName
     * @param request
     * @return
     */
    public static String getCookie(String cookieName, HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 删除cookie
     *
     * @param cookieName
     * @param response
     * @param path
     */
    public static void deleteCookie(String cookieName, HttpServletResponse response, String path) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        if (path != null)
            cookie.setPath("/");

        response.addCookie(cookie);
    }
}
