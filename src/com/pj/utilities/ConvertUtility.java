package com.pj.utilities;

public class ConvertUtility {
	public static Integer parseInt(String input){
		return parseInt(input,0);
	}
	
	public static Integer parseInt(String input,int defaultVal){
		return parseDouble(input, defaultVal).intValue();
	}
	
	public static Float parseFloat(String input){
		return parseFloat(input,0F);
	}
	
	public static Float parseFloat(String input,float defaultVal){
		return parseDouble(input, defaultVal).floatValue();
	}
	
	public static Double parseDouble(String input){
		return parseDouble(input,0D);
	}
	
	public static Double parseDouble(String input,double defaultVal){
		Double r=Double.valueOf(defaultVal);
		if (!StringUtility.isEmpty(input)) {
			try {
				r=Double.parseDouble(input.trim());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return r;
	}
	
	public static Long parseLong(String input){
		return parseLong(input,0L);
	}
	
	public static Long parseLong(String input,long defaultVal){
		return  parseDouble(input, defaultVal).longValue();
	}
}
