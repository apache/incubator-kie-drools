package org.drools.guvnor.server.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.modeldriven.MethodInfo;
import org.drools.guvnor.server.rules.ClassToGenericClassConverter;

/**
 * 
 * Finds all methods that are not getters or setters from a class.
 * 
 * @author Nicolas Heron
 * @author Toni Rikkola
 *
 */
public class ClassMethodInspector {

    private final Set<MethodInfo> methods = new HashSet<MethodInfo>();

    public ClassMethodInspector(final Class< ? > clazz, ClassToGenericClassConverter converter) throws IOException {
        Method[] methods = clazz.getDeclaredMethods();

        for ( int i = 0; i < methods.length; i++ ) {
            Method aMethod = methods[i];
            int modifiers = methods[i].getModifiers();
            String methodName = aMethod.getName();

            if ( isNotGetterOrSetter(aMethod) && isReasonableMethod( clazz,
            		methodName ) && Modifier.isPublic( modifiers ) ) {

                Class<?>[] listParam = aMethod.getParameterTypes();
                
				MethodInfo info = new MethodInfo(methodName,
						solveParams(listParam), 
						aMethod.getReturnType(),
						SuggestionCompletionEngineBuilder
								.obtainGenericType(aMethod.getGenericReturnType()),
						converter.translateClassToGenericType(clazz));
				this.methods.add(info);
            }
        }
    }

    /**
     * Not all methods make sense when shown in drop downs. Like toArray, hashCode, size. 
     * Methods can only be called or used to set something. Reasonable methods examples: clean, set, add.
     * 
     * @param clazz
     * @param methodName
     * @return
     */
    private boolean isReasonableMethod(Class< ? > clazz,
                                       String methodName) {
        if ( "hashCode".equals( methodName ) || "equals".equals( methodName ) ) {
            return false;
        }

        if ( Collection.class.isAssignableFrom( clazz ) ) {
            if ( checkCollectionMethods( methodName ) ) {
                return false;
            }
        }

        if ( Set.class.isAssignableFrom( clazz ) ) {
            if ( checkCollectionMethods( methodName ) ) {
                return false;
            }
        }

        if ( List.class.isAssignableFrom( clazz ) ) {

            if ( checkCollectionMethods( methodName ) || "get".equals( methodName ) || "listIterator".equals( methodName ) || "lastIndexOf".equals( methodName ) || "indexOf".equals( methodName ) || "subList".equals( methodName ) ) {
                return false;
            }
        }

        if ( Map.class.isAssignableFrom( clazz ) ) {
            if ( "get".equals( methodName ) || "isEmpty".equals( methodName ) || "containsKey".equals( methodName ) || "values".equals( methodName ) || "entrySet".equals( methodName ) || "containsValue".equals( methodName )
                 || "keySet".equals( methodName ) || "size".equals( methodName ) ) {
                return false;
            }
        }

        return true;
    }

    private boolean checkCollectionMethods(String methodName) {
        return ("toArray".equals( methodName ) || "iterator".equals( methodName ) || "contains".equals( methodName ) || "isEmpty".equals( methodName ) || "containsAll".equals( methodName ) || "size".equals( methodName ));
    }

    private List<String> solveParams(Class< ? >[] listParam) {
        List<String> params = new ArrayList<String>();

        if ( listParam.length == 0 ) {
            return params;
        } else {

            for ( int i = 0; i < listParam.length; i++ ) {
                params.add( listParam[i].getName().substring( listParam[i].getName().lastIndexOf( "." ) + 1 ) );
            }

            return params;
        }
    }

    /**
     * Check if this method is a mutator or accessor method for a field.
     * 
     * If method starts with set or get and is longer than 3 characters. 
     * For example java.util.List.set(int index, Object element) is considered to be a method, not a setter.
     * 
     * @param methodName
     */
    private boolean isNotGetterOrSetter(Method m) {
    	String name = m.getName();
        return !((name.length() > 3 && (name.startsWith( "set" ) || name.startsWith( "get" ))) ||
        	(name.length() > 2 && name.startsWith("is") 
        			&& (Boolean.class.isAssignableFrom(m.getReturnType()) || Boolean.TYPE == m.getReturnType())));
        
    }

    public List<String> getMethodFields(String methodName) {
        List<String> paramList = new ArrayList<String>();

        for ( String string : paramList ) {
            paramList.add( string );
        }

        return paramList;
    }

    public List<String> getMethodNames() {
        List<String> methodList = new ArrayList<String>();
        for ( MethodInfo info : methods ) {
            methodList.add( info.getName() );
        }
        return methodList;
    }

    public List<MethodInfo> getMethodInfos() {
        return new ArrayList<MethodInfo>( this.methods );
    }

}
