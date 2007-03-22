package org.drools.clp.functions;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.drools.clp.BaseFunction;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.SlotNameValuePair;
import org.drools.clp.ValueHandler;
import org.drools.clp.VariableValueHandler;

public class ModifyFunction  implements Function {
    private static final String name = "modify"; 
    
    public ModifyFunction() {
    }


    public Object execute(ValueHandler[] args, ExecutionContext context) {
        // This is very slow, its just to get things working for now
        Object object = args[0].getValue( context );
        
        try {
            for ( int i = 1, length = args.length; i < length; i++ ) {
                SlotNameValuePair pair = ( SlotNameValuePair ) args[i];
                Method method = getField( object.getClass(), pair.getName() ); 
                method.invoke( object, new Object[] { pair.getValueHandler().getValue( context ) } );
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
                
        return null;
    }
    
    public Method getField(Class clazz, String name) throws IntrospectionException {
        PropertyDescriptor[] descrs = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0, length = descrs.length; i < length; i++ ) {
            if ( descrs[i].getName().equals( name ) ) {
                return descrs[i].getWriteMethod();
            }
        }
        return null;
    }
    
    public String getName() {
        return name;
    }

}
