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

import org.drools.RuntimeDroolsException;
import org.drools.common.DroolsObjectInput;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ReadAccessor;
import org.drools.util.ClassUtils;

/**
 * This provides access to fields, and what their numerical index/object type is.
 * This is basically a wrapper class around dynamically generated subclasses of
 * BaseClassFieldExtractor,
 *  which allows serialization by regenerating the accessor classes
 * when needed.
 *
 * @author Michael Neale
 */
public class ClassFieldReader
    implements
    InternalReadAccessor {
    /**
     *
     */
    private static final long              serialVersionUID = 400L;
    private String                         fieldName;
    private Class< ? >                     clazz;
    private transient InternalReadAccessor reader;

    public ClassFieldReader() {

    }

    public ClassFieldReader(final Class< ? > clazz,
                            final String fieldName) {
        this( clazz,
              fieldName,
              clazz.getClassLoader() );
    }

    public ClassFieldReader(final Class< ? > clazz,
                            final String fieldName,
                            final ClassLoader classLoader) {
        this( clazz,
              fieldName,
              classLoader == null ? clazz.getClassLoader() : classLoader,
              new ClassFieldAccessorFactory() );
    }

    public ClassFieldReader(final Class< ? > clazz,
                            final String fieldName,
                            final ClassLoader classLoader,
                            final ClassFieldAccessorFactory factory) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        init( classLoader,
              factory );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // Call even if there is no default serializable fields.
        out.writeObject( clazz.getName() );
        out.writeObject( fieldName );
    }

    public void readExternal(final ObjectInput is) throws ClassNotFoundException,
                                                  IOException {
        String clsName = (String) is.readObject();
        fieldName = (String) is.readObject();
        if ( is instanceof DroolsObjectInput ) {
            DroolsObjectInput droolsInput = (DroolsObjectInput) is;
            this.clazz = droolsInput.getClassLoader().loadClass( clsName );
            reader = droolsInput.getExtractorFactory().getReader( clazz,
                                                                     fieldName,
                                                                     droolsInput.getClassLoader() );
        } else {
            this.clazz = getClass().getClassLoader() .loadClass( clsName );            
            reader = ClassFieldAccessorCache.getInstance().getReader( clazz,        
                                                                            fieldName,
                                                                            getClass().getClassLoader() );
        }
    }

    private void init(final ClassLoader classLoader,
                      final ClassFieldAccessorFactory factory) {
        try {
            this.reader = factory.getClassFieldReader( this.clazz,
                                                          this.fieldName,
                                                          classLoader );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int getIndex() {
        return this.reader.getIndex();
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Object getValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.reader.getValue( workingMemory,
                                        object );
    }

    public ValueType getValueType() {
        return this.reader.getValueType();
    }

    public Class< ? > getExtractToClass() {
        return this.reader.getExtractToClass();
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( this.reader.getExtractToClass() );
    }

    public String toString() {
        return "[ClassFieldExtractor class=" + this.clazz + " field=" + this.fieldName + "]";
    }

    public int hashCode() {
        return getValueType().hashCode() * 17 + getIndex();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof ClassFieldReader) ) {
            return false;
        }

        final ClassFieldReader other = (ClassFieldReader) object;

        return this.reader.getValueType() == other.getValueType() && this.reader.getIndex() == other.getIndex();
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
        return this.reader.getBooleanValue( workingMemory,
                                               object );
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.reader.getByteValue( workingMemory,
                                            object );
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.reader.getCharValue( workingMemory,
                                            object );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        return this.reader.getDoubleValue( workingMemory,
                                              object );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.reader.getFloatValue( workingMemory,
                                             object );
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.reader.getIntValue( workingMemory,
                                           object );
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.reader.getLongValue( workingMemory,
                                            object );
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.reader.getShortValue( workingMemory,
                                             object );
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.reader.isNullValue( workingMemory,
                                           object );
    }

    public Method getNativeReadMethod() {
        return this.reader.getNativeReadMethod();
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.reader.getHashCode( workingMemory,
                                           object );
    }

    public boolean isGlobal() {
        return false;
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getBooleanValue(java.lang.Object)
     */
    public boolean getBooleanValue(Object object) {
        return reader.getBooleanValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getByteValue(java.lang.Object)
     */
    public byte getByteValue(Object object) {
        return reader.getByteValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getCharValue(java.lang.Object)
     */
    public char getCharValue(Object object) {
        return reader.getCharValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getDoubleValue(java.lang.Object)
     */
    public double getDoubleValue(Object object) {
        return reader.getDoubleValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getFloatValue(java.lang.Object)
     */
    public float getFloatValue(Object object) {
        return reader.getFloatValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getHashCode(java.lang.Object)
     */
    public int getHashCode(Object object) {
        return reader.getHashCode( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getIntValue(java.lang.Object)
     */
    public int getIntValue(Object object) {
        return reader.getIntValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getLongValue(java.lang.Object)
     */
    public long getLongValue(Object object) {
        return reader.getLongValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getShortValue(java.lang.Object)
     */
    public short getShortValue(Object object) {
        return reader.getShortValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#getValue(java.lang.Object)
     */
    public Object getValue(Object object) {
        return reader.getValue( object );
    }

    /**
     * @param object
     * @return
     * @see org.drools.spi.ReadAccessor#isNullValue(java.lang.Object)
     */
    public boolean isNullValue(Object object) {
        return reader.isNullValue( object );
    }
    
    
}