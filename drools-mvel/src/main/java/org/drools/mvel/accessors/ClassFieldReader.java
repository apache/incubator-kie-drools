/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.accessors;

import org.drools.base.base.AccessorKey;
import org.drools.base.base.AccessorKeySupplier;
import org.drools.base.base.ValueResolver;
import org.drools.base.base.ValueType;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.core.base.FieldNameSupplier;
import org.drools.util.ClassUtils;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import static org.drools.util.StringUtils.lcFirstForBean;

/**
 * This provides access to fields, and what their numerical index/object type is.
 * This is basically a wrapper class around dynamically generated subclasses of
 * BaseClassFieldExtractor,
 *  which allows serialization by regenerating the accessor classes
 * when needed.
 */
public class ClassFieldReader implements Externalizable, ReadAccessor, FieldNameSupplier, AccessorKeySupplier {
    private static final long              serialVersionUID = 510l;
    private String                         className;
    private String                         fieldName;
    private transient ReadAccessor reader;

    public ClassFieldReader() {

    }

    public ClassFieldReader(final String className,
                            final String fieldName) {
        this.className = className;
        this.fieldName = lcFirstForBean(fieldName);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( className );
        out.writeObject( fieldName );
    }

    public void readExternal(final ObjectInput is) throws ClassNotFoundException,
                                                  IOException {
        className = (String) is.readObject();
        fieldName = (String) is.readObject();
    }

    public void setReadAccessor(ReadAccessor reader) {
        this.reader = reader;
    }

    public int getIndex() {
        return this.reader.getIndex();
    }

    public String getClassName() {
        return this.className;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public boolean hasReadAccessor() {
        return this.reader != null;
    }
    
    public Object getValue(final ValueResolver valueResolver,
                           final Object object) {
        return this.reader.getValue( valueResolver,
                                     object );
    }

    public ValueType getValueType() {
        return this.reader.getValueType();
    }

    public Class< ? > getExtractToClass() {
        return reader != null ? reader.getExtractToClass() : null;
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( this.reader.getExtractToClass() );
    }

    public String toString() {
        return "[ClassFieldExtractor class=" + this.className + " field=" + this.fieldName + "]";
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !(obj instanceof ClassFieldReader) ) return false;
        ClassFieldReader other = (ClassFieldReader) obj;
        if ( className == null ) {
            if ( other.className != null ) return false;
        } else if ( !className.equals( other.className ) ) return false;
        if ( fieldName == null ) {
            if ( other.fieldName != null ) return false;
        } else if ( !fieldName.equals( other.fieldName ) ) return false;
        return true;
    }

    public boolean getBooleanValue(final ValueResolver valueResolver,
                                   final Object object) {
        return this.reader.getBooleanValue( valueResolver,
                                            object );
    }

    public byte getByteValue(final ValueResolver valueResolver,
                             final Object object) {
        return this.reader.getByteValue( valueResolver,
                                         object );
    }

    public char getCharValue(ValueResolver valueResolver,
                             final Object object) {
        return this.reader.getCharValue( valueResolver,
                                         object );
    }

    public double getDoubleValue(ValueResolver valueResolver,
                                 final Object object) {
        return this.reader.getDoubleValue( valueResolver,
                                           object );
    }

    public float getFloatValue(ValueResolver valueResolver,
                               final Object object) {
        return this.reader.getFloatValue( valueResolver,
                                          object );
    }

    public int getIntValue(ValueResolver valueResolver,
                           final Object object) {
        return this.reader.getIntValue( valueResolver,
                                        object );
    }

    public long getLongValue(ValueResolver valueResolver,
                             final Object object) {
        return this.reader.getLongValue( valueResolver,
                                         object );
    }

    public short getShortValue(ValueResolver valueResolver,
                               final Object object) {
        return this.reader.getShortValue( valueResolver,
                                          object );
    }

    public boolean isNullValue(ValueResolver valueResolver,
                               final Object object) {
        return this.reader.isNullValue( valueResolver,
                                        object );
    }

    public Method getNativeReadMethod() {
        return this.reader.getNativeReadMethod();
    }

    public String getNativeReadMethodName() {
        return this.reader.getNativeReadMethod().getName();
    }

    public int getHashCode(ValueResolver valueResolver,
                           final Object object) {
        return this.reader.getHashCode( valueResolver,
                                        object );
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return "this".equals( this.fieldName );
    }

    public int getHashCode(Object object) {
        return reader.getHashCode( object );
    }

    public Object getValue(Object object) {
        return reader.getValue( object );
    }

    public AccessorKey getAccessorKey() {
        return new AccessorKey( className, fieldName, AccessorKey.AccessorType.FieldAccessor );
    }
}
