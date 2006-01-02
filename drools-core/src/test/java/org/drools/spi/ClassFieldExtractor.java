package org.drools.spi;

import java.beans.IntrospectionException;
import java.beans.Introspector;

import org.drools.spi.FieldExtractor;

/**
 * Should be able to extract field values for a given index
 * 
 */
public class ClassFieldExtractor
    implements
    FieldExtractor {
    private Class clazz;
    private int   index;

    public ClassFieldExtractor(Class clazz,
                               int index) {
        this.clazz = clazz;
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    public Object getValue(Object object) {
        Object value = null;
        try {
            value = Introspector.getBeanInfo( this.clazz ).getPropertyDescriptors()[this.index].getReadMethod().invoke( object,
                                                                                                                        null );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
        return value;
    }

    public Class getValueType() {
        Class clazz = null;
        try {
            clazz = Introspector.getBeanInfo( this.clazz ).getPropertyDescriptors()[this.index].getPropertyType();
        } catch ( IntrospectionException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return clazz;
    }

}