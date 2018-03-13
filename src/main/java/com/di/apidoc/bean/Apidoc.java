package com.di.apidoc.bean;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.di.kit.StringUtil;

/**
 * @author d
 */
public class Apidoc implements Serializable {

	private static final long serialVersionUID = -1047806064484821659L;

	private List<String> packagePath;

	private boolean interfaceable;
	// 限制是否Api注解
	private boolean onlyApiAnnotation = false;
	// 限制是否递归子包下的文件
	private boolean subPackagable = false;

	private HashMap<String, HashSet<Method>> method = new HashMap<>();

	public Apidoc() {
	}

	public Apidoc(String packagePath) {
		this.getPackagePath().add(packagePath);
	}

	public Apidoc putMethod(Method m) {
		String beanName = StringUtil.firstCharLower(m.getDeclaringClass().getSimpleName());
		HashSet<Method> set = method.get(beanName);
		if (set == null) {
			set = new HashSet<>();
			set.add(m);
			method.put(beanName, set);
		}
		return this;
	}

	public HashSet<Method> getMethod(String beanName) {
		return method.get(beanName);
	}

	public boolean isSubPackagable() {
		return subPackagable;
	}

	public Apidoc setSubPackagable(boolean subPackagable) {
		this.subPackagable = subPackagable;
		return this;
	}

	public boolean isOnlyApiAnnotation() {
		return onlyApiAnnotation;
	}

	public Apidoc setOnlyApiAnnotation(boolean onlyApiAnnotation) {
		this.onlyApiAnnotation = onlyApiAnnotation;
		return this;
	}

	public List<String> getPackagePath() {
		if (packagePath == null) {
			packagePath = new ArrayList<>();
		}
		return packagePath;
	}

	public void setPackagePath(List<String> packagePath) {
		this.packagePath = packagePath;
	}

	public Apidoc packagePath(String... packagePath) {
		this.getPackagePath().addAll(Arrays.asList(packagePath));
		return this;
	}

	public boolean isInterfaceable() {
		return interfaceable;
	}

	public void setInterfaceable(boolean interfaceable) {
		this.interfaceable = interfaceable;
	}

	public static class Menu implements Serializable {

		private static final long serialVersionUID = 6650309062740452862L;
		// 参数名
		String name;
		// 参数描述
		String desc;
		// 类名
		String className;
		// 0controller;1dubbo
		int type;
		List<Item> items;

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public List<Item> getItems() {
			return items;
		}

		public void setItems(List<Item> items) {
			this.items = items;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public static class Item {
			// 方法中文名称
			String name;
			// 方法描述
			String desc;
			// 类型：0:control;1:interface;2:class;3:enum;4:array;5:list;
			Type type;
			// 方法名
			String methodName;
			String beanName;
			// 请求路径
			String reqPath;
			// 返回类型
			String rtype;
			// 返回类型
			String rc;
			// 返回说明
			String rdesc;
			// 方法参数
			List<Param> params;

			public String getBeanName() {
				return beanName;
			}

			public void setBeanName(String beanName) {
				this.beanName = beanName;
			}

			public String getRc() {
				return rc;
			}

			public void setRc(String rc) {
				this.rc = rc;
			}

			public String getRtype() {
				return rtype;
			}

			public void setRtype(String rtype) {
				this.rtype = rtype;
			}

			public String getRdesc() {
				return rdesc;
			}

			public void setRdesc(String rdesc) {
				this.rdesc = rdesc;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getDesc() {
				return desc;
			}

			public void setDesc(String desc) {
				this.desc = desc;
			}

			public Type getType() {
				return type;
			}

			public void setType(Type type) {
				this.type = type;
			}

			public List<Param> getParams() {
				return params;
			}

			public void setParams(List<Param> params) {
				this.params = params;
			}

			public static enum Type {
				INTERFACE, ENUM, CONTROL, CLASS, STRING, NUM, DATE, ARRAY, LIST;
				public static String getType(Class<?> typec) {
					if (typec.isEnum()) {
						return ENUM.name();
					} else if (typec.isInterface()) {
						return INTERFACE.name();
					} else if (typec == String.class) {
						return STRING.name();
					} else if (typec == byte.class || typec == Byte.class || typec == short.class
							|| typec == Short.class || typec == int.class || typec == Integer.class
							|| typec == long.class || typec == Long.class || typec == double.class
							|| typec == Double.class || typec == float.class || typec == Float.class) {
						return NUM.name();
					}
					return CLASS.name();
				}
			}

			public String getMethodName() {
				return methodName;
			}

			public void setMethodName(String methodName) {
				this.methodName = methodName;
			}

			public String getReqPath() {
				return reqPath;
			}

			public void setReqPath(String reqPath) {
				this.reqPath = reqPath;
			}

			public static class Param {
				// 参数名
				String name;
				// 参数类型
				String type;
				String c;
				// 参数描述
				String desc;
				// 默认值
				String defaultValue;
				// 可空
				boolean required = true;

				public String getC() {
					return c;
				}

				public void setC(String c) {
					this.c = c;
				}

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

				public String getType() {
					return type;
				}

				public void setType(String type) {
					this.type = type;
				}

				public String getDesc() {
					return desc;
				}

				public void setDesc(String desc) {
					this.desc = desc;
				}

				public String getDefaultValue() {
					return defaultValue;
				}

				public void setDefaultValue(String defaultValue) {
					this.defaultValue = defaultValue;
				}

				public boolean isRequired() {
					return required;
				}

				public void setRequired(boolean required) {
					this.required = required;
				}

			}

			public static class Resp {
				// 响应名
				String name;
				// 响应类型
				String type;
				// 响应描述
				String desc;
				// json格式的具体字段描述
				String json;

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

				public String getType() {
					return type;
				}

				public void setType(String type) {
					this.type = type;
				}

				public String getDesc() {
					return desc;
				}

				public void setDesc(String desc) {
					this.desc = desc;
				}

				public String getJson() {
					return json;
				}

				public void setJson(String json) {
					this.json = json;
				}
			}
		}
	}
}
