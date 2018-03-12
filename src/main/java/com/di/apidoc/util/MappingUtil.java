package com.di.apidoc.util;

import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author d
 */
public class MappingUtil {
	public static String[] getPaths(Method m) {
		if (m.isAnnotationPresent(RequestMapping.class)) {
			RequestMapping rm = m.getAnnotation(RequestMapping.class);
			return rm.path();
		} else if (m.isAnnotationPresent(GetMapping.class)) {
			GetMapping gm = m.getAnnotation(GetMapping.class);
			return gm.path();
		} else if (m.isAnnotationPresent(PostMapping.class)) {
			PostMapping gm = m.getAnnotation(PostMapping.class);
			return gm.path();
		} else if (m.isAnnotationPresent(PutMapping.class)) {
			PutMapping gm = m.getAnnotation(PutMapping.class);
			return gm.path();
		} else if (m.isAnnotationPresent(DeleteMapping.class)) {
			DeleteMapping gm = m.getAnnotation(DeleteMapping.class);
			return gm.path();
		}
		return null;
	}

	public static String getPath(Method m) {
		String[] paths = getPaths(m);
		if (paths != null && paths.length > 0) {
			return paths[0];
		}
		return null;
	}
}
