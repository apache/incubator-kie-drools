package org.drools.brms.client.modeldriven.testing;

import org.drools.brms.client.modeldriven.brl.PortableObject;

public class VerifyField implements PortableObject {

    public String fieldName;
    public String expected;

    public String actualResult;
    public Boolean successResult;

    /**
     * This is a natural language explanation of the outcome for reporting purposes.
     */
    public String explanation;

    /**
     * Operator is generally "==" or "!="  - an MVEL operator.
     */
    public String operator = "==";

    public VerifyField() {}

    public VerifyField(String fieldName, String expected, String operator) {
        this.fieldName = fieldName;
        this.expected = expected;
        this.operator = operator;
    }

}
