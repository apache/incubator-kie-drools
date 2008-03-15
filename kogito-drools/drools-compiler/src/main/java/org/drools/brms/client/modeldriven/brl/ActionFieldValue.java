package org.drools.brms.client.modeldriven.brl;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/**
 * Holds field and value for "action" parts of the rule.
 *
 * @author Michael Neale
 */
public class ActionFieldValue
    implements
    PortableObject {

    public String field;
    public String value;

    /**
     * This is the datatype archectype (eg String, Numeric etc).
     */
    public String type;

    public ActionFieldValue(final String field,
                            final String value,
                            final String type) {
        this.field = field;
        this.value = value;
        this.type = type;
    }

    public ActionFieldValue() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        field   = (String)in.readObject();
        value   = (String)in.readObject();
        type    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(field);
        out.writeObject(value);
        out.writeObject(type);
    }

    /**
     * This will return true if the value is really a "formula" - in
     * the sense of like an excel spreadsheet.
     *
     *  If it IS a formula, then the value should never be turned into a
     *  string, always left as-is.
     *
     */
    public boolean isFormula() {
        if ( this.value == null ) {
            return false;
        }
        if ( this.value.trim().startsWith( "=" ) ) {
            return true;
        } else {
            return false;
        }
    }

}
