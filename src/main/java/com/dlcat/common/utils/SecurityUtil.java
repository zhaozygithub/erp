package com.dlcat.common.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * 加密工具类
 */
public class SecurityUtil {
	//换行符
	public static final String ENTER = "(\r\n|\r|\n|\n\r)";
	
	/**
	 * MD5加密
	 * @param text
	 * @return
	 */
	public static String md5(String text) {
		MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch(Exception e) {
            return "";
        }

        byte[] byteArray = text.getBytes();
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for(int i=0;i<md5Bytes.length;i++) {
            int val = ((int)md5Bytes[i]) & 0xff;
            if(val < 16) hexValue.append("0");
            
            hexValue.append(Integer.toHexString(val));
        }
        
        return hexValue.toString();
	}
	
	/**
	 * SHA-1加密
	 * @param text 要加密的文本
	 * @return
	 */
	public static String sha1(String text) {
		return new SimpleHash("SHA-1", text).toString();
	}

	/**
	 * SHA-1加密
	 * @param text 要加密的文本
	 * @param salt
	 * @return
	 */
	public static String sha1(String text, String salt) {
		return new SimpleHash("SHA-1", text, salt).toString();
	}
	
	/**
	 * 生成签名
	 * @param treeMap
	 * @return
	 * @throws Exception
	 */
	public static String generateSign(TreeMap<String, Object> treeMap, String key) throws Exception {
		return md5(encode(serialize(treeMap)), key);
	}
	
	/**
	 * 加密
	 * @param plainText 要加密的明文
	 * @param key 加密密钥
	 * @return
	 */
	public static String encrypt(String plainText, String key) throws Exception {
		plainText = plainText.replace(ENTER, "");
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec seckey = new SecretKeySpec(key.getBytes(),"AES");
		cipher.init(Cipher.ENCRYPT_MODE, seckey);
		
		return new String(encode(cipher.doFinal(plainText.getBytes())));
	}
	
	/**
	 * 解密
	 * @param cipherText 要解密的密文
	 * @param key 解密密钥
	 * @return
	 */
	public static String decrypt(String cipherText, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
		cipher.init(Cipher.DECRYPT_MODE, skey);
		
		return new String(cipher.doFinal(decode(cipherText)));
	}
	
	/**
	 * MD5加密
	 * @param content
	 * @param key
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String content, String key) throws NoSuchAlgorithmException {
		char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		
		content = content.replaceAll(ENTER, "");
		messageDigest.update((key + content + key).getBytes());
		byte[] md = messageDigest.digest();

		int k = 0, j = md.length;
		char str[] = new char[j * 2];
		for(int i = 0; i < j; i++) {
			byte byte0 = md[i];
			str[k++] = hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}
	
	/**
	 * 序列化
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static byte[] serialize(Object object) {
		byte[] result = null;
		try {
			result = new SerializerUtil().serialize(object);
		} catch(Exception e) {
			throw new RuntimeException("序列化失败", e);
		}
		return result;
	}
	
	/**
	 * 反序列化
	 * @param bytes
	 */
	public static Object unserialize(byte[] bytes) {
		return unserialize(bytes, null, null);
	}
	
	/**
	 * 反序列化
	 * @param bytes
	 * @param type
	 */
	public static Object unserialize(byte[] bytes, String type) {
		return unserialize(bytes, null, type);
	}
	
	/**
	 * 反序列化
	 * @param bytes
	 * @param clazz
	 */
	public static Object unserialize(byte[] bytes, Class<?> clazz) {
		return unserialize(bytes, clazz, null);
	}
	
	/**
	 * 反序列化
	 * @param bytes
	 * @param clazz
	 * @param type
	 */
	public static Object unserialize(byte[] bytes, Class<?> clazz, String type) {
		Object result = null;
		try {
			SerializerUtil serializerUtil = new SerializerUtil(type);
			if(clazz == null) result = serializerUtil.unserialize(bytes);
			else result = serializerUtil.unserialize(bytes, clazz);
		} catch(Exception e) {
			throw new RuntimeException("反序列化失败", e);
		}
		return result;
	}
	
	/**
	 * BASE64编码
	 * @param bytes
	 * @return
	 */
	public static String encode(byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
	}
	
	/**
	 * BASE64解码
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static byte[] decode(String str) throws IOException {
		return Base64.decodeBase64(str);
	}
}