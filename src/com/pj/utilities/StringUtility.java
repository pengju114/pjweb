package com.pj.utilities;

public class StringUtility {
	public static final String EMPTY_STRING="";
        
        /**
         * 去掉两端空格判断字符串是否为空
         * @param str
         * @return 
         */
	public static boolean isEmpty(String str) {
		return (str==null || str.trim().length()<1);
	}
	
        /**
         * 确保obj是字符串，如果是null把obj转换为EMPTY_STRING
         * @param obj
         * @return 
         */
        public static String ensureAsString(Object obj){
            if (obj==null) {
                return EMPTY_STRING;
            }
            return String.valueOf(obj);
        }
}
