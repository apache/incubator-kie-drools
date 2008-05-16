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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import org.drools.common.InternalWorkingMemory;

/**
 * This is a wrapper for a ClassFieldExtractor that provides
 * default values and a simpler interface for non-used parameters
 * like the working memory, when the field extractor is used outside
 * the working memory scope.
 *
 * @author Edson Tirelli
 */
public class ClassFieldAccessor
    implements
    FieldAccessor, Externalizable {

    private static final long   serialVersionUID = 400L;
    private ClassFieldReader reader;
    private ClassFieldWriter writer;

    public ClassFieldAccessor() {
    }

    public ClassFieldAccessor(final ClassFieldReader reader, final ClassFieldWriter writer ) {
        this.reader = reader;
        this.writer = writer;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( reader );
        out.writeObject( writer );
    }

    public void readExternal(final ObjectInput is) throws ClassNotFoundException,
                                                  IOException {
        this.reader = (ClassFieldReader) is.readObject();
        this.writer = (ClassFieldWriter) is.readObject();
    }

    public int getIndex() {
        return this.reader.getIndex();
    }

    public String getFieldName() {
        return this.reader.getFieldName();
    }

    public Object getValue(final Object object) {
        return this.reader.getValue( null,
                                        object );
    }

    public ValueType getValueType() {
        return this.reader.getValueType();
    }

    public Class< ? > getExtractToClass() {
        return this.reader.getExtractToClass();
    }

    public String getExtractToClassName() {
        return this.reader.getExtractToClassName();
    }

    public String toString() {
        return this.reader.toString();
    }

    public int hashCode() {
        return this.reader.hashCode();
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof ClassFieldAccessor) ) {
            return false;
        }

        final ClassFieldAccessor other = (ClassFieldAccessor) object;

        return this.reader.equals( other.reader );
    }

    public boolean getBooleanValue(final Object object) {
        return this.reader.getBooleanValue( null,
                                               object );
    }

    public byte getByteValue(final Object object) {
        return this.reader.getByteValue( null,
                                            object );
    }

    public char getCharValue(final Object object) {
        return this.reader.getCharValue( null,
                                            object );
    }

    public double getDoubleValue(final Object object) {
        return this.reader.getDoubleValue( null,
                                              object );
    }

    public float getFloatValue(final Object object) {
        return this.reader.getFloatValue( null,
                                             object );
    }

    public int getIntValue(final Object object) {
        return this.reader.getIntValue( null,
                                           object );
    }

    public long getLongValue(final Object object) {
        return this.reader.getLongValue( null,
                                            object );
    }

    public short getShortValue(final Object object) {
        return this.reader.getShortValue( null,
                                             object );
    }

    public boolean isNullValue(final Object object) {
        return this.reader.isNullValue( null,
                                           object );
    }

    public Method getNativeReadMethod() {
        return this.reader.getNativeReadMethod();
    }

    public int getHashCode(final Object object) {
        return this.reader.getHashCode( null,
                                           object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getBooleanValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   Object object) {
        return reader.getBooleanValue( workingMemory,
                                       object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getByteValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public byte getByteValue(InternalWorkingMemory workingMemory,
                             Object object) {
        return reader.getByteValue( workingMemory,
                                    object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getCharValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public char getCharValue(InternalWorkingMemory workingMemory,
                             Object object) {
        return reader.getCharValue( workingMemory,
                                    object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getDoubleValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 Object object) {
        return reader.getDoubleValue( workingMemory,
                                      object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getFloatValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public float getFloatValue(InternalWorkingMemory workingMemory,
                               Object object) {
        return reader.getFloatValue( workingMemory,
                                     object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getHashCode(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public int getHashCode(InternalWorkingMemory workingMemory,
                           Object object) {
        return reader.getHashCode( workingMemory,
                                   object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getIntValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public int getIntValue(InternalWorkingMemory workingMemory,
                           Object object) {
        return reader.getIntValue( workingMemory,
                                   object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getLongValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public long getLongValue(InternalWorkingMemory workingMemory,
                             Object object) {
        return reader.getLongValue( workingMemory,
                                    object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getShortValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public short getShortValue(InternalWorkingMemory workingMemory,
                               Object object) {
        return reader.getShortValue( workingMemory,
                                     object );
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#getValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public Object getValue(InternalWorkingMemory workingMemory,
                           Object object) {
        return reader.getValue( workingMemory,
                                object );
    }

    /**
     * @return
     * @see org.drools.base.ClassFieldReader#isGlobal()
     */
    public boolean isGlobal() {
        return reader.isGlobal();
    }

    /**
     * @param workingMemory
     * @param object
     * @return
     * @see org.drools.base.ClassFieldReader#isNullValue(org.drools.common.InternalWorkingMemory, java.lang.Object)
     */
    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               Object object) {
        return reader.isNullValue( workingMemory,
                                   object );
    }

    /**
     * @return
     * @see org.drools.base.ClassFieldWriter#getFieldType()
     */
    public Class< ? > getFieldType() {
        return writer.getFieldType();
    }

    /**
     * @return
     * @see org.drools.base.ClassFieldWriter#getNativeWriteMethod()
     */
    public Method getNativeWriteMethod() {
        return writer.getNativeWriteMethod();
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setBooleanValue(java.lang.Object, boolean)
     */
    public void setBooleanValue(Object bean,
                                boolean value) {
        writer.setBooleanValue( bean,
                                value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setByteValue(java.lang.Object, byte)
     */
    public void setByteValue(Object bean,
                             byte value) {
        writer.setByteValue( bean,
                             value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setCharValue(java.lang.Object, char)
     */
    public void setCharValue(Object bean,
                             char value) {
        writer.setCharValue( bean,
                             value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setDoubleValue(java.lang.Object, double)
     */
    public void setDoubleValue(Object bean,
                               double value) {
        writer.setDoubleValue( bean,
                               value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setFloatValue(java.lang.Object, float)
     */
    public void setFloatValue(Object bean,
                              float value) {
        writer.setFloatValue( bean,
                              value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setIntValue(java.lang.Object, int)
     */
    public void setIntValue(Object bean,
                            int value) {
        writer.setIntValue( bean,
                            value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setLongValue(java.lang.Object, long)
     */
    public void setLongValue(Object bean,
                             long value) {
        writer.setLongValue( bean,
                             value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setShortValue(java.lang.Object, short)
     */
    public void setShortValue(Object bean,
                              short value) {
        writer.setShortValue( bean,
                              value );
    }

    /**
     * @param bean
     * @param value
     * @see org.drools.base.ClassFieldWriter#setValue(java.lang.Object, java.lang.Object)
     */
    public void setValue(Object bean,
                         Object value) {
        writer.setValue( bean,
                         value );
    }

    
}