package org.drools.guvnor.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.List;

/**
 * This is for making assertions over a specific facts value/state AFTER execution.
 * @author Michael Neale
 *
 */
public class VerifyFact implements Expectation {

    /**
     * @gwt.typeArgs <org.drools.guvnor.client.modeldriven.testing.VerifyField >
     */
    public List fieldValues = new ArrayList();
    public String name;
    public String description;

    /**
     * This is true if it isn't a named fact, but it will just search working memory to verify.
     */
	public boolean anonymous = false;


    public VerifyFact() {}
    public VerifyFact(String factName, List fieldValues, boolean anonymous) {
        this.name = factName;
        this.fieldValues = fieldValues;
        this.anonymous = anonymous;
    }

    public VerifyFact(String factName, List fieldValues) {
    	this(factName, fieldValues, false);
    }




    public boolean wasSuccessful() {
        for (int i = 0; i < fieldValues.size(); i++) {
            VerifyField vf = (VerifyField) fieldValues.get(i);
            if (!vf.successResult.booleanValue()) {
                return false;
            }
        }
        return true;
    }

}
