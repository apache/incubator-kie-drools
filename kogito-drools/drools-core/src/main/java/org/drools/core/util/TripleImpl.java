package org.drools.core.util;

public class TripleImpl implements Entry, Triple {
    private Entry next;
    
    private Object instance;
    private String property;
    private Object value;
    
    public TripleImpl(Object instance,
                  String property,
                  Object value) {
        super();
        this.instance = instance;
        this.property = property;
        this.value = value;
    }

    public void setNext(Entry next) {
        this.next = next;
    }

    public Entry getNext() {
        return this.next;
    }

    /* (non-Javadoc)
     * @see org.drools.core.util.Triple#getInstance()
     */
    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    /* (non-Javadoc)
     * @see org.drools.core.util.Triple#getProperty()
     */
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    /* (non-Javadoc)
     * @see org.drools.core.util.Triple#getValue()
     */
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instance == null) ? 0 : instance.hashCode());
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TripleImpl other = (TripleImpl) obj;
        if ( instance == null ) {
            if ( other.instance != null ) return false;
        } else if ( !instance.equals( other.instance ) ) return false;
        if ( property == null ) {
            if ( other.property != null ) return false;
        } else if ( !property.equals( other.property ) ) return false;
        if ( value == null ) {
            if ( other.value != null ) return false;
        } else if ( !value.equals( other.value ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "Triple [instance=" + instance + ", property=" + property + ", value=" + value + "]";
    }
 
}
