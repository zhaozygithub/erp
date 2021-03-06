package com.dlcat.core.plugins;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.baomidou.kisso.SSOCache;
import com.baomidou.kisso.Token;

/**
 * 
 * 生产环境不要用，这里测试使用 
 * 
 * <p>
 * 未实现缓存记得去掉 sso.properties 中配置 sso.cache.class=com.baomidou.kisso.TestCache
 * </p>
 * 
 * @author hubin
 *
 */
public class KissoCache implements SSOCache {
	
	public static Map<String, Token> cache = new ConcurrentHashMap<String, Token>();

	public Token get( String key, int expires ) {
		System.out.println("缓存中获取 Token key=" + key + ", expires=" + expires);
		return cache.get(key);
	}


	public boolean set( String key, Token token, int expires ) {
		System.out.println(" 缓存 Token key=" + key);
		cache.put(key, token);
		return true;
	}


	public boolean delete( String key ) {
		System.out.println(" 缓存中删除 Token key=" + key);
		cache.remove(key);
		return true;
	}

}
