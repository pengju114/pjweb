package com.pj.utilities;

import java.security.MessageDigest;

public class SecurityUtility {
	public static String SHA1Encrypt(String input) {
		if (input == null) {
			return null;
		}
		try {  
            //指定sha1算法  
            MessageDigest digest = MessageDigest.getInstance("SHA-1");  
            digest.update(input.getBytes("UTF-8"));  
            //获取字节数组  
            byte messageDigest[] = digest.digest();  
            // Create Hex String  
            StringBuilder hexString = new StringBuilder();  
            // 字节数组转换为 十六进制 数  
            for (int i = 0; i < messageDigest.length; i++) {  
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);  
                if (shaHex.length() < 2) {  
                    hexString.append(0);  
                }  
                hexString.append(shaHex);  
            }  
            return hexString.toString();  
  
        } catch (Exception e) {  
            return null;  
        }
	}
	
	public static void main(String[] agrs) {
		System.out.print(SHA1Encrypt("762354"));
	}
}
