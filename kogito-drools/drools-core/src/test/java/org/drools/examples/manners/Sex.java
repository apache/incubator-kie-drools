package org.drools.examples.manners;

public class Sex {
    public static final Sex m = new Sex( 0 );
    public static final Sex f = new Sex( 1 );       

    public static final String stringM = "m";
    public static final String stringF = "f";    
    
    private final int sex;
    
    private Sex(int sex) {
        this.sex = sex;
    }
    
    public String getValue() {
        switch ( this.sex ) {
            case 0 :
                return stringM;
            case 1 :
                return stringF;
            default :
                return "";
        }
    }
    
    public String toString() {
        return getValue();
    }
    
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        
        if ( ! ( object instanceof Sex ) )  {
            return false;
        }
        
        Sex other = (Sex) object;
        
        return ( this.sex == other.sex ); 
    }
    
    public int hashcode() {
        return this.sex;
    }
    
    
}
