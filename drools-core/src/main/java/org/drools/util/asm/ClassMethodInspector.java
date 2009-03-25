package org.drools.util.asm;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassMethodInspector {
	private final Map<String, List<String>> methods = new HashMap<String, List<String>>();;
	ClassFieldInspector classFieldInspector = null;

	public ClassMethodInspector(final Class<?> clazz) throws IOException {
		this(clazz, new ClassFieldInspector(clazz));
	}

	public ClassMethodInspector(final Class<?> clazz,
			ClassFieldInspector classFieldInspector) throws IOException {
		this.classFieldInspector = classFieldInspector;
		Method[] methods = clazz.getDeclaredMethods();
		Map<String, Method> listSetters = classFieldInspector
				.getSetterMethods();
		Map<String, Method> listGetters = classFieldInspector
				.getSetterMethods();
		for (int i = 0; i < methods.length; i++) {
			Method aMethod = methods[i];
			int modifiers = methods[i].getModifiers();
			String methodName = aMethod.getName();
			if (methodName != "hashCode" && methodName != "equals"
					&& Modifier.isPublic(modifiers)) {
				if (methodName.length() >= 4) {
					char c[] = new char[1];
					c[0] = methodName.charAt(3);
					methodName = new String(c).toLowerCase()
							+ methodName.substring(4);
				} else {
					methodName = aMethod.getName();
				}

				/*
				 * method must not be a getter or a setter
				 */
				if (aMethod.getName().length() <= 3
						|| (listSetters.containsKey(methodName) == false && listGetters
								.containsKey(methodName) == false)) {
					Class<?>[] listParam = aMethod.getParameterTypes();
					for (int j = 0; j < listParam.length; j++) {
						Class c = listParam[j];
						System.out.println(aMethod.getName());
						addParamMethod(aMethod.getName(),
								listParam[j].getName()
										.substring(
												listParam[j].getName()
														.lastIndexOf(".") + 1));

					}
				}
			}
		}
	}

	private void addParamMethod(String methodName, String paramType) {
		List<String> paramList = methods.get(methodName);
		if (paramList == null) {
			paramList = new ArrayList<String>();
			methods.put(methodName, paramList);
		}
		paramList.add(paramType);
	}

	public List<String> getMethodFields(String methodName) {
		List<String> paramList = methods.get(methodName);
		return paramList;
	}

	public List<String> getMethodNames() {
		List<String> methodList = new ArrayList<String>();
		for (String methodName : methods.keySet()) {
			methodList.add(methodName);
		}
		return methodList;
	}
}
