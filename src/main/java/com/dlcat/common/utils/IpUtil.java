package com.dlcat.common.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

public class IpUtil {
	/**
	 * IP -- 字符串转Long
	 * @param ipAddress
	 * @return
	 */
	public static Long ipStrToLong(String ipAddress) {
		if (StringUtils.isEmpty(ipAddress)) {
			return 0L;
		}
		if (ipAddress.indexOf(",") > 0) {
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
		}
		String[] ipArray = ipAddress.split("\\.");
		if (ipArray.length < 4) {
			return 0L;
		}

		Long ip = Long.parseLong(ipArray[0]) << 24;
		ip += Long.parseLong(ipArray[1]) << 16;
		ip += Long.parseLong(ipArray[2]) << 8;
		ip += Long.parseLong(ipArray[3]);

		return ip;
	}
	
	/**
	 * IP -- Long转字符串
	 * @param ipAddress
	 * @return
	 */
	public static String ipLongToStr(Long ipAddress) {
		String ip = ((ipAddress & 0xFF000000) >> 24) + ".";
		ip += ((ipAddress & 0x00FF0000) >> 16) + ".";
		ip += ((ipAddress & 0x0000FF00) >> 8) + ".";
		ip += (ipAddress & 0x000000FF);
		return ip;
	}
	/**
	 * 获取当前主机外网IP -- 待测试
	 * @author masai
	 * @time 2016年12月15日 下午1:41:15
	 * @return
	 * @throws SocketException
	 */
	public static String getRealIp() throws SocketException {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP
        Enumeration<NetworkInterface> netInterfaces =
            NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;// 是否找到外网IP
        
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress()
                        && !ip.isLoopbackAddress()
                        && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                } else if (ip.isSiteLocalAddress()
                        		&& !ip.isLoopbackAddress()
                        		&& ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                    localip = ip.getHostAddress();
                }
            }
        }
        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }
	/**
	 * 获取请求ip地址
	 * @param request
	 * @return
	 * @throws Exception
	 * @author masai
	 * @time 2017年5月27日 上午11:44:02
	 */
	public static String getRemoteIp(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null)
			ip = ip.split("\\,")[0];
		else if (request.getRemoteAddr() != null) {
			ip = request.getRemoteAddr().split("\\,")[0];
		}
		if ("0:0:0:0:0:0:0:1".equals(ip))
			ip = "127.0.0.1";
		return ip;
	}
	/**
	 * 获取请求ip地址，并转化成Long
	 * @Title getRemoteIpToLong 
	 * @Description TODO
	 * @param @param ipAddress
	 * @param @return  
	 * @return Long 
	 * @author liuran 
	 * @time 2017年5月27日下午12:01:52
	 */
	public static Long getRemoteIpToLong(HttpServletRequest request){
		Long ipAddres = null;
		try {
			ipAddres = ipStrToLong(getRemoteIp(request));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipAddres;
	}
}