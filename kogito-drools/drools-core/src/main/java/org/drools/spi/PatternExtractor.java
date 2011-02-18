/**
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

package org.drools.spi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MathUtils;
import org.drools.facttemplates.Fact;

public class PatternExtractor
    implements
    InternalReadAccessor,
    AcceptsClassObjectType,
    Externalizable {

    /**
     *
     */
    private static final long serialVersionUID = 510l;
    private ObjectType        objectType;

    public PatternExtractor() {
        this( null );
    }

    public PatternExtractor(final ObjectType objectType) {
        this.objectType = objectType;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        objectType = (ObjectType) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( objectType );
    }
    
    public void setClassObjectType(ClassObjectType objectType) {
        this.objectType = objectType;
    }    

    public Object getValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return object;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class<?> getExtractToClass() {
        // @todo : this is a bit nasty, but does the trick
        if ( this.objectType instanceof ClassObjectType ) {
            return ((ClassObjectType) this.objectType).getClassType();
        } else {
            return Fact.class;
        }
    }

    public String getExtractToClassName() {
        Class<?> clazz = null;
        // @todo : this is a bit nasty, but does the trick
        if ( this.objectType instanceof ClassObjectType ) {
            clazz = ((ClassObjectType) this.objectType).getClassType();
        } else {
            clazz = Fact.class;
        }
        return ClassUtils.canonicalName( clazz );
    }

    public ValueType getValueType() {
        return this.objectType.getValueType();
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory,
                                   final Object object) {
        if ( this.objectType.getValueType().isBoolean() ) {
            return ((Boolean) object).booleanValue();
        }
        throw new RuntimeDroolsException( "Conversion to boolean not supported for type: " + object.getClass() );
    }

    public byte getByteValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).byteValue();
        }
        throw new RuntimeDroolsException( "Conversion to byte not supported for type: " + object.getClass() );
    }

    public char getCharValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        if ( this.objectType.getValueType().isChar() ) {
            return ((Character) object).charValue();
        }
        throw new RuntimeDroolsException( "Conversion to char not supported for type: " + object.getClass() );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory,
                                 final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).doubleValue();
        }
        throw new RuntimeDroolsException( "Conversion to double not supported for type: " + object.getClass() );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).floatValue();
        }
        throw new RuntimeDroolsException( "Conversion to float not supported for type: " + object.getClass() );
    }

    public int getIntValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).intValue();
        }
        throw new RuntimeDroolsException( "Conversion to int not supported for type: " + object.getClass() );
    }

    public long getLongValue(InternalWorkingMemory workingMemory,
                             final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).longValue();
        } else if( object instanceof EventFactHandle ) {
            // special case for handling event timestamps
            return ((EventFactHandle)object).getStartTimestamp();
        }
        throw new RuntimeDroolsException( "Conversion to long not supported for type: " + object.getClass() );
    }

    public short getShortValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        if ( this.objectType.getValueType().isNumber() ) {
            return ((Number) object).shortValue();
        }
        throw new RuntimeDroolsException( "Conversion to short not supported for type: " + object.getClass() );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory,
                               final Object object) {
        return getValue( workingMemory,
                         object ) == null;
    }

    public int getHashCode(InternalWorkingMemory workingMemory,
                           final Object object) {
        return getValue( workingMemory,
                         object ).hashCode();
    }

    public int hashCode() {
        return this.objectType.hashCode();
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( !(obj instanceof PatternExtractor) ) {
            return false;
        }
        final PatternExtractor other = (PatternExtractor) obj;
        return this.objectType.equals( other.objectType );
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return false;
    }

    public boolean getBooleanValue(Object object) {
        return getBooleanValue( null,
                                object );
    }

    public byte getByteValue(Object object) {
        return getByteValue( null,
                             object );
    }

    public char getCharValue(Object object) {
        return getCharValue( null,
                             object );
    }

    public double getDoubleValue(Object object) {
        return getDoubleValue( null,
                               object );
    }

    public float getFloatValue(Object object) {
        return getFloatValue( null,
                              object );
    }

    public int getHashCode(Object object) {
        return getHashCode( null,
                            object );
    }

    public int getIndex() {
        return -1;
    }

    public int getIntValue(Object object) {
        return getIntValue( null,
                            object );
    }

    public long getLongValue(Object object) {
        return getLongValue( null,
                             object );
    }

    public short getShortValue(Object object) {
        return getShortValue( null,
                              object );
    }

    public Object getValue(Object object) {
        return getValue( null,
                         object );
    }
    
    public BigDecimal getBigDecimalValue(Object object) {
        return getBigDecimalValue( null,
                                   object );
    }

    public BigInteger getBigIntegerValue(Object object) {
        return getBigIntegerValue( null,
                                   object );
    }

    public BigDecimal getBigDecimalValue(InternalWorkingMemory workingMemory,
                                         Object object) {
        return MathUtils.getBigDecimal( getValue( workingMemory,
                                                  object ) );
    }

    public BigInteger getBigIntegerValue(InternalWorkingMemory workingMemory,
                                         Object object) {
        return MathUtils.getBigInteger( getValue( workingMemory,
                                                  object ) );
    }

    public boolean isNullValue(Object object) {
        return isNullValue( null,
                            object );
    }
}
