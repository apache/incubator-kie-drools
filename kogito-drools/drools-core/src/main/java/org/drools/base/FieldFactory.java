package org.drools.base;

import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;

public class FieldFactory {
    private static final FieldFactory INSTANCE = new FieldFactory();

    public static FieldFactory getInstance() {
        return FieldFactory.INSTANCE;
    }

    private FieldFactory() {

    }
    
    public static FieldValue getFieldValue(String value,
                                           int valueType) {
        FieldValue field = null;
        switch (valueType) {
            case Evaluator.NULL_TYPE:
                field = new FieldImpl(null);
                break;
            case Evaluator.CHAR_TYPE:
                field = new FieldImpl(new Character( value.charAt( 0 ) ) );
                break;
            case Evaluator.BYTE_TYPE:
                field = new FieldImpl( new Byte(value) );
                break;
            case Evaluator.SHORT_TYPE:
                field = new FieldImpl( new Short(value) );
                break;
            case Evaluator.INTEGER_TYPE:
                field = new FieldImpl( new Integer(value) );
                break;
            case Evaluator.LONG_TYPE:
                field = new FieldImpl( new Long(value) );
                break;
            case Evaluator.FLOAT_TYPE:
                field = new FieldImpl( new Float(value) );
                break;
            case Evaluator.DOUBLE_TYPE:
                field = new FieldImpl( new Double(value) );
                break;
            case Evaluator.BOOLEAN_TYPE:
                field = new FieldImpl( new Boolean(value) );
                break;
            case Evaluator.STRING_TYPE:
                field = new FieldImpl( value );
                break;
            case Evaluator.DATE_TYPE:
                //@todo
                break;
            case Evaluator.ARRAY_TYPE:
                //@todo
                break;
            case Evaluator.OBJECT_TYPE:
                field = new FieldImpl( value );
                break;              
        }
        return field;
    }
    
    public static class FieldImpl implements FieldValue {
        private Object value;
        
        public FieldImpl(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return this.value;
        }
        
        public boolean equals(Object other) {
            if(this == other) {
                return true;
            }
            if(!(other instanceof FieldImpl)) {
                return false;
            }
            FieldImpl field = (FieldImpl) other;
            
            return (((this.value == null ) && (field.value == null)) ||
                    ((this.value != null ) && (this.value.equals(field.value))));
        }
        
        public int hashCode() {
            return this.value.hashCode();
        }
    }
    
    
}
