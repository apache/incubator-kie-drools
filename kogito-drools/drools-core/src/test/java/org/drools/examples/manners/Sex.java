package org.drools.examples.manners;

public class Sex {
    public static final Sex    M       = new Sex( 0 );
    public static final Sex    F       = new Sex( 1 );

    public static final String stringM = "m";
    public static final String stringF = "f";

    private final int          sex;

    private Sex(int sex) {
        this.sex = sex;
    }

    public String getSex() {
        switch ( this.sex ) {
            case 0 :
                return stringM;
            case 1 :
                return stringF;
            default :
                return "";
        }
    }

    public static Sex resolve(String sex) {
        if ( stringM.equals( sex ) ) {
            return Sex.M;
        } else if ( stringF.equals( sex ) ) {
            return Sex.F;
        } else {
            throw new RuntimeException( "Sex '" + sex + "' does not exist for Sex Enum" );
        }
    }

    public String toString() {
        return getSex();
    }

    public boolean equals(Object object) {
        if ( object == null ) {
            return false;
        }

        if ( !(object instanceof Sex) ) {
            return false;
        }

        Sex other = (Sex) object;

        return (this.sex == other.sex);
    }

    public int hashcode() {
        return this.sex;
    }

}
