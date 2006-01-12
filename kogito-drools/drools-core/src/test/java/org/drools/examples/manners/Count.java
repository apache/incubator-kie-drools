package org.drools.examples.manners;

public class Count {
    private int value;

    public Count(int value) {
        super();
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    public String toString() {
        return "[Count value=" + this.value + "]";
    }
    
    public boolean equals(Object object) {
        if ( object == this ) {
            return true;
        }
        
        if ((object == null )||!(object instanceof Count)) {
            return false;
        }
        
        return this.value == ( (Count) object ).value;
    }
    
    public int hashCode() {
        return this.value;
    }
    
}
