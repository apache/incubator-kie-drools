package org.drools.examples.manners;

public class Hobby {
    public static final Hobby h1 = new Hobby( 1 );
    public static final Hobby h2 = new Hobby( 2 );
    public static final Hobby h3 = new Hobby( 3 );       

    public static final String stringH1 = "h1";
    public static final String stringH2 = "h2";
    public static final String stringH3 = "h3";    
    
    private final int hobby;
    
    private Hobby(int hobby) {
        this.hobby = hobby;
    }
    
    public String getValue() {
        switch ( this.hobby ) {
            case 1 :
                return stringH1;            
            case 2 :
                return stringH2;
            case 3 :
                return stringH3;
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
        
        if ( ! ( object instanceof Hobby ) )  {
            return false;
        }
        
        Hobby other = (Hobby) object;
        
        return ( this.hobby == other.hobby); 
    }
    
    public int hashcode() {
        return this.hobby;
    }
    
    
}
