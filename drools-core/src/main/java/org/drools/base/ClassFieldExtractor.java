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
    private ClassObjectType objectType;
    private int             index;
    private FieldAccessor   accessor;

    public ClassFieldExtractor(Class clazz,
                               String fieldName) {
        try {
            FieldAccessorMap accessorMap = FieldAccessorGenerator.getInstance().getInstanceFor( clazz );
            this.accessor = accessorMap.getFieldAccessor();
            this.index = accessorMap.getIndex( fieldName );

            this.objectType = new ClassObjectType( getClassType( clazz,
                                                                 fieldName ) );
        } catch ( Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int getIndex() {
        return this.index;
    }

    public Object getValue(Object object) {
        return this.accessor.getFieldByIndex( object,
                                              this.index );
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

        // autobox primitives
        if ( fieldType.isPrimitive() ) {
            if (fieldType == char.class ) {
                fieldType = Character.class;
            } else if ( fieldType == byte.class ) {
                fieldType = Byte.class;
            } else if ( fieldType == short.class ) {
                fieldType = Short.class;
            } else if ( fieldType == int.class ) {
                fieldType = Integer.class;
            } else if ( fieldType == long.class ) {
                fieldType = Long.class;
            } else if ( fieldType == float.class ) {
                fieldType = Float.class;
            } else if ( fieldType == double.class ) {
                fieldType = Double.class;
            } else if ( fieldType == boolean.class ) {
                fieldType = Boolean.class;
            }
        }

        return fieldType;

    }
}