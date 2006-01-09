package org.drools.examples.manners;

public class Hobby {
    public static final Hobby H1 = new Hobby( 1 );
    public static final Hobby H2 = new Hobby( 2 );
    public static final Hobby H3 = new Hobby( 3 );       

    public static final String stringH1 = "h1";
    public static final String stringH2 = "h2";
    public static final String stringH3 = "h3";    
    
    private final int hobby;
    
    private Hobby(int hobby) {
        this.hobby = hobby;
    }
    
    public String getHobby() {
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
    
    public static Hobby resolve(String hobby) {
        if (stringH1.equals( hobby ) ) {
            return Hobby.H1;
        } else if (stringH2.equals( hobby ) ) {
            return Hobby.H2;
        } else if (stringH3.equals( hobby ) ) {
            return Hobby.H3;
        } else {
            throw new RuntimeException("Hobby '" + hobby + "' does not exist for Hobby Enum" );
        }
    }
    
    public String toString() {
        return getHobby();
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
