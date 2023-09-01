package org.drools.core.base;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="array-elements")
@XmlAccessorType(XmlAccessType.NONE)
public class ArrayElements {

    @XmlElement
    private Object[] elements;

    private static final Object[] EMPTY_ELEMENTS = new Object[0];

    public ArrayElements() {
        this(null);
    }

    public ArrayElements(final Object[] elements) {
        setElements( elements );
    }

    public Object[] getElements() {
        return this.elements;
    }

    public void setElements(Object[] elements) {
        this.elements = elements != null ? elements : EMPTY_ELEMENTS;
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
