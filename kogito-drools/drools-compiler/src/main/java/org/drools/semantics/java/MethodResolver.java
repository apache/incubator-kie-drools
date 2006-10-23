package org.drools.semantics.java;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.RuntimeDroolsException;
import org.drools.base.resolvers.LiteralValue;
import org.drools.base.resolvers.ValueHandler;

/**
 * Determines the method to call, based on the given values
 *
 */
public class MethodResolver {

    private final Class  clazz;
    private final String name;
    private boolean      staticMethod;

    public MethodResolver(Class clazz,
                          String name) {
        super();
        this.clazz = clazz;
        this.name = name;
    }

    public Class getClazz() {
        return this.clazz;
    }

    public String getName() {
        return this.name;
    }

    public boolean isStaticMethod() {
        return this.staticMethod;
    }

    public Method resolveMethod(ValueHandler[] params) {
        Class[]  classes  = new Class[params.length];
        for ( int i = 0, length  = params.length; i  < length; i++ )  {
            classes[i]   = params[i].getExtractToClass();
        }
        
        return  resolveMethod(classes);
    }
    
    public Method resolveMethod(Class[] params) {
        Method method = null;
        
        boolean allDefined = true;

        //check for null params, so we know if all the types are defined
        for ( int i = 0, length = params.length; i < length; i++ ) {
            if ( params[i] == null ) {
                allDefined = false;
            }
        } 
        
        if ( allDefined ) {
            method = getMethod( this.clazz,
                                this.name,
                                params );
        } else {
            // For some reason all the types are not specified, so just match the first method with the same
            // number of arguments
            Method[] methods = getMethods( this.clazz, this.name, params.length );
            if ( methods != null && methods.length != 0 ) {
                method = methods[0];
            }
        }
        
        
        if ( method != null && (method.getModifiers() & Modifier.STATIC) == Modifier.STATIC ) {
            this.staticMethod = true;
        }

// @todo We could potentially output a warning here        
//        if ( (method.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC ) {
//            throw new RuntimeDroolsException( "Unable to call the private method [" + name + "] on class [" + clazz.getName() + "] for parameters " + arrayToString( params ) );
//        }
        return method;
    }

    /**
     * work out what method we will be calling at runtime, based on the name and number of parameters.
     */
    private Method getMethod(Class clazz,
                             String methodName,
                             Class[] args) {   

        // Fist get methods with same number of arguments
        Method[] methods = getMethods( clazz, methodName, args.length );
        if ( methods.length == 0 ) {
            return null;
        }

        // now iterate to find correct method
        for ( int i = 0, length = methods.length; i < length; i++ ) {
            if ( matchesTypes( methods[i].getParameterTypes(),
                               args ) ) {
                return methods[i];
            }
        }
        
        // Casting hasn't worked, so just return the first method with the same number of params
        return methods[0];
    }
    
    private Method[] getMethods(Class clazz,
                                String methodName,
                                int length) {
        List list = new ArrayList();     
        Method[] methods = clazz.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            if ( methods[i].getName().equals( methodName ) ) {
                if ( methods[i].getParameterTypes().length == length ) {
                    list.add( methods[i] );
                }
            }
        }     
        return ( Method[] ) list.toArray( new Method[ list.size() ] );
    }

    private boolean matchesTypes(Class[] methodClasses,
                                 Class[] argumentClasses) {
        for ( int i = 0, length = methodClasses.length; i < length; i++ ) {
            Class methodClass = methodClasses[i];
            Class argumentClass = argumentClasses[i];
            if ( methodClasses[i].isPrimitive() ) {
                // try matching to primitive
                if ( methodClass == int.class && argumentClass == Integer.class ) {
                    continue;
                } else if ( methodClass == long.class && argumentClass == Long.class ) {
                    continue;
                } else if ( methodClass == float.class && argumentClass == Float.class ) {
                    continue;
                } else if ( methodClass == double.class && argumentClass == Double.class ) {
                    continue;
                } else {
                    return false;
                }
            } else if ( methodClasses[i] != argumentClasses[i] ) {
                return false;
            }
        }
        return true;
    }

    private String arrayToString(Object[] values) {
        StringBuffer args = new StringBuffer();
        for ( int i = 0, length = values.length; i < length; i++ ) {
            args.append( "[" );
            args.append( values[i].getClass() );
            args.append( "]" );
            if ( i < length - 1 ) {
                args.append( ", " );
            }
        }
        return args.toString();
    }
    
}
