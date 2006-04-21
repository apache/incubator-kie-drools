package org.drools;

public class Alarm {
    private String message;
    
    public Alarm(String message) {
        this.message = message;
    }
    
    public String toString() {
        return this.message;
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    /**
     * @inheritDoc
     */
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Alarm other = (Alarm) obj;
        if ( message == null ) {
            if ( other.message != null ) return false;
        } else if ( !message.equals( other.message ) ) return false;
        return true;
    }

}
