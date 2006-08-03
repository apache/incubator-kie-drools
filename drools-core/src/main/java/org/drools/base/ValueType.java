package org.drools.base;

import java.io.Serializable;
import java.util.Date;

import org.drools.RuntimeDroolsException;
import org.drools.base.evaluators.ArrayFactory;
import org.drools.base.evaluators.BooleanFactory;
import org.drools.base.evaluators.ByteFactory;
import org.drools.base.evaluators.CharacterFactory;
import org.drools.base.evaluators.DateFactory;
import org.drools.base.evaluators.DoubleFactory;
import org.drools.base.evaluators.FloatFactory;
import org.drools.base.evaluators.IntegerFactory;
import org.drools.base.evaluators.LongFactory;
import org.drools.base.evaluators.ObjectFactory;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.ShortFactory;
import org.drools.base.evaluators.StringFactory;
import org.drools.spi.Evaluator;
import org.drools.base.evaluators.EvaluatorFactory;
import org.drools.facttemplates.FactTemplate;

public class ValueType implements Serializable {
    
    private static final long        serialVersionUID = 320;
    
    public static final ValueType NULL_TYPE = new  ValueType( "null", null, null );
    public static final ValueType CHAR_TYPE = new  ValueType( "Character", Character.class, CharacterFactory.getInstance() );   
    public static final ValueType BYTE_TYPE = new  ValueType( "Byte", Byte.class, ByteFactory.getInstance() );
    public static final ValueType SHORT_TYPE = new  ValueType( "Short", Short.class, ShortFactory.getInstance() );
    public static final ValueType INTEGER_TYPE = new  ValueType( "Integer", Integer.class, IntegerFactory.getInstance() );
    public static final ValueType LONG_TYPE = new  ValueType( "Long", Long.class, LongFactory.getInstance() );
    public static final ValueType FLOAT_TYPE = new  ValueType( "Float", Float.class, FloatFactory.getInstance() );
    public static final ValueType DOUBLE_TYPE = new  ValueType( "Double", Double.class, DoubleFactory.getInstance() );
    public static final ValueType BOOLEAN_TYPE = new  ValueType( "Boolean", Boolean.class, BooleanFactory.getInstance() );    
    public static final ValueType DATE_TYPE = new  ValueType( "Date", Date.class, DateFactory.getInstance() );    
    public static final ValueType ARRAY_TYPE = new  ValueType( "Array", Object[].class, ArrayFactory.getInstance() );
    public static final ValueType STRING_TYPE = new  ValueType( "String", String.class, StringFactory.getInstance() );    
    public static final ValueType OBJECT_TYPE = new  ValueType( "Object", Object.class, ObjectFactory.getInstance() );
    public static final ValueType FACTTEMPLATE_TYPE = new  ValueType( "FactTemplate", FactTemplate.class, ObjectFactory.getInstance() );    
    
    private final String name;
    private final Class classType;
    private final EvaluatorFactory evaluatorFactory;
    
    private ValueType(String name, Class classType, EvaluatorFactory evaluatorFactory) {
        this.name  = name;
        this.classType  = classType;
        this.evaluatorFactory = evaluatorFactory;
    }
    
    private Object readResolve () throws java.io.ObjectStreamException
    {
        return determineValueType( this.classType );
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class getClassType() {
        return this.classType;
    }
    
    public Evaluator getEvaluator(Operator operator)  {
        return this.evaluatorFactory.getEvaluator( operator );
    }
    
    public static ValueType determineValueType(Class clazz) {
        if ( clazz == null ) {
            return  ValueType .NULL_TYPE;
        } if ( clazz == FactTemplate.class ) {
          return ValueType.FACTTEMPLATE_TYPE;  
        } else if ( clazz == Character.class ) {
            return  ValueType.CHAR_TYPE;
        } else if ( clazz == Byte.class ) {
            return  ValueType .BYTE_TYPE;
        } else if ( clazz == Short.class ) {
            return  ValueType .SHORT_TYPE;
        } else if ( clazz == Integer.class ) {
            return  ValueType .INTEGER_TYPE;
        } else if ( clazz == Long.class ) {
            return  ValueType .LONG_TYPE;
        } else if ( clazz == Float.class ) {
            return  ValueType .FLOAT_TYPE;
        } else if ( clazz == Double.class ) {
            return  ValueType .DOUBLE_TYPE;
        } else if ( clazz == Boolean.class ) {
            return  ValueType .BOOLEAN_TYPE;
        } else if ( clazz == java.sql.Date.class ) {
            return  ValueType .DATE_TYPE;
        } else if ( clazz == java.util.Date.class ) {
            return  ValueType .DATE_TYPE;
        } else if ( clazz.isAssignableFrom( Object[].class ) ) {
            return  ValueType .ARRAY_TYPE;
        } else if ( clazz == String.class ) {
            return  ValueType .STRING_TYPE;
        } else if ( clazz instanceof Object ) {
            return  ValueType .OBJECT_TYPE;
        }
        throw new RuntimeDroolsException( "unable to determine ValueType for Class [" + clazz + "]" );        
    }
    
    public String toString() {
        return "ValueType = '" + this.name + "'";
    }    
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public boolean equals(Object object) {
        if ( object == this ) {
            return  true;
        }
        
        return false;        
    }
    
}
