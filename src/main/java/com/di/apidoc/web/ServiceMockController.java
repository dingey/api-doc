package com.di.apidoc.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.di.apidoc.bean.Apidoc;
import com.di.apidoc.util.ParameterUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

@RestController
public class ServiceMockController implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	private HashMap<Class<?>, Method[]> map = new HashMap<>();
	@Autowired
	private Apidoc apidoc;

	@PostMapping(path = "/api-doc/service")
	public Object invoke(Request request) {
		return invoke(request.getBeanName(), request.getMethodName(), request.debug, request.getParams());
	}

	public <T> Object invoke(String beanName, String methodName, boolean debug, String... params) {
		Object serviceClass = applicationContext.getBean(beanName);
		if (serviceClass == null)
			return Response.fail("找不到" + beanName);
		Method[] declaredMethods = getMethod(serviceClass.getClass());
		for (Method method : declaredMethods) {
			if (method.getName().equals(methodName) && ((params == null && method.getParameterCount() == 0)
					|| (params != null && method.getParameterCount() == params.length))) {
				Method iMethod = apidoc.getMethod(beanName, method);
				Object[] args = new Object[params == null ? 0 : params.length];
				for (int i = 0; i < method.getParameters().length; i++) {
					Parameter parameter = iMethod.getParameters()[i];
					try {
						args[i] = ParameterUtil.getValue(parameter, params[i]);
					} catch (Exception e) {
						Response response = Response
								.fail("第" + (i + 1) + "个参数" + parameter.getName() + "解析异常:" + e.getMessage());
						if (debug)
							response.stackTrace(e);
						return response;
					}
				}
				try {
					return Response.success(method.invoke(serviceClass, args));
				} catch (Throwable e) {
					Response fail = Response.fail("执行dubbo方法" + methodName + "异常:" + e.getMessage());
					if (debug)
						fail.stackTrace(e);
					return fail;
				}
			}
		}
		return Response.fail("找不到dubbo方法");
	}

	@ApiModel("请求参数")
	public static class Request {
		@ApiModelProperty(value = "service名称", example = "userService")
		private String beanName = "userService";
		@ApiModelProperty(value = "方法名称", example = "getUserBasicInfo")
		private String methodName = "getUserBasicInfo";
		private boolean debug = false;
		@ApiModelProperty(value = "方法参数")
		private String[] params;

		public boolean isDebug() {
			return debug;
		}

		public void setDebug(boolean debug) {
			this.debug = debug;
		}

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public String getMethodName() {
			return methodName;
		}

		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}

		public String[] getParams() {
			return params;
		}

		public void setParams(String[] params) {
			this.params = params;
		}
	}

	@ApiModel("响应")
	public static class Response {
		@ApiModelProperty(value = "响应状状态1：成功；0失败")
		private int code = 1;
		@ApiModelProperty(value = "响应内容")
		private Object data;
		private String stack;

		public Response() {
			super();
		}

		public Response(Object data) {
			this.data = data;
		}

		public Response(int code, Object data) {
			super();
			this.code = code;
			this.data = data;
		}

		public String getStack() {
			return stack;
		}

		public Response setStack(String stack) {
			this.stack = stack;
			return this;
		}

		public Response stackTrace(Throwable e) {
			StringBuilder s = new StringBuilder();
			for (StackTraceElement ste : e.getStackTrace()) {
				s.append(ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getLineNumber() + ")\r\n");
			}
			this.stack = s.toString();
			return this;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

		public static Response success(Object data) {
			return new Response(data);
		}

		public static Response fail(Object data) {
			return new Response(0, data);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	Method[] getMethod(Class<?> c) {
		Method[] methods = map.get(c);
		if (methods == null) {
			methods = c.getDeclaredMethods();
			map.put(c, methods);
		}
		return methods;
	}
}
