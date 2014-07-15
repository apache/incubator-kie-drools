/*
 * Copyright 2010 JBoss Inc
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.core.common.EventFactHandle;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.facttemplates.FactTemplate;

public class ValueType
    implements
    Externalizable {

    private static final long      serialVersionUID  = 510l;

    public static final ValueType  NULL_TYPE         = new ValueType( "null",
                                                                      null,
                                                                      SimpleValueType.NULL);
    // primitive types
    public static final ValueType  PCHAR_TYPE        = new ValueType( "char",
                                                                      Character.TYPE,
                                                                      SimpleValueType.CHAR );
    public static final ValueType  PBYTE_TYPE        = new ValueType( "byte",
                                                                      Byte.TYPE,
                                                                      SimpleValueType.INTEGER );
    public static final ValueType  PSHORT_TYPE       = new ValueType( "short",
                                                                      Short.TYPE,
                                                                      SimpleValueType.INTEGER );
    public static final ValueType  PINTEGER_TYPE     = new ValueType( "int",
                                                                      Integer.TYPE,
                                                                      SimpleValueType.INTEGER );
    public static final ValueType  PLONG_TYPE        = new ValueType( "long",
                                                                      Long.TYPE,
                                                                      SimpleValueType.INTEGER );
    public static final ValueType  PFLOAT_TYPE       = new ValueType( "float",
                                                                      Float.TYPE,
                                                                      SimpleValueType.DECIMAL );
    public static final ValueType  PDOUBLE_TYPE      = new ValueType( "double",
                                                                      Double.TYPE,
                                                                      SimpleValueType.DECIMAL );
    public static final ValueType  PBOOLEAN_TYPE     = new ValueType( "boolean",
                                                                      Boolean.TYPE,
                                                                      SimpleValueType.BOOLEAN );
    
    // wrapper types
    public static final ValueType  CHAR_TYPE         = new ValueType( "Character",
                                                                      Character.class,
                                                                      SimpleValueType.CHAR );
    public static final ValueType  BYTE_TYPE         = new ValueType( "Byte",
                                                                      Byte.class,
                                                                      SimpleValueType.INTEGER);
    public static final ValueType  SHORT_TYPE        = new ValueType( "Short",
                                                                      Short.class,
                                                                      SimpleValueType.INTEGER );
    public static final ValueType  INTEGER_TYPE      = new ValueType( "Integer",
                                                                      Integer.class,
                                                                      SimpleValueType.INTEGER );
    public static final ValueType  LONG_TYPE         = new ValueType( "Long",
                                                                      Long.class,
                                                                      SimpleValueType.INTEGER );
    public static final ValueType  FLOAT_TYPE        = new ValueType( "Float",
                                                                      Float.class,
                                                                      SimpleValueType.DECIMAL );
    public static final ValueType  DOUBLE_TYPE       = new ValueType( "Double",
                                                                      Double.class,
                                                                      SimpleValueType.DECIMAL );
    public static final ValueType  BOOLEAN_TYPE      = new ValueType( "Boolean",
                                                                      Boolean.class,
                                                                      SimpleValueType.BOOLEAN );
   
    public static final ValueType  NUMBER_TYPE         = new ValueType( "Number",
                                                                        Number.class,
                                                                        SimpleValueType.DATE );
   
    public static final ValueType  BIG_DECIMAL_TYPE  = new ValueType( "BigDecimal",
                                                                      BigDecimal.class,
                                                                      SimpleValueType.NUMBER );
    public static final ValueType  BIG_INTEGER_TYPE  = new ValueType( "BigInteger",
                                                                      BigInteger.class,
                                                                      SimpleValueType.NUMBER );
    
    
    // other types    
    public static final ValueType  DATE_TYPE         = new ValueType( "Date",
                                                                      Date.class,
                                                                      SimpleValueType.DATE );
    public static final ValueType  ARRAY_TYPE        = new ValueType( "Array",
                                                                      Object[].class,
                                                                      SimpleValueType.LIST );
    public static final ValueType  STRING_TYPE       = new ValueType( "String",
                                                                      String.class,
                                                                      SimpleValueType.STRING );
    public static final ValueType  OBJECT_TYPE       = new ValueType( "Object",
                                                                      Object.class,
                                                                      SimpleValueType.OBJECT );
    public static final ValueType  FACTTEMPLATE_TYPE = new ValueType( "FactTemplate",
                                                                      FactTemplate.class,
                                                                      SimpleValueType.UNKNOWN );
    public static final ValueType  EVENT_TYPE        = new ValueType( "Event",
                                                                      EventFactHandle.class,
                                                                      SimpleValueType.OBJECT );
    public static final ValueType  QUERY_TYPE        = new ValueType( "Query",
                                                                      DroolsQuery.class,
                                                                      SimpleValueType.OBJECT );

    public static final ValueType  TRAIT_TYPE        = new ValueType( "Trait",
                                                                      Thing.class,
                                                                      SimpleValueType.OBJECT );

    public static final ValueType  CLASS_TYPE        = new ValueType( "Class",
                                                                      Class.class,
                                                                      SimpleValueType.OBJECT );

    private String           name;
    private Class<?>         classType;
    private int              simpleType;

    public ValueType() {
        this(null, null, 0);
    }

    private ValueType(final String name,
                      final Class<?> classType,
                      final int simpleType) {
        this.name = name;
        this.classType = classType;
        this.simpleType = simpleType;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name        = (String)in.readObject();
        classType   = (Class<?>)in.readObject();
        simpleType  = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(classType);
        out.writeInt(simpleType);
    }
    
    private Object readResolve() throws java.io.ObjectStreamException {
        return determineValueType( this.classType );
    }

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#getName()
     */
    public String getName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#getClassType()
     */
    public Class<?> getClassType() {
        return this.classType;
    }

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#getSimpleType()
     */
    public int getSimpleType() {
        return this.simpleType;
    }

    public static ValueType determineValueType(final Class<?> clazz) {
        if ( clazz == null ) {
            return ValueType.NULL_TYPE;
        }
        
        // primitives
        if ( clazz == FactTemplate.class ) {
            return ValueType.FACTTEMPLATE_TYPE;
        } else if ( clazz == DroolsQuery.class ) {
            return ValueType.QUERY_TYPE;
        } else if ( clazz == Character.TYPE ) {
            return ValueType.PCHAR_TYPE;
        } else if ( clazz == Byte.TYPE ) {
            return ValueType.PBYTE_TYPE;
        } else if ( clazz == Short.TYPE ) {
            return ValueType.PSHORT_TYPE;
        } else if ( clazz == Integer.TYPE ) {
            return ValueType.PINTEGER_TYPE;
        } else if ( clazz == Long.TYPE ) {
            return ValueType.PLONG_TYPE;
        } else if ( clazz == Float.TYPE ) {
            return ValueType.PFLOAT_TYPE;
        } else if ( clazz == Double.TYPE ) {
            return ValueType.PDOUBLE_TYPE;
        } else if ( clazz == Boolean.TYPE ) {
            return ValueType.PBOOLEAN_TYPE;
        } 
        
        // Number Wrappers
        if ( clazz == Character.class ) {
            return ValueType.CHAR_TYPE;
        } else if ( clazz == Byte.class ) {
            return ValueType.BYTE_TYPE;
        } else if ( clazz == Short.class ) {
            return ValueType.SHORT_TYPE;
        } else if ( clazz == Integer.class ) {
            return ValueType.INTEGER_TYPE;
        } else if ( clazz == Long.class ) {
            return ValueType.LONG_TYPE;
        } else if ( clazz == Float.class ) {
            return ValueType.FLOAT_TYPE;
        } else if ( clazz == Double.class ) {
            return ValueType.DOUBLE_TYPE;
        } else if ( clazz == Boolean.class ) {
            return ValueType.BOOLEAN_TYPE;
        }  else if ( clazz == BigDecimal.class ) {
            return ValueType.BIG_DECIMAL_TYPE;
        } else if ( clazz == BigInteger.class ) {
            return ValueType.BIG_INTEGER_TYPE;
        } else if ( Number.class.isAssignableFrom( clazz ) ) {
            return ValueType.NUMBER_TYPE;
        }
        
        
        // Other Object types
        if ( Date.class.isAssignableFrom( clazz ) ) {
            return ValueType.DATE_TYPE;
        } else if ( clazz.isArray() ) {
            return ValueType.ARRAY_TYPE;
        } else if ( clazz == String.class ) {
            return ValueType.STRING_TYPE;
        } else if ( clazz == EventFactHandle.class ) {
            return ValueType.EVENT_TYPE;
        } else if ( clazz == Class.class ) {
            return ValueType.CLASS_TYPE;
        } 
        else if ( Thing.class.isAssignableFrom( clazz ) || clazz.isAnnotationPresent( Trait.class ) ) {
            return ValueType.TRAIT_TYPE;
        } else {
            return ValueType.OBJECT_TYPE;
        }
    }

    public String toString() {
        return "ValueType = '" + this.name + "'";
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        } else if (object instanceof ValueType) {
            ValueType   that    = (ValueType)object;
            return classType == that.classType &&
                   simpleType == that.simpleType &&
                   (name == that.name || name != null && name.equals(that.name));
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#isBoolean()
     */
    public boolean isBoolean() {
        return ((this.classType == Boolean.class) || (this.classType == Boolean.TYPE));
    }
    
    public boolean isDate() {
        return this.classType == Date.class;
    }    

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#isNumber()
     */
    public boolean isNumber() {
        return ( this.simpleType == SimpleValueType.INTEGER ||
                 this.simpleType == SimpleValueType.DECIMAL ||
                 this.simpleType == SimpleValueType.CHAR || 
                 this.simpleType == SimpleValueType.NUMBER ) ;
    }

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#isIntegerNumber()
     */
    public boolean isIntegerNumber() {
        return this.simpleType == SimpleValueType.INTEGER;
    }

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#isFloatNumber()
     */
    public boolean isFloatNumber() {
        return this.simpleType == SimpleValueType.DECIMAL;
    }

    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#isChar()
     */
    public boolean isChar() {
        return this.simpleType == SimpleValueType.CHAR;
    }
    
    /* (non-Javadoc)
     * @see org.kie.base.ValueTypeInterface#isEvent()
     */
    public boolean isEvent() {
        return this.classType == EventFactHandle.class;
    }

}
