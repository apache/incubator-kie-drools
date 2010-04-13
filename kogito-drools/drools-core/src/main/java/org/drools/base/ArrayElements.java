package org.drools.base;

import java.util.Arrays;


public class ArrayElements {
    private final Object[] elements;
    
    private static final Object[] EMPTY_ELEMENTS = new Object[0];
    
    public ArrayElements() {
        this(null);
    }
    
    public ArrayElements(final Object[] elements) {
        if ( elements != null ) {
            this.elements = elements;
        } else {
            this.elements = EMPTY_ELEMENTS;
        }
    }    
    
    public Object[] getElements() {
        return this.elements;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( elements );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ArrayElements other = (ArrayElements) obj;
        if ( !Arrays.equals( elements,
                             other.elements ) ) return false;
        return true;
    }
    
    
}
