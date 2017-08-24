package com.dingapp.core.util;

import org.jsoup.Jsoup;

public class Html2Text {

	public static String toText(String html) {
		String result = Jsoup.parse(html).text();
		return result;
	}

}
