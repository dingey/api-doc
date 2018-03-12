package com.di.apidoc.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author d
 */
public class InterfaceUtil {
	public static List<Class<?>> getinterfaces(String packagePath) {
		String realPath = getRealPath(packagePath.replaceAll("\\.", "/"));
		File file = new File(realPath);
		List<Class<?>> cs = new ArrayList<>();
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					cs.addAll(getInterfaces(packagePath, f));
				}
			} else {
				cs.addAll(getInterfaces(packagePath, file));
			}
		}
		return cs;
	}

	public static List<Class<?>> getInterfaces(String packagePath, File file) {
		List<Class<?>> cs = new ArrayList<>();
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					cs.addAll(getInterfaces(packagePath + "." + file.getName(), f));
				}
			} else {
				ClassLoader custom = null;
				try {
					URL url = new URL("file:/" + file.getParent() + "/");
					custom = new URLClassLoader(new URL[] { url });
					String n = packagePath + "." + file.getName();
					n = n.substring(0, n.lastIndexOf("."));
					try {
						Class<?> clazz = custom.loadClass(n);
						cs.add(clazz);
					} catch (NoClassDefFoundError ex) {
					}
				} catch (ClassNotFoundException | MalformedURLException e) {
					e.printStackTrace();
				} finally {
					if (custom != null)
						custom.clearAssertionStatus();
				}
			}
		}
		return cs;
	}

	private static String getRealPath(String relativePath) {
		String path = relativePath;
		File f = new File(relativePath);
		if (!f.exists()) {
			try {
				path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
			} catch (URISyntaxException var4) {
				var4.printStackTrace();
			}

			path = path + relativePath;
			f = new File(path);
			if (!f.exists()) {
				if (path.indexOf("test-classes") != -1) {
					path = path.replaceFirst("test-classes", "classes");
				}

				f = new File(path);
				if (!f.exists()) {
					System.err.println(relativePath + " not found");
				}
			}
		}

		return path;
	}
}
