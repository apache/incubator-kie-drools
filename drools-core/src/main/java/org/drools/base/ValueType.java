package org.drools.base;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.RuntimeDroolsException;
import org.drools.base.evaluators.ArrayFactory;
import org.drools.base.evaluators.BigDecimalFactory;
import org.drools.base.evaluators.BigIntegerFactory;
import org.drools.base.evaluators.BooleanFactory;
import org.drools.base.evaluators.ByteFactory;
import org.drools.base.evaluators.CharacterFactory;
import org.drools.base.evaluators.DateFactory;
import org.drools.base.evaluators.DoubleFactory;
import org.drools.base.evaluators.EvaluatorFactory;
import org.drools.base.evaluators.FloatFactory;
import org.drools.base.evaluators.IntegerFactory;
import org.drools.base.evaluators.LongFactory;
import org.drools.base.evaluators.ObjectFactory;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.ShortFactory;
import org.drools.base.evaluators.StringFactory;
import org.drools.facttemplates.FactTemplate;
import org.drools.spi.Evaluator;

public class ValueType
    implements
    Serializable {

    private static final long      serialVersionUID  = 320;

    public static final ValueType  NULL_TYPE         = new ValueType( "null",
                                                                      null,
                                                                      SimpleValueType.NULL,
                                                                      null );
    // wrapper types
    public static final ValueType  CHAR_TYPE         = new ValueType( "Character",
                                                                      Character.class,
                                                                      SimpleValueType.CHAR,
                                                                      CharacterFactory.getInstance() );
    public static final ValueType  BYTE_TYPE         = new ValueType( "Byte",
                                                                      Byte.class,
                                                                      SimpleValueType.INTEGER,
                                                                      ByteFactory.getInstance() );
    public static final ValueType  SHORT_TYPE        = new ValueType( "Short",
                                                                      Short.class,
                                                                      SimpleValueType.INTEGER,
                                                                      ShortFactory.getInstance() );
    public static final ValueType  INTEGER_TYPE      = new ValueType( "Integer",
                                                                      Integer.class,
                                                                      SimpleValueType.INTEGER,
                                                                      IntegerFactory.getInstance() );
    public static final ValueType  LONG_TYPE         = new ValueType( "Long",
                                                                      Long.class,
                                                                      SimpleValueType.INTEGER,
                                                                      LongFactory.getInstance() );
    public static final ValueType  FLOAT_TYPE        = new ValueType( "Float",
                                                                      Float.class,
                                                                      SimpleValueType.DECIMAL,
                                                                      FloatFactory.getInstance() );
    public static final ValueType  DOUBLE_TYPE       = new ValueType( "Double",
                                                                      Double.class,
                                                                      SimpleValueType.DECIMAL,
                                                                      DoubleFactory.getInstance() );
    public static final ValueType  BOOLEAN_TYPE      = new ValueType( "Boolean",
                                                                      Boolean.class,
                                                                      SimpleValueType.BOOLEAN,
                                                                      BooleanFactory.getInstance() );
    // primitive types
    public static final ValueType  PCHAR_TYPE        = new ValueType( "char",
                                                                      Character.TYPE,
                                                                      SimpleValueType.CHAR,
                                                                      CharacterFactory.getInstance() );
    public static final ValueType  PBYTE_TYPE        = new ValueType( "byte",
                                                                      Byte.TYPE,
                                                                      SimpleValueType.INTEGER,
                                                                      ByteFactory.getInstance() );
    public static final ValueType  PSHORT_TYPE       = new ValueType( "short",
                                                                      Short.TYPE,
                                                                      SimpleValueType.INTEGER,
                                                                      ShortFactory.getInstance() );
    public static final ValueType  PINTEGER_TYPE     = new ValueType( "int",
                                                                      Integer.TYPE,
                                                                      SimpleValueType.INTEGER,
                                                                      IntegerFactory.getInstance() );
    public static final ValueType  PLONG_TYPE        = new ValueType( "long",
                                                                      Long.TYPE,
                                                                      SimpleValueType.INTEGER,
                                                                      LongFactory.getInstance() );
    public static final ValueType  PFLOAT_TYPE       = new ValueType( "float",
                                                                      Float.TYPE,
                                                                      SimpleValueType.DECIMAL,
                                                                      FloatFactory.getInstance() );
    public static final ValueType  PDOUBLE_TYPE      = new ValueType( "double",
                                                                      Double.TYPE,
                                                                      SimpleValueType.DECIMAL,
                                                                      DoubleFactory.getInstance() );
    public static final ValueType  PBOOLEAN_TYPE     = new ValueType( "boolean",
                                                                      Boolean.TYPE,
                                                                      SimpleValueType.BOOLEAN,
                                                                      BooleanFactory.getInstance() );
    // other types
    public static final ValueType  DATE_TYPE         = new ValueType( "Date",
                                                                      Date.class,
                                                                      SimpleValueType.DATE,
                                                                      DateFactory.getInstance() );
    public static final ValueType  ARRAY_TYPE        = new ValueType( "Array",
                                                                      Object[].class,
                                                                      SimpleValueType.LIST,
                                                                      ArrayFactory.getInstance() );
    public static final ValueType  STRING_TYPE       = new ValueType( "String",
                                                                      String.class,
                                                                      SimpleValueType.STRING,
                                                                      StringFactory.getInstance() );
    public static final ValueType  OBJECT_TYPE       = new ValueType( "Object",
                                                                      Object.class,
                                                                      SimpleValueType.OBJECT,
                                                                      ObjectFactory.getInstance() );
    public static final ValueType  FACTTEMPLATE_TYPE = new ValueType( "FactTemplate",
                                                                      FactTemplate.class,
                                                                      SimpleValueType.UNKNOWN,
                                                                      ObjectFactory.getInstance() );
    public static final ValueType  BIG_DECIMAL_TYPE  = new ValueType( "BigDecimal",
                                                                      BigDecimal.class,
                                                                      SimpleValueType.OBJECT,
                                                                      BigDecimalFactory.getInstance() );
    public static final ValueType  BIG_INTEGER_TYPE  = new ValueType( "BigInteger",
                                                                      BigInteger.class,
                                                                      SimpleValueType.OBJECT,
                                                                      BigIntegerFactory.getInstance() );

    private final String           name;
    private final Class            classType;
    private final EvaluatorFactory evaluatorFactory;
    private final int              simpleType;

    private ValueType(final String name,
                      final Class classType,
                      final int simpleType,
                      final EvaluatorFactory evaluatorFactory) {
        this.name = name;
        this.classType = classType;
        this.simpleType = simpleType;
        this.evaluatorFactory = evaluatorFactory;
    }    

    private Object readResolve() throws java.io.ObjectStreamException {
        return determineValueType( this.classType );
    }

    public String getName() {
        return this.name;
    }        

    public Class getClassType() {
        return this.classType;
    }
    
    public int getSimpleType() {
        return this.simpleType;
    }

    public Evaluator getEvaluator(final Operator operator) {
        return this.evaluatorFactory.getEvaluator( operator );
    }

    public static ValueType determineValueType(final Class clazz) {
        if ( clazz == null ) {
            return ValueType.NULL_TYPE;
        }
        if ( clazz == FactTemplate.class ) {
            return ValueType.FACTTEMPLATE_TYPE;
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
        } else if ( clazz == Character.class ) {
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
        } else if ( clazz == java.sql.Date.class ) {
            return ValueType.DATE_TYPE;
        } else if ( clazz == java.util.Date.class ) {
            return ValueType.DATE_TYPE;
        } else if ( clazz.isAssignableFrom( Object[].class ) ) {
            return ValueType.ARRAY_TYPE;
        } else if ( clazz == BigDecimal.class ) {
            return ValueType.BIG_DECIMAL_TYPE;
        } else if ( clazz == BigInteger.class ) {
            return ValueType.BIG_INTEGER_TYPE;
        } else if ( clazz == String.class ) {
            return ValueType.STRING_TYPE;
        } else if ( clazz instanceof Object ) {
            return ValueType.OBJECT_TYPE;
        }
        throw new RuntimeDroolsException( "unable to determine ValueType for Class [" + clazz + "]" );
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
        }

        return false;
    }

    public boolean isBoolean() {
        return ((this.classType == Boolean.class) || (this.classType == Boolean.TYPE));
    }

    public boolean isNumber() {
        return ( this.simpleType == SimpleValueType.INTEGER || this.simpleType == SimpleValueType.DECIMAL || this.simpleType == SimpleValueType.CHAR );
//        return (this.classType == Integer.TYPE) || 
//               (this.classType == Long.TYPE) || 
//               (this.classType == Float.TYPE) || 
//               (this.classType == Double.TYPE) ||
//               (this.classType == Byte.TYPE) || 
//               (this.classType == Short.TYPE) || 
//               (this.classType == Character.TYPE) ||
//               (this.classType == Character.class) || 
//               (Number.class.isAssignableFrom( this.classType ));
    }

    public boolean isIntegerNumber() {
        return this.simpleType == SimpleValueType.INTEGER;
//        return (this.classType == Integer.TYPE) || 
//               (this.classType == Long.TYPE) || 
//               (this.classType == Integer.class) || 
//               (this.classType == Long.class) || 
//               (this.classType == Character.class) || 
//               (this.classType == Character.TYPE) ||
//               (this.classType == Byte.TYPE) ||
//               (this.classType == Short.TYPE) || 
//               (this.classType == Byte.class) || 
//               (this.classType == Short.class);
    }

    public boolean isFloatNumber() {
        return this.simpleType == SimpleValueType.DECIMAL;
//        return (this.classType == Float.TYPE) || 
//               (this.classType == Double.TYPE) || 
//               (this.classType == Float.class) || 
//               (this.classType == Double.class);
    }

    public boolean isChar() {
        return this.simpleType == SimpleValueType.CHAR;
//        return ((this.classType == Character.class) || (this.classType == Character.TYPE));
    }

}
