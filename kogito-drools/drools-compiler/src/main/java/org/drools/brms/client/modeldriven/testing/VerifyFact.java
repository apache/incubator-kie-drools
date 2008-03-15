package org.drools.brms.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.List;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.IOException;

/**
 * This is for making assertions over a specific facts value/state AFTER execution.
 * @author Michael Neale
 *
 */
public class VerifyFact implements Expectation {

    /**
     * @gwt.typeArgs <org.drools.brms.client.modeldriven.testing.VerifyField>
     */
    public List fieldValues = new ArrayList();
    public String name;
    public String description;


    public VerifyFact() {}
    public VerifyFact(String factName, List fieldValues) {
        this.name = factName;
        this.fieldValues = fieldValues;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fieldValues = (List)in.readObject();
        name = (String)in.readObject();
        description = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(fieldValues);
        out.writeObject(name);
        out.writeObject(description);
    }

    public boolean wasSuccessful() {
        for (int i = 0; i < fieldValues.size(); i++) {
            VerifyField vf = (VerifyField) fieldValues.get(i);
            if (! vf.successResult.booleanValue()) {
                return false;
            }
        }
        return true;
    }

}
