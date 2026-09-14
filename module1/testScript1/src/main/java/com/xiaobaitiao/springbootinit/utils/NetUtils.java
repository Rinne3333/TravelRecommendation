package com.xiaobaitiao.springbootinit.utils;

import java.net.InetAddress;
import javax.servlet.http.HttpServletRequest;

/**
 * 网络工具类
 *
 * @author 程序员小白条
 * @from <a href="https://luoye6.github.io/"> 个人博客
 */
public class NetUtils {

    private static final String[] PROXY_HEADERS = {
            "x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "X-Real-IP"
    };

    /**
     * 获取客户端 IP 地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "127.0.0.1";
        }
        for (String header : PROXY_HEADERS) {
            String forwardedIp = firstValidAddress(request.getHeader(header));
            if (forwardedIp != null) {
                return forwardedIp;
            }
        }
        String ip = firstValidAddress(request.getRemoteAddr());
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
                // 根据网卡取本机配置的 IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (inet != null) {
                    ip = inet.getHostAddress();
                }
        }
        if (ip == null) {
            return "127.0.0.1";
        }
        return ip;
    }

    private static String firstValidAddress(String addressList) {
        if (addressList == null) {
            return null;
        }
        for (String candidate : addressList.split(",")) {
            String address = candidate.trim();
            if (!address.isEmpty() && !"unknown".equalsIgnoreCase(address)) {
                return address;
            }
        }
        return null;
    }

}
