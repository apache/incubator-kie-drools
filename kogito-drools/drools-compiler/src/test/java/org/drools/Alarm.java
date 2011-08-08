package org.drools;

import java.io.Serializable;

public class Alarm implements Serializable {
    private String message;

    private int number;
    
    public Alarm() {

    }

    public Alarm(final String message) {
        this.message = message;
    }

    public String toString() {
        return "[Alarm message=" + this.message + "]";
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.message == null) ? 0 : this.message.hashCode());
        return result;
    }

    /**
     * @inheritDoc
     */
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Alarm other = (Alarm) obj;
        if ( this.message == null ) {
            if ( other.message != null ) {
                return false;
            }
        } else if ( !this.message.equals( other.message ) ) {
            return false;
        }
        return true;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    
    public void incrementNumber() {
        this.number++;;
    }

}
