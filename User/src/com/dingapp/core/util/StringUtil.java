package com.dingapp.core.util;

public class StringUtil {

	public static boolean isNull(String s) {
		return s == null;
	}

	public static String getString(Object obj) {
		return obj == null ? "null" : obj.toString();
	}

	public static String getString(Object obj, Throwable t) {
		return String.format("message:%s\n%s",
				obj == null ? "null" : obj.toString(), t);
	}

}
