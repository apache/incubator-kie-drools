package org.drools.base;

/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import org.drools.spi.WriteAccessor;

/**
 * This class implements the WriteAccessor interface
 * allowing the application to write values into a field
 * from a class
 *
 * @author Edson Tirelli
 */
public class ClassFieldWriter
    implements
    WriteAccessor {

    private static final long       serialVersionUID = 400L;
    private String                  className;
    private String                  fieldName;
    private transient WriteAccessor writer;

    public ClassFieldWriter() {

    }
    
    public ClassFieldWriter(final String className,
                            final String fieldName) {
        this.className = className;
        this.fieldName = fieldName;
//        this.clazz = clazz;
//        this.fieldName = fieldName;
//        init( findClassLoader( classLoader ),
//              factory );
    }    

//    public ClassFieldWriter(final Class< ? > clazz,
//                            final String fieldName,
//                            final ClassLoader classLoader,
//                            final ClassFieldAccessorFactory factory) {
//        this.clazz = clazz;
//        this.fieldName = fieldName;
//        init( findClassLoader( classLoader ),
//              factory );
//    }

//    /**
//     * @param classLoader
//     */
//    private ClassLoader findClassLoader(final ClassLoader classLoader) {
//        ClassLoader loader = classLoader;
//        if ( loader == null ) {
//            loader = Thread.currentThread().getContextClassLoader();
//            if ( loader == null ) {
//                loader = this.getClass().getClassLoader();
//            }
//        }
//        return loader;
//    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( className );
        out.writeObject( fieldName );
    }

    public void readExternal(final ObjectInput is) throws ClassNotFoundException,
                                                  IOException {
        className = (String) is.readObject();
        fieldName = (String) is.readObject();
    }
//
//    private void init(final ClassLoader classLoader,
//                      final ClassFieldAccessorFactory factory) {
//        try {
//            this.writer = factory.getClassFieldWriter( this.clazz,
//                                                       this.fieldName,
//                                                       classLoader );
//        } catch ( final Exception e ) {
//            throw new RuntimeDroolsException( e );
//        }
//    }
    
    public void setWriteAccessor(WriteAccessor writer) {
        this.writer = writer;
    }    

    public int getIndex() {
        return this.writer.getIndex();
    }
    
    public String getClassName() {
        return this.className;
    }    

    public String getFieldName() {
        return this.fieldName;
    }

    public String toString() {
        return "[ClassFieldWriter class=" + this.className + " field=" + this.fieldName + "]";
    }

    public int hashCode() {
        return getValueType().hashCode() * 17 + getIndex();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof ClassFieldWriter) ) {
            return false;
        }

        final ClassFieldWriter other = (ClassFieldWriter) object;

        return this.writer.getValueType() == other.getValueType() && this.writer.getIndex() == other.getIndex();
    }

    public Class< ? > getFieldType() {
        return writer.getFieldType();
    }

    public Method getNativeWriteMethod() {
        return writer.getNativeWriteMethod();
    }

    public ValueType getValueType() {
        return writer.getValueType();
    }

    public void setBooleanValue(Object bean,
                                boolean value) {
        writer.setBooleanValue( bean, value );
    }

    public void setByteValue(Object bean,
                             byte value) {
        writer.setByteValue( bean, value );

    }

    public void setCharValue(Object bean,
                             char value) {
        writer.setCharValue( bean, value );
    }

    public void setDoubleValue(Object bean,
                               double value) {
        writer.setDoubleValue( bean, value );
    }

    public void setFloatValue(Object bean,
                              float value) {
        writer.setFloatValue( bean, value );
    }

    public void setIntValue(Object bean,
                            int value) {
        writer.setIntValue( bean, value );
    }

    public void setLongValue(Object bean,
                             long value) {
        writer.setLongValue( bean, value );
    }

    public void setShortValue(Object bean,
                              short value) {
        writer.setShortValue( bean, value );
    }

    public void setValue(Object bean,
                         Object value) {
        writer.setValue( bean, value );
    }

}