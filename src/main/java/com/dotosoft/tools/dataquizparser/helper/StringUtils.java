package com.dotosoft.tools.dataquizparser.helper;

public class StringUtils {
	public static boolean hasValue(String value) {
		if(value != null && !value.equals("")) {
			return true;
		}
		
		return false;
	}
}
