package org.drools.spi;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.drools.spi.FieldExtractor;

/**
 * Should be able to extract field values for a given index
 * 
 */
public class ClassFieldExtractor
    implements
    FieldExtractor {
    private Class clazz;
    private ClassObjectType objectType;
    private Method method;
    private int             index;

    public ClassFieldExtractor(Class clazz,
                               int index) {
        this.clazz = clazz;
        this.objectType = new ClassObjectType( getClassType( clazz,
                                                             index ) );
        this.index = index;
        try {
            this.method  = Introspector.getBeanInfo( this.clazz ).getPropertyDescriptors()[this.index].getReadMethod();
        }
        catch ( IntrospectionException e ) {
            throw new RuntimeException( e );
        }
    }

    public int getIndex() {
        return this.index;
    }

    public Object getValue(Object object) {
        Object value = null;
        try {
            value = this.method.invoke( object, (Object[]) null );
        }
        catch ( IllegalArgumentException e ) {
            throw new RuntimeException( e );
        }
        catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
        catch ( InvocationTargetException e ) {
            throw new RuntimeException( e );
        }
        return value;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    private Class getClassType(Class clazz,
                               int index) {
        Class fieldClass = null;
        try {
            fieldClass = Introspector.getBeanInfo( clazz ).getPropertyDescriptors()[index].getPropertyType();
        } catch ( IntrospectionException e ) {
            e.printStackTrace();
        }
        return fieldClass;
    }
}