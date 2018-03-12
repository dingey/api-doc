package com.di.apidoc.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.di.apidoc.bean.Apidoc;
import com.di.apidoc.bean.Apidoc.Menu.Item.Param;
import com.di.apidoc.bean.Apidoc.Menu.Item.Type;
import com.di.apidoc.util.InterfaceUtil;
import com.di.apidoc.util.MappingUtil;
import com.di.apidoc.util.ResourceUtil;
import com.di.kit.ClassUtil;
import com.di.kit.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author d
 */
@RestController
@RequestMapping(path = "/api-doc")
public class ApidocController {
	@Autowired
	private Apidoc doc;

	@GetMapping(path = "/{filename:.+}")
	public void index(@PathVariable(name = "filename") String filename) throws IOException {
		doc.getPackagePath();
		HttpServletResponse response = getResponse();
		response.setCharacterEncoding("UTF-8");
		if (filename.endsWith(".html")) {
			response.setHeader("Content-type", "text/html;charset=UTF-8");
		} else if (filename.endsWith(".css")) {
			response.setHeader("Content-type", "text/css; charset=utf-8");
		} else if (filename.endsWith(".js")) {
			response.setHeader("Content-type", "application/javascript; charset=utf-8");
		}
		PrintWriter writer = response.getWriter();
		String s = ResourceUtil.getStringResource(filename);
		writer.println(s);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping(path = "/menu")
	public Object menu() {
		List<Class> cs = new ArrayList<>();
		List<Apidoc.Menu> menus = new ArrayList<>();
		for (String path : doc.getPackagePath()) {
			List<Class<?>> classList = InterfaceUtil.getinterfaces(path);
			if (!doc.isSubPackagable()) {
				for (int i = 0; i < classList.size(); i++) {
					if (!classList.get(i).getPackage().getName().equals(path))
						classList.remove(i);
				}
			}
			cs.addAll(classList);
		}
		for (Class c : cs) {
			if (c.isAnnotationPresent(Api.class) || !doc.isOnlyApiAnnotation()) {
				Api a = (Api) c.getDeclaredAnnotation(Api.class);
				Apidoc.Menu m = new Apidoc.Menu();
				m.setClassName(c.getName());
				if (a != null) {
					m.setDesc(a.tags()[0]);
					m.setName(a.value());
				} else {
					m.setName(c.getSimpleName());
					m.setDesc(c.getName());
				}
				String beanName = StringUtil.firstCharLower(c.getSimpleName());
				List<Apidoc.Menu.Item> items = new ArrayList<>();
				for (Method method : c.getDeclaredMethods()) {
					if (method.isAnnotationPresent(ApiOperation.class) || !doc.isOnlyApiAnnotation()) {
						ApiOperation ao = method.getAnnotation(ApiOperation.class);
						Apidoc.Menu.Item item = new Apidoc.Menu.Item();
						item.setBeanName(beanName);
						item.setMethodName(method.getName());
						if (ao != null) {
							item.setName(ao.value());
							item.setDesc(ao.notes());
						} else {
							item.setName(method.getName());
							item.setDesc(method.getName());
						}
						item.setRtype(method.getReturnType().getName());
						item.setRdesc("");
						if (c.isEnum()) {
							item.setType(Apidoc.Menu.Item.Type.ENUM);
						} else if (c.isInterface()) {
							m.setType(1);
							item.setType(Apidoc.Menu.Item.Type.INTERFACE);
						} else if (c.isAnnotationPresent(Controller.class)
								|| c.isAnnotationPresent(RestController.class)) {
							m.setType(0);
							item.setType(Apidoc.Menu.Item.Type.CONTROL);
							item.setReqPath(MappingUtil.getPath(method));
						} else if (c.isArray()) {
							m.setType(1);
							item.setType(Apidoc.Menu.Item.Type.ARRAY);
						} else if (c == Date.class || c == java.sql.Date.class || c == Time.class
								|| c == Timestamp.class) {
							// m.setType(1);
							item.setType(Apidoc.Menu.Item.Type.DATE);
						} else {
							item.setType(Apidoc.Menu.Item.Type.CLASS);
						}
						List<Apidoc.Menu.Item.Param> ps = new ArrayList<>();
						for (Parameter p : method.getParameters()) {
							Apidoc.Menu.Item.Param pa = new Param();
							pa.setName(p.getName());
							if (p.isAnnotationPresent(RequestParam.class)) {
								RequestParam rp = p.getAnnotation(RequestParam.class);
								if (!rp.name().isEmpty())
									pa.setName(rp.name());
								if (!rp.defaultValue().isEmpty())
									pa.setDefaultValue(rp.defaultValue());
							}
							if (p.isAnnotationPresent(ApiParam.class)) {
								ApiParam apiParam = p.getAnnotation(ApiParam.class);
								if (!apiParam.name().isEmpty())
									pa.setName(apiParam.name());
								if (!apiParam.value().isEmpty())
									pa.setDesc(apiParam.value());
								pa.setRequired(apiParam.required());
							}
							if (p.getType().isPrimitive())
								pa.setRequired(true);
							pa.setType(Apidoc.Menu.Item.Type.getType(p.getType()));
							pa.setC(p.getType().getName());
							ps.add(pa);
						}
						item.setParams(ps);
						Class<?> returnType = method.getReturnType();
						if (returnType == Collection.class || returnType == List.class
								|| returnType == ArrayList.class) {
							java.lang.reflect.Type genericReturnType = method.getGenericReturnType();
							ParameterizedType pt = (ParameterizedType) genericReturnType;
							java.lang.reflect.Type type = pt.getActualTypeArguments()[0];
							item.setRtype(Type.LIST.name());
							item.setRc(type.getTypeName());
							item.setRdesc(returnType.getSimpleName() + "&lt;" + type.getTypeName() + "&gt;");
						} else if (returnType.isArray()) {
							item.setRtype(returnType.getComponentType().getName());
							item.setRdesc(returnType.getComponentType().getName() + "[]");
						} else {
							item.setRtype(Type.CLASS.name());
							item.setRc(returnType.getName());
							item.setRdesc(returnType.getSimpleName());
						}
						items.add(item);
					}
				}
				m.setItems(items);
				menus.add(m);
			} else if (c.isAnnotationPresent(ApiModel.class)) {

			}
		}
		return menus;
	}

	@PostMapping(path = "/link")
	public Object link(String className) {
		Apidoc.Menu.Item item = new Apidoc.Menu.Item();
		Class<?> c = null;
		try {
			c = Class.forName(className);
			item.setName(c.getSimpleName());
			if (c.isAnnotationPresent(ApiModel.class)) {
				ApiModel am = c.getAnnotation(ApiModel.class);
				if (!am.value().isEmpty())
					item.setDesc(am.value());
				if (!am.description().isEmpty())
					item.setDesc(am.description());
				item.setType(Type.CLASS);
			}
			List<Param> ps = new ArrayList<>();
			for (Field f : ClassUtil.getDeclaredFields(c)) {
				f.setAccessible(true);
				Param p = new Param();
				p.setName(f.getName());
				p.setType(Type.getType(f.getType()));
				p.setC(f.getType().getName());
				p.setDefaultValue(String.valueOf(f.get(c.newInstance())));
				if (f.isAnnotationPresent(ApiModelProperty.class)) {
					ApiModelProperty amp = f.getAnnotation(ApiModelProperty.class);
					if (!amp.name().isEmpty())
						p.setName(amp.name());
					if (!amp.value().isEmpty())
						p.setDesc(amp.value());
					p.setRequired(amp.required());
				}
				ps.add(p);
			}
			item.setParams(ps);
		} catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException
				| InstantiationException e) {
			e.printStackTrace();
		}
		return item;
	}

	public HttpServletRequest getRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attrs.getRequest();
	}

	public HttpServletResponse getResponse() {
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		return response;
	}
}
