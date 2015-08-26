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

package org.drools.core.base;

import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.AcceptsWriteAccessor;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.WriteAccessor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * This is a wrapper for a ClassFieldExtractor that provides
 * default values and a simpler interface for non-used parameters
 * like the working memory, when the field extractor is used outside
 * the working memory scope.
 */
public class ClassFieldAccessor
    implements
    AcceptsReadAccessor,
    AcceptsWriteAccessor,
    FieldAccessor,
    Externalizable {

    private static final long serialVersionUID = 510l;
    private ClassFieldReader  reader;
    private ClassFieldWriter  writer;

    public ClassFieldAccessor() {
    }

    public ClassFieldAccessor(final ClassFieldReader reader,
                              final ClassFieldWriter writer) {
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

    public void setReadAccessor(InternalReadAccessor readAccessor) {
        this.reader = (ClassFieldReader) readAccessor;
    }

    public void setWriteAccessor(WriteAccessor writeAccessor) {
        this.writer = (ClassFieldWriter) writeAccessor;
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

    @Override
    public int hashCode() {
        return reader.getClassName().hashCode() ^ reader.getFieldName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof ClassFieldAccessor) ) return false;
        ClassFieldAccessor other = (ClassFieldAccessor) obj;
        if ( reader == null ) {
            if ( other.reader != null ) return false;
        } else if ( !reader.getClassName().equals( other.reader.getClassName() ) || !reader.getFieldName().equals( other.reader.getFieldName() ) ) return false;
        if ( writer == null ) {
            if ( other.writer != null ) return false;
        } else if ( !writer.getClassName().equals( other.writer.getClassName() ) || !writer.getFieldName().equals( other.writer.getFieldName() ) ) return false;
        return true;
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

    public String getNativeReadMethodName() {
        return this.reader.getNativeReadMethodName();
    }

    public int getHashCode(final Object object) {
        return this.reader.getHashCode( null,
                                        object );
    }

    public boolean isGlobal() {
        return reader.isGlobal();
    }

    public Class< ? > getFieldType() {
        return writer.getFieldType();
    }

    public Method getNativeWriteMethod() {
        return writer.getNativeWriteMethod();
    }

    public void setBooleanValue(Object bean,
                                boolean value) {
        writer.setBooleanValue( bean,
                                value );
    }

    public void setByteValue(Object bean,
                             byte value) {
        writer.setByteValue( bean,
                             value );
    }

    public void setCharValue(Object bean,
                             char value) {
        writer.setCharValue( bean,
                             value );
    }

    public void setDoubleValue(Object bean,
                               double value) {
        writer.setDoubleValue( bean,
                               value );
    }

    public void setFloatValue(Object bean,
                              float value) {
        writer.setFloatValue( bean,
                              value );
    }

    public void setIntValue(Object bean,
                            int value) {
        writer.setIntValue( bean,
                            value );
    }

    public void setLongValue(Object bean,
                             long value) {
        writer.setLongValue( bean,
                             value );
    }

    public void setShortValue(Object bean,
                              short value) {
        writer.setShortValue( bean,
                              value );
    }

    public void setValue(Object bean,
                         Object value) {
        writer.setValue( bean,
                         value );
    }

    public BigDecimal getBigDecimalValue(Object object) {
        return reader.getBigDecimalValue( object );
    }

    public BigInteger getBigIntegerValue(Object object) {
        return reader.getBigIntegerValue( object );
    }

    public void setBigDecimalValue(Object bean,
                                   BigDecimal value) {
        writer.setBigDecimalValue( bean,
                                   value );
    }

    public void setBigIntegerValue(Object bean,
                                   BigInteger value) {
        writer.setBigIntegerValue( bean,
                                   value );
    }

}
