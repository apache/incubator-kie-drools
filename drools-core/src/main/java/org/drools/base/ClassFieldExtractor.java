package org.drools.base;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.drools.RuntimeDroolsException;
import org.drools.spi.FieldExtractor;
import org.drools.spi.ObjectType;
import org.drools.util.asm.FieldAccessor;
import org.drools.util.asm.FieldAccessorGenerator;
import org.drools.util.asm.FieldAccessorMap;

/**
 * Should be able to extract field values for a given index
 * 
 */
public class ClassFieldExtractor
    implements
    FieldExtractor {
    private Class           clazz;
    private ClassObjectType objectType;
    private Method          method;
    private int             index;
    private FieldAccessor   accessor;

    public ClassFieldExtractor(Class clazz,
                               String fieldName) {
        try {
            this.clazz = clazz;
            FieldAccessorMap accessorMap = FieldAccessorGenerator.getInstance().getInstanceFor( clazz );
            this.accessor = accessorMap.getFieldAccessor();
            this.index = accessorMap.getIndex( fieldName );

            this.objectType = new ClassObjectType( getClassType( clazz,
                                                                 fieldName ) );

            this.method = Introspector.getBeanInfo( this.clazz ).getPropertyDescriptors()[this.index].getReadMethod();
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int getIndex() {
        return this.index;
    }

    public Object getValue(Object object) {
        return this.accessor.getFieldByIndex( object, this.index );
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    private Class getClassType(Class clazz,
                               String name) throws IntrospectionException {
        Class fieldType = null;
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                fieldType = descriptors[i].getPropertyType();
                break;
            }
        }
        return fieldType;

    }
}