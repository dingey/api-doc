package com.di.apidoc.util;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParameterUtil {
	static ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object getValue(Parameter p, String v) throws JsonMappingException, IOException {
		if (v == null || v.isEmpty())
			return null;
		if (p.getType() == byte.class || p.getType() == Byte.class) {
			return Byte.valueOf(v).byteValue();
		} else if (p.getType() == short.class || p.getType() == Short.class) {
			return Short.valueOf(v).shortValue();
		} else if (p.getType() == int.class || p.getType() == Integer.class) {
			return Integer.valueOf(v).intValue();
		} else if (p.getType() == long.class || p.getType() == Long.class) {
			return Long.valueOf(v).longValue();
		} else if (p.getType() == double.class || p.getType() == Double.class) {
			return Double.valueOf(v).doubleValue();
		} else if (p.getType() == float.class || p.getType() == Float.class) {
			return Float.valueOf(v).floatValue();
		} else if (p.getType() == boolean.class || p.getType() == Boolean.class) {
			return Boolean.valueOf(v).booleanValue();
		} else if (p.getType().isArray()) {
			ParameterizedType pt = (ParameterizedType) p.getParameterizedType();
			Type type = pt.getActualTypeArguments()[0];
			return listFromJson(v, (Class<?>) type).toArray();
		} else if (p.getType() == java.util.List.class) {
			ParameterizedType pt = (ParameterizedType) p.getParameterizedType();
			Type type = pt.getActualTypeArguments()[0];
			return listFromJson(v, (Class<?>) type);
		} else if (p.getType() == Date.class) {
			try {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(v);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		} else if (p.getType() == String.class) {
			return v;
		} else if (p.getType().isEnum()) {
			Class<?> type = p.getType();
			Class<? extends Enum> e = (Class<? extends Enum>) type;
			return Enum.valueOf(e, v);
		} else if ((p.getType() instanceof Object) && p.getType() != Object.class && p.getType() != Class.class) {
			return fromJson(v, p.getType());
		}
		return v;
	}

	public static <T> Object fromJson(String json, Class<T> c) throws JsonMappingException, IOException {
		return objectMapper.readValue(json, c);
	}

	public static <T> List<T> listFromJson(String json, Class<T> c) {
		try {
			return objectMapper.readValue(json,
					objectMapper.getTypeFactory().constructParametrizedType(ArrayList.class, ArrayList.class, c));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
