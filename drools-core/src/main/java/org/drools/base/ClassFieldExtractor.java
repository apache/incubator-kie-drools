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

import org.drools.RuntimeDroolsException;
import org.drools.common.DroolsObjectInput;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.FieldExtractor;
import org.drools.util.ClassUtils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

/**
 * This provides access to fields, and what their numerical index/object type is.
 * This is basically a wrapper class around dynamically generated subclasses of
 * BaseClassFieldExtractor,
 *  which allows serialization by regenerating the accessor classes
 * when needed.
 *
 * @author Michael Neale
 */
public class ClassFieldExtractor
    implements
    FieldExtractor {
    /**
     *
     */
    private static final long        serialVersionUID = 400L;
    private String                   fieldName;
    private Class                    clazz;
    private transient FieldExtractor extractor;

    public ClassFieldExtractor() {

    }

    public ClassFieldExtractor(final Class clazz,
                               final String fieldName) {
        this( clazz,
              fieldName,
              clazz.getClassLoader() );
    }

    public ClassFieldExtractor(final Class clazz,
                               final String fieldName,
                               final ClassLoader classLoader) {
        this( clazz,
              fieldName,
              classLoader == null ? clazz.getClassLoader() : classLoader,
              new ClassFieldExtractorFactory() );
    }

    public ClassFieldExtractor(final Class clazz,
                               final String fieldName,
                               final ClassLoader classLoader,
                               final ClassFieldExtractorFactory factory) {
        this.clazz = clazz;
        this.fieldName = fieldName;
        init( classLoader,
              factory );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // Call even if there is no default serializable fields.
        out.writeObject(clazz);
        out.writeObject(fieldName);
    }

    public void readExternal(final ObjectInput is) throws ClassNotFoundException,
                                                       IOException {
        clazz   = (Class)is.readObject();
        fieldName   = (String)is.readObject();
        if (is instanceof DroolsObjectInput) {
            DroolsObjectInput   droolsInput = (DroolsObjectInput)is;
            extractor = droolsInput.getExtractorFactory().getExtractor( clazz,
                                                                        fieldName,
                                                                        droolsInput.getClassLoader() );
        }
        else
            extractor   = ClassFieldExtractorCache.getInstance().getExtractor( clazz, fieldName,
                                                                               getClass().getClassLoader());
    }

//    private Object readResolve() {
//        // always return the value from the cache
//        return ClassFieldExtractorCache.getInstance().getExtractor( this.clazz,
//                                                                    this.fieldName,
//                                                                    this.clazz.getClassLoader() );
//    }

    private void init(final ClassLoader classLoader,
                      final ClassFieldExtractorFactory factory) {
        try {
            this.extractor = factory.getClassFieldExtractor( this.clazz,
                                                             this.fieldName,
                                                             classLoader );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( e );
        }
    }

    public int getIndex() {
        return this.extractor.getIndex();
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public Object getValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.extractor.getValue( workingMemory,
                                        object );
    }

    public ValueType getValueType() {
        return this.extractor.getValueType();
    }

    public Class getExtractToClass() {
        return this.extractor.getExtractToClass();
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( this.extractor.getExtractToClass() );
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

        if ( object == null || !(object instanceof ClassFieldExtractor) ) {
            return false;
        }

        final ClassFieldExtractor other = (ClassFieldExtractor) object;

        return this.extractor.getValueType() == other.getValueType() && this.extractor.getIndex() == other.getIndex();
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
        return this.extractor.getBooleanValue( workingMemory,
                                               object );
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.extractor.getByteValue( workingMemory,
                                            object );
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.extractor.getCharValue( workingMemory,
                                            object );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        return this.extractor.getDoubleValue( workingMemory,
                                              object );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.extractor.getFloatValue( workingMemory,
                                             object );
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.extractor.getIntValue( workingMemory,
                                           object );
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        return this.extractor.getLongValue( workingMemory,
                                            object );
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.extractor.getShortValue( workingMemory,
                                             object );
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return this.extractor.isNullValue( workingMemory,
                                           object );
    }

    public Method getNativeReadMethod() {
        return this.extractor.getNativeReadMethod();
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        return this.extractor.getHashCode( workingMemory,
                                           object );
    }

    public boolean isGlobal() {
        return false;
    }
}