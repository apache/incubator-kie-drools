package org.drools.guvnor.client.modeldriven.brl;


/**
 * This holds values for rule attributes (eg salience, agenda-group etc).
 * @author Michael Neale
 */
public class RuleAttribute
    implements
    PortableObject {

    private static final String NOLOOP   = "no-loop";
    private static final String SALIENCE = "salience";
    private static final String ENABLED  = "enabled";
    private static final String DURATION = "duration";
    private static final String LOCK_ON_ACTIVE = "lock-on-active";

    private static final String AUTO_FOCUS = "auto-focus";

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
        StringBuilder ret = new StringBuilder();
        ret.append( this.attributeName );
        if ( NOLOOP.equals( attributeName ) )
        {
            ret.append( " " );
            ret.append( this.value == null ? "true" : this.value );
        }
        else if (SALIENCE.equals( this.attributeName ) ||
         DURATION.equals( this.attributeName ))
        {
            ret.append( " " );
            ret.append( this.value );
        }
        else if (ENABLED.equals( this.attributeName ) ||
         AUTO_FOCUS.equals( this.attributeName ) ||
         LOCK_ON_ACTIVE.equals( this.attributeName ))
        {
            ret.append( " " );
            ret.append( this.value.equals("true") ? "true" : "false" );
        }
        else if ( this.value != null ) {
            ret.append( " \"" );
            ret.append( this.value );
            ret.append( "\"" );
        }
        return ret.toString();
    }

}
