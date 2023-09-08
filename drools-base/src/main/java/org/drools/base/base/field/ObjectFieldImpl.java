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
package org.drools.base.base.field;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

import org.drools.base.rule.accessor.FieldValue;
import org.drools.util.MathUtils;

public class ObjectFieldImpl
    implements
        FieldValue,
    Externalizable {

    private static final long serialVersionUID = 510l;

    private Object            value;

    private String            fieldName;

    private transient boolean isCollection;
    private transient boolean isNumber;
    private transient boolean isBoolean;
    private transient boolean isCharacter;
    private transient boolean isString;

    public ObjectFieldImpl() {
        this( null );
    }

    public ObjectFieldImpl(final Object value) {
        this.value = value;
        setBooleans();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fieldName = (String) in.readObject();
        if ( fieldName == null ) {
            value = in.readObject();
        }
        setBooleans();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( fieldName );
        if ( fieldName == null ) {
            out.writeObject( value );
        }
    }

    private void setBooleans() {
        this.isCollection = value instanceof Collection;
        this.isNumber = value instanceof Number;
        this.isBoolean = value instanceof Boolean;
        this.isCharacter = value instanceof Character;
        this.isString = value instanceof String;
    }

    public Object getValue() {
        return this.value;
    }

    public String toString() {
        return this.value == null ? "null" : this.value.toString();
    }

    public boolean getBooleanValue() {
        if ( isBoolean ) {
            return (Boolean) this.value;
        } else if ( isString ) {
            return Boolean.parseBoolean( (String) this.value );
        }
        throw new RuntimeException( "Conversion to boolean not supported for type: " + this.value.getClass() );
    }

    public byte getByteValue() {
        if ( isNumber ) {
            return ((Number) this.value).byteValue();
        } else if ( isString ) {
            return Byte.parseByte( (String) this.value );
        }
        throw new RuntimeException( "Conversion to byte not supported for type: " + this.value.getClass() );
    }

    public char getCharValue() {
        if ( isCharacter ) {
            return (Character) this.value;
        } else if ( isString && ((String) this.value).length() == 1 ) {
            return ((String) this.value).charAt( 0 );
        }
        throw new RuntimeException( "Conversion to char not supported for type: " + this.value.getClass() );
    }

    public double getDoubleValue() {
        if ( isNumber ) {
            return ((Number) this.value).doubleValue();
        } else if ( isString ) {
            return Double.parseDouble( (String) this.value );
        }
        throw new RuntimeException( "Conversion to double not supported for type: " + this.value.getClass() );
    }

    public float getFloatValue() {
        if ( isNumber ) {
            return ((Number) this.value).floatValue();
        } else if ( isString ) {
            return Float.parseFloat( (String) this.value );
        }
        throw new RuntimeException( "Conversion to float not supported for type: " + this.value.getClass() );
    }

    public int getIntValue() {
        if ( isNumber ) {
            return ((Number) this.value).intValue();
        } else if ( isString ) {
            return Integer.parseInt( (String) this.value );
        }
        throw new RuntimeException( "Conversion to int not supported for type: " + this.value.getClass() );
    }

    public long getLongValue() {
        if ( isNumber ) {
            return ((Number) this.value).longValue();
        } else if ( isString ) {
            return Long.parseLong( (String) this.value );
        }
        throw new RuntimeException( "Conversion to long not supported for type: " + this.value.getClass() );
    }

    public short getShortValue() {
        if ( isNumber ) {
            return ((Number) this.value).shortValue();
        } else if ( isString ) {
            return Short.parseShort( (String) this.value );
        }
        throw new RuntimeException( "Conversion to short not supported for type: " + this.value.getClass() );
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if (!(object instanceof ObjectFieldImpl)) {
            return false;
        }
        final ObjectFieldImpl other = (ObjectFieldImpl) object;

        return (((this.value == null) && (other.value == null)) || ((this.value != null) && (this.value.equals( other.value ))));
    }

    public int hashCode() {
        if ( this.value != null ) {
            return this.value.hashCode();
        } else {
            return 0;
        }
    }

    public boolean isNull() {
        return value == null;
    }

    public boolean isBooleanField() {
        return false;
    }

    public boolean isFloatNumberField() {
        return false;
    }

    public boolean isIntegerNumberField() {
        return false;
    }

    public boolean isObjectField() {
        return true;
    }

    public boolean isCollectionField() {
        return this.isCollection;
    }

    public boolean isStringField() {
        return this.isString;
    }

    public BigDecimal getBigDecimalValue() {
        return MathUtils.getBigDecimal( this.value );
    }

    public BigInteger getBigIntegerValue() {
        return MathUtils.getBigInteger( this.value );
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}
