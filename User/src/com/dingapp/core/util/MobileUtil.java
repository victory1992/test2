package com.dingapp.core.util;

import java.util.regex.Pattern;

public class MobileUtil {
	public static boolean isValidPhoneNumber(String phone) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(17[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$");
		return p.matcher(phone).matches();
	}
}
