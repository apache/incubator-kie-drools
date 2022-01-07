/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.ClassUtils;

/**
 * This provides access to fields, and what their numerical index/object type is.
 * This is basically a wrapper class around dynamically generated subclasses of
 * BaseClassFieldExtractor,
 *  which allows serialization by regenerating the accessor classes
 * when needed.
 */
public class ClassFieldReader
    implements
    Externalizable,
    InternalReadAccessor {
    private static final long              serialVersionUID = 510l;
    private String                         className;
    private String                         fieldName;
    private transient InternalReadAccessor reader;

    /**
     * Utility method to take a string and convert it to normal Java variable
     * name capitalization.  This normally means converting the first
     * character from upper case to lower case, but in the (unusual) special
     * case when there is more than one character and both the first and
     * second characters are upper case, we leave it alone.
     * <p>
     * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
     * as "URL".
     *
     * Taken from
     *
     * @param  name The string to be decapitalized.
     * @return  The decapitalized version of the string.
     */
    public static String decapitalizeFieldName(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
                Character.isUpperCase(name.charAt(0))){
            return name;
        }
        char chars[] = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    public ClassFieldReader() {

    }

    public ClassFieldReader(final String className,
                            final String fieldName) {
        this.className = className;
        this.fieldName = ClassFieldReader.decapitalizeFieldName(fieldName);
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

    public void setReadAccessor(InternalReadAccessor reader) {
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
    
    public Object getValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        return this.reader.getValue( reteEvaluator,
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

    public boolean getBooleanValue(ReteEvaluator reteEvaluator,
                                   final Object object) {
        return this.reader.getBooleanValue( reteEvaluator,
                                            object );
    }

    public byte getByteValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        return this.reader.getByteValue( reteEvaluator,
                                         object );
    }

    public char getCharValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        return this.reader.getCharValue( reteEvaluator,
                                         object );
    }

    public double getDoubleValue(ReteEvaluator reteEvaluator,
                                 final Object object) {
        return this.reader.getDoubleValue( reteEvaluator,
                                           object );
    }

    public float getFloatValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        return this.reader.getFloatValue( reteEvaluator,
                                          object );
    }

    public int getIntValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        return this.reader.getIntValue( reteEvaluator,
                                        object );
    }

    public long getLongValue(ReteEvaluator reteEvaluator,
                             final Object object) {
        return this.reader.getLongValue( reteEvaluator,
                                         object );
    }

    public short getShortValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        return this.reader.getShortValue( reteEvaluator,
                                          object );
    }

    public boolean isNullValue(ReteEvaluator reteEvaluator,
                               final Object object) {
        return this.reader.isNullValue( reteEvaluator,
                                        object );
    }

    public Method getNativeReadMethod() {
        return this.reader.getNativeReadMethod();
    }

    public String getNativeReadMethodName() {
        return this.reader.getNativeReadMethod().getName();
    }

    public int getHashCode(ReteEvaluator reteEvaluator,
                           final Object object) {
        return this.reader.getHashCode( reteEvaluator,
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

    public boolean isNullValue(Object object) {
        return reader.isNullValue( object );
    }

    public AccessorKey getAccessorKey() {
        return new AccessorKey( className, fieldName, AccessorKey.AccessorType.FieldAccessor );
    }
}
