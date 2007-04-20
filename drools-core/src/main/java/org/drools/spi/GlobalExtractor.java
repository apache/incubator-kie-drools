/*
 * Copyright 2006 JBoss Inc
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

package org.drools.spi;

import java.lang.reflect.Method;
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;

/**
 * This is a dummy extractor used during rule compilation and build. It is not
 * supposed to be used to extract real global values during runtime, so
 * all getValueXXX() methods will raise unsupported operation exceptions.
 * 
 * @author etirelli
 */
public class GlobalExtractor
    implements
    Extractor {

    private static final long serialVersionUID = -756967384190918798L;
    private final String            key;
    private final ObjectType        objectType;

    public GlobalExtractor(final String key,
                           final Map map) {
        this.key = key;
        this.objectType = new ClassObjectType( (Class) map.get( this.key ));
    }

    public Object getValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class getExtractToClass() {
        return this.objectType.getValueType().getClassType();
    }

    public ValueType getValueType() {
        return this.objectType.getValueType();
    }

    public boolean getBooleanValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public byte getByteValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public char getCharValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public double getDoubleValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public float getFloatValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public int getIntValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public long getLongValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public short getShortValue(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public Method getNativeReadMethod() {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public int getHashCode(final Object object) {
        throw new UnsupportedOperationException("Operation not suported for globals");
    }

    public int hashCode() {
        return this.objectType.hashCode();
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( !(obj instanceof GlobalExtractor) ) {
            return false;
        }
        final GlobalExtractor other = (GlobalExtractor) obj;
        return ( key == null ? other.key == null : key.equals( other.key ) ) &&
               ( this.objectType == null ? other.objectType == null : this.objectType.equals( other.objectType ));
    }
}
