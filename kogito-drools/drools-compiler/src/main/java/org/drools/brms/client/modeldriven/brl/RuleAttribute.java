package org.drools.brms.client.modeldriven.brl;


/**
 * This holds values for rule attributes (eg salience, agenda-group etc).
 * @author Michael Neale
 */
public class RuleAttribute
    implements
    PortableObject {

    private static final String NOLOOP   = "no-loop";
    private static final String SALIENCE = "salience";

    public RuleAttribute(final String name,
                         final String value) {
        this.attributeName = name;
        this.value = value;
    }

    public String attributeName;
    public String value;

    public RuleAttribute() {
    }



    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append( this.attributeName );
        if ( NOLOOP.equals( attributeName ) ) {
            ret.append( " " );
            ret.append( this.value == null ? "true" : this.value );
        } else if ( SALIENCE.equals( this.attributeName ) ) {
            ret.append( " " );
            ret.append( this.value );
        } else if ( this.value != null ) {
            ret.append( " \"" );
            ret.append( this.value );
            ret.append( "\"" );
        }
        return ret.toString();
    }

}
