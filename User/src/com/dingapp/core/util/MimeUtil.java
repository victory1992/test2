package com.dingapp.core.util;

import java.util.HashMap;

import android.text.TextUtils;

public class MimeUtil {
	private static HashMap<String, String> MIME_MapTable = new HashMap<String, String>();

	static {
		MIME_MapTable.put(".3gp", "video/3gpp");
		MIME_MapTable.put(".apk", "application/vnd.android.package-archive");
		MIME_MapTable.put(".asf", "video/x-ms-asf");
		MIME_MapTable.put(".avi", "video/x-msvideo");
		MIME_MapTable.put(".bin", "application/octet-stream");
		MIME_MapTable.put(".bmp", "image/bmp");
		MIME_MapTable.put(".c", "text/plain");
		MIME_MapTable.put(".class", "application/octet-stream");
		MIME_MapTable.put(".conf", "text/plain");
		MIME_MapTable.put(".cpp", "text/plain");
		MIME_MapTable.put(".doc", "application/msword");
		MIME_MapTable
				.put(".docx",
						"application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		MIME_MapTable.put(".xls", "application/vnd.ms-excel");
		MIME_MapTable
				.put(".xlsx",
						"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		MIME_MapTable.put(".exe", "application/octet-stream");
		MIME_MapTable.put(".gif", "image/gif");
		MIME_MapTable.put(".gtar", "application/x-gtar");
		MIME_MapTable.put(".gz", "application/x-gzip");
		MIME_MapTable.put(".h", "text/plain");
		MIME_MapTable.put(".htm", "text/html");
		MIME_MapTable.put(".html", "text/html");
		MIME_MapTable.put(".jar", "application/java-archive");
		MIME_MapTable.put(".java", "text/plain");
		MIME_MapTable.put(".jpeg", "image/jpeg");
		MIME_MapTable.put(".jpg", "image/jpeg");
		MIME_MapTable.put(".js", "application/x-javascript");
		MIME_MapTable.put(".log", "text/plain");
		MIME_MapTable.put(".m3u", "audio/x-mpegurl");
		MIME_MapTable.put(".m4a", "audio/mp4a-latm");
		MIME_MapTable.put(".m4b", "audio/mp4a-latm");
		MIME_MapTable.put(".m4p", "audio/mp4a-latm");
		MIME_MapTable.put(".m4u", "video/vnd.mpegurl");
		MIME_MapTable.put(".m4v", "video/x-m4v");
		MIME_MapTable.put(".mov", "video/quicktime");
		MIME_MapTable.put(".mp2", "audio/x-mpeg");
		MIME_MapTable.put(".mp3", "audio/x-mpeg");
		MIME_MapTable.put(".mp4", "video/mp4");
		MIME_MapTable.put(".mpc", "application/vnd.mpohun.certificate");
		MIME_MapTable.put(".mpe", "video/mpeg");
		MIME_MapTable.put(".mpeg", "video/mpeg");
		MIME_MapTable.put(".mpg", "video/mpeg");
		MIME_MapTable.put(".mpg4", "video/mp4");
		MIME_MapTable.put(".mpga", "audio/mpeg");
		MIME_MapTable.put(".msg", "application/vnd.ms-outlook");
		MIME_MapTable.put(".ogg", "audio/ogg");
		MIME_MapTable.put(".pdf", "application/pdf");
		MIME_MapTable.put(".png", "image/png");
		MIME_MapTable.put(".pps", "application/vnd.ms-powerpoint");
		MIME_MapTable.put(".ppt", "application/vnd.ms-powerpoint");
		MIME_MapTable
				.put(".pptx",
						"application/vnd.openxmlformats-officedocument.presentationml.presentation");
		MIME_MapTable.put(".prop", "text/plain");
		MIME_MapTable.put(".rc", "text/plain");
		MIME_MapTable.put(".rmvb", "audio/x-pn-realaudio");
		MIME_MapTable.put(".rtf", "application/rtf");
		MIME_MapTable.put(".sh", "text/plain");
		MIME_MapTable.put(".tar", "application/x-tar");
		MIME_MapTable.put(".tgz", "application/x-compressed");
		MIME_MapTable.put(".txt", "text/plain");
		MIME_MapTable.put(".wav", "audio/x-wav");
		MIME_MapTable.put(".wma", "audio/x-ms-wma");
		MIME_MapTable.put(".wmv", "audio/x-ms-wmv");
		MIME_MapTable.put(".wps", "application/vnd.ms-works");
		MIME_MapTable.put(".xml", "text/plain");
		MIME_MapTable.put(".z", "application/x-compress");
		MIME_MapTable.put(".zip", "application/x-zip-compressed");
	}

	public static String getMimeType(String filePath) {
		int dotSep = filePath.lastIndexOf(".");
		if (dotSep < 0) {
			return "*/*";
		}
		String suffix = filePath.substring(dotSep).toLowerCase();

		if (TextUtils.isEmpty(suffix) || !MIME_MapTable.containsKey(suffix)) {
			return "*/*";
		}
		return MIME_MapTable.get(suffix);
	}
}
