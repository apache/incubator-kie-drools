package org.codehaus.jfdi.interpreter.operations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.codehaus.jfdi.interpreter.MethodInvoker;
import org.codehaus.jfdi.interpreter.MethodResolver;

public class MethodCall implements Expr {

	private Expr obj;
	private String name;
	private Expr[] params;
	
	private Method method;

	public MethodCall(Expr obj, String name, Expr[] params) {
		this.obj = obj;
		this.name = name;
		this.params = params;
		Class[] paramTypes = new Class[ params.length ];
		for ( int i = 0 ; i < paramTypes.length ; ++i ) {
			paramTypes[ i ] = params[ i ].getType();
		}
		System.err.println( "obj.getType(): " + obj.getType() );
		System.err.println( "name: " + name );
		this.method = MethodResolver.getInstance().resolveMethod( obj.getType(), name, paramTypes );
		System.err.println( "method: " + method );
	}

	public Object getValue() {
		Object thisObj = obj.getValue();
		Object[] paramObjs = new Object[ params.length ];
		
		for ( int i = 0 ; i < params.length ; ++i ) {
			paramObjs[i] = params[i].getValue();
		}
		
		System.err.println( "CALL " + name + " " + Arrays.asList( paramObjs ) );
		MethodInvoker invoker = new MethodInvoker(  method, false, paramObjs );
		try {
			return invoker.invoke( thisObj );
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	public Class getType() {
		return method.getReturnType();
	}

}
