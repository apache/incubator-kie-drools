package org.codehaus.jfdi.interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

public class MethodInvoker {
	private Method method;

	private boolean isFunction;

	private final Object[] params;

	private Class[] parameterTypes;

	/**
	 * Method invoker
	 */
	public MethodInvoker(Method method, boolean isFunction, Object[] params) {
		this.method = method;
		this.params = params;
		this.isFunction = isFunction;
	}

	public Object invoke(Object instance) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		
		System.err.println( "invoke ::" + method  + " on " + instance );

		// None static methods cannot have a null instance
		if (!isFunction && instance == null) {
			throw new NullPointerException(
					"Cannot call the non-static method ["
							+ this.method.getName() + "] on the class ["
							+ this.method.getDeclaringClass().getName()
							+ " with a null instance");
		}

		Object result = null;

		System.err.println("invoke on " + instance);
		System.err.println("invoke of " + method);
		System.err.println("invoke with " + Arrays.asList( params ) );

		// now the actual invoking of the method
		result = this.method.invoke(instance, params);

		return result;
	}

	/**
	 * Attempt to convert text to the target class type
	 */
	private static Object convert(String text, Class type) {
		if (type == Integer.class || type == int.class) {
			return new Integer(text);
		} else if (text == "null") {
			return null;
		} else if (type == Character.class || type == char.class) {
			return (new Character(text.charAt(0)));
		} else if (type == Short.class || type == short.class) {
			return new Short(text);
		} else if (type == Long.class || type == long.class) {
			return new Long(text);
		} else if (type == Float.class || type == float.class) {
			return new Float(text);
		} else if (type == Double.class || type == double.class) {
			return new Double(text);
		} else if (type == Boolean.class || type == boolean.class) {
			return new Boolean(text);
		} else if (type == Date.class) {
			// return DateFactory.parseDate( text );
			throw new UnsupportedOperationException(
					"Whoops ! need to do dates !");
		} else if (type == BigDecimal.class) {
			return new BigDecimal(text);
		} else if (type == BigInteger.class) {
			return new BigInteger(text);
		} else {
			throw new IllegalArgumentException("Unable to convert [" + text
					+ "] to type: [" + type.getName() + "]");
		}
	}
}
