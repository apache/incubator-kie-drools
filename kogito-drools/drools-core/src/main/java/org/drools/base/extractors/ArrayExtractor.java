package org.drools.base.extractors;

import java.lang.reflect.Method;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Extractor;
import org.drools.util.ClassUtils;

public class ArrayExtractor implements Extractor {
    private Extractor arrayExtractor;
    private int index;
    private Class type;

    public ArrayExtractor() {

    }

    public ArrayExtractor(Extractor arrayExtractor, int index, Class type) {
        this.arrayExtractor = arrayExtractor;
        this.index = index;
        this.type = type;
    }

    public Class getExtractToClass() {
        return type;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        arrayExtractor  = (Extractor)in.readObject();
        index           = in.readInt();
        type            = (Class)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(arrayExtractor);
        out.writeInt(index);
        out.writeObject(type);
    }

    public String getExtractToClassName() {
        return ClassUtils.canonicalName( type );
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Boolean)array[ this.index ]).booleanValue();
    }
    public byte getByteValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Number)array[ this.index ]).byteValue();
    }
    public char getCharValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Character)array[ this.index ]).charValue();
    }
    public double getDoubleValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Number)array[ this.index ]).doubleValue();
    }

    public float getFloatValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Number)array[ this.index ]).floatValue();
    }

    public int getIntValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Number)array[ this.index ]).intValue();
    }
    public long getLongValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Number) array[ this.index ]).longValue();
    }
    public Method getNativeReadMethod() {
        throw new UnsupportedOperationException("cannot call a method on an array extractor" );
    }
    public short getShortValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return ( (Number)array[ this.index ]).shortValue();
    }
    public Object getValue(InternalWorkingMemory workingMemory, Object object) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return array[ this.index ];
    }
    public ValueType getValueType() {
        return ValueType.OBJECT_TYPE;
    }
    public boolean isNullValue(InternalWorkingMemory workingMemory, Object object ) {
        Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
        return array[ this.index ] == null;
    }

  public int getHashCode(InternalWorkingMemory workingMemory, Object object) {
      Object[] array = ( Object[] ) this.arrayExtractor.getValue( workingMemory, object );
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

    public boolean isGlobal() {
        return false;
    }
}
