package org.drools.brms.client.modeldriven.brl;

import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

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

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        attributeName   = (String)in.readObject();
        value   = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(attributeName);
        out.writeObject(value);
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
