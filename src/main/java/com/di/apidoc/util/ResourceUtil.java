package com.di.apidoc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceUtil {
	public static String getStringResource(String filename) throws IOException {
		// 返回读取指定资源的输入流
		InputStream is = ResourceUtil.class.getResourceAsStream("/" + filename);
		// InputStream is=当前类.class.getResourceAsStream("XX.config");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String s = "";
		while ((s = br.readLine()) != null) {
			sb.append(s).append("\r\n");
		}
		return sb.toString();
	}
}
