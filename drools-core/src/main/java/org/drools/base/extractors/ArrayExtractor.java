package org.drools.base.extractors;

import java.lang.reflect.Method;

import org.drools.base.ValueType;
import org.drools.spi.Extractor;

public class ArrayExtractor implements Extractor {
    private final Extractor arrayExtractor;    
    private final int index;
    private final Class type;
    
    public ArrayExtractor(Extractor arrayExtractor, int index, Class type) {
        this.arrayExtractor = arrayExtractor;
        this.index = index;
        this.type = type;
    }
    
    public Class getExtractToClass() {
        return type;        
    }
    
    public boolean getBooleanValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Boolean)array[ this.index ]).booleanValue();
    }
    public byte getByteValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Byte)array[ this.index ]).byteValue();
    }
    public char getCharValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Character)array[ this.index ]).charValue();
    }
    public double getDoubleValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Double)array[ this.index ]).doubleValue();
    }

    public float getFloatValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Float)array[ this.index ]).floatValue();
    }

    public int getIntValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Integer)array[ this.index ]).intValue();
    }
    public long getLongValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Long) array[ this.index ]).longValue();
    }
    public Method getNativeReadMethod() {
        throw new UnsupportedOperationException("cannot call a method on an array extractor" );
    }
    public short getShortValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return ( (Short)array[ this.index ]).shortValue();
    }
    public Object getValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return array[ this.index ];
    }
    public ValueType getValueType() {
        return ValueType.OBJECT_TYPE;
    }
    public boolean isNullValue(Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
        return array[ this.index ] == null;
    }
    
  public int getHashCode(Object object) {
      Object[] array = ( Object[] ) this.arrayExtractor.getValue( object );
      return array[ this.index ].hashCode();
  }    

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((arrayExtractor == null) ? 0 : arrayExtractor.hashCode());
        result = PRIME * result + index;
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final ArrayExtractor other = (ArrayExtractor) obj;
        if ( arrayExtractor == null ) {
            if ( other.arrayExtractor != null ) return false;
        } else if ( !arrayExtractor.equals( other.arrayExtractor ) ) return false;
        if ( index != other.index ) return false;
        return true;
    }
    

    
    
    
}
