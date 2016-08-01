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

import org.drools.core.base.field.BooleanFieldImpl;
import org.drools.core.base.field.ClassFieldImpl;
import org.drools.core.base.field.DoubleFieldImpl;
import org.drools.core.base.field.LongFieldImpl;
import org.drools.core.base.field.ObjectFieldImpl;
import org.drools.core.spi.FieldValue;
import org.drools.core.util.DateUtils;
import org.drools.core.util.MathUtils;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;

public class FieldFactory implements FieldDataFactory, Serializable {
    private static final FieldFactory INSTANCE = new FieldFactory();


    public static FieldFactory getInstance() {
        return FieldFactory.INSTANCE;
    }

    protected FieldFactory() {
    }

    public FieldValue getFieldValue(Object value,
                                    ValueType valueType) {
        FieldValue field = null;
        if ( value == null ) {
            valueType = ValueType.NULL_TYPE;
        }

        if ( valueType == ValueType.NULL_TYPE ) {
            field = new ObjectFieldImpl( null );
        } else if ( valueType == ValueType.PCHAR_TYPE || valueType == ValueType.CHAR_TYPE) {
            if( value instanceof String && ((String)value).length() == 1 ) {
                field = new LongFieldImpl( ((String) value).charAt(0) );
            } else {
                field = new LongFieldImpl( ((Character) value).charValue() );
            }
        } else if ( valueType == ValueType.PBYTE_TYPE ||   valueType == ValueType.BYTE_TYPE ) {
            if( value instanceof String ) {
                field = new LongFieldImpl( Byte.parseByte( (String) value) );
            } else {
                field = new LongFieldImpl( ((Number) value).byteValue() );
            }
        } else if ( valueType == ValueType.PSHORT_TYPE ||  valueType == ValueType.SHORT_TYPE ) {
            if( value instanceof String ) {
                try {
                    field = new LongFieldImpl( NumberFormat.getInstance().parse( (String) value ).shortValue() );
                } catch ( ParseException e ) {
                    throw new NumberFormatException( "Error parsing number '"+value+"'" );
                }
            } else {
                field = new LongFieldImpl( ((Number) value).shortValue() );
            }
        } else if ( valueType == ValueType.PINTEGER_TYPE || valueType == ValueType.INTEGER_TYPE ) {
            if( value instanceof String ) {
                try {
                    field = new LongFieldImpl( NumberFormat.getInstance().parse( (String) value ).intValue() );
                } catch ( ParseException e ) {
                    throw new NumberFormatException( "Error parsing number '"+value+"'" );
                }
            } else {
                field = new LongFieldImpl( ((Number) value).intValue() );
            }
        } else if ( valueType == ValueType.PLONG_TYPE || valueType == ValueType.LONG_TYPE ) {
            if( value instanceof String ) {
                try {
                    field = new LongFieldImpl( NumberFormat.getInstance().parse( (String) value ).longValue() );
                } catch ( ParseException e ) {
                    throw new NumberFormatException( "Error parsing number '"+value+"'" );
                }
            } else {
                field = new LongFieldImpl( ((Number) value).longValue() );
            }
        } else if ( valueType == ValueType.PFLOAT_TYPE || valueType == ValueType.FLOAT_TYPE ) {
            if( value instanceof String ) {
                try {
                    field = new DoubleFieldImpl( NumberFormat.getInstance().parse( (String) value ).floatValue() );
                } catch ( ParseException e ) {
                    throw new NumberFormatException( "Error parsing number '"+value+"'" );
                }
            } else {
                field = new DoubleFieldImpl( ((Number) value).floatValue() );
            }
        } else if ( valueType == ValueType.PDOUBLE_TYPE || valueType == ValueType.DOUBLE_TYPE ) {
            if( value instanceof String ) {
                try {
                    field = new DoubleFieldImpl( NumberFormat.getInstance().parse( (String) value ).doubleValue() );
                } catch ( ParseException e ) {
                    throw new NumberFormatException( "Error parsing number '"+value+"'" );
                }
            } else {
                field = new DoubleFieldImpl( ((Number) value).doubleValue() );
            }
        } else if ( valueType == ValueType.PBOOLEAN_TYPE || valueType == ValueType.BOOLEAN_TYPE ) {
            if( value instanceof String ) {
                field = new BooleanFieldImpl( Boolean.valueOf( (String) value).booleanValue() );
            } else {
                field = new BooleanFieldImpl( ((Boolean) value).booleanValue() );
            }
        }  else if ( valueType == ValueType.STRING_TYPE ) {
            field = new ObjectFieldImpl( value );
        } else if ( valueType == ValueType.DATE_TYPE ) {
            //MN: I think its fine like this, seems to work !
            if( value instanceof String ) {
                Date date = DateUtils.parseDate( (String) value );
                field = new ObjectFieldImpl( date );
            } else {
                field = new ObjectFieldImpl( value );
            }
        } else if ( valueType == ValueType.ARRAY_TYPE ) {
            //MN: I think its fine like this.
            field = new ObjectFieldImpl( value );
        } else if ( valueType == ValueType.OBJECT_TYPE ) {
            field = new ObjectFieldImpl( value );
        } else if ( valueType == ValueType.TRAIT_TYPE ) {
            field = new ObjectFieldImpl( value );
        } else if ( valueType == ValueType.BIG_DECIMAL_TYPE ) {
            field = new ObjectFieldImpl( MathUtils.getBigDecimal( value ) );
        } else if ( valueType == ValueType.BIG_INTEGER_TYPE ) {
            field = new ObjectFieldImpl( MathUtils.getBigInteger( value ) );
        } else if ( valueType == ValueType.CLASS_TYPE ) {
            field = new ClassFieldImpl( (Class) value );
        }

        return field;
    }



    public FieldValue getFieldValue(final Object value) {
        return new ObjectFieldImpl( value );
    }

    public FieldValue getFieldValue(final byte value) {
        return new LongFieldImpl( value );
    }

    public FieldValue getFieldValue(final short value) {
        return new LongFieldImpl( value );
    }

    public FieldValue getFieldValue(final char value) {
        return new LongFieldImpl( value );
    }

    public FieldValue getFieldValue(final int value) {
        return new LongFieldImpl( value );
    }

    public FieldValue getFieldValue(final long value) {
        return new LongFieldImpl( value );
    }

    public FieldValue getFieldValue(final boolean value) {
        return new BooleanFieldImpl( value );
    }

    public FieldValue getFieldValue(final float value) {
        return new DoubleFieldImpl( value );
    }

    public FieldValue getFieldValue(final double value) {
        return new DoubleFieldImpl( value );
    }

    public FieldValue getFieldValue(final Class value) {
        return value == null ? new ObjectFieldImpl( null ) :  new ClassFieldImpl( value );
    }

    private String stripNumericType(String value) {
        // incase a user adds a f or l, strip it as its not needed
        if ( Character.getType( value.charAt( value.length() - 1 ) ) != Character.DECIMAL_DIGIT_NUMBER ) {
            value = value.substring( 0,
                                     value.length() - 1 );
        }

        return value;
    }


}
