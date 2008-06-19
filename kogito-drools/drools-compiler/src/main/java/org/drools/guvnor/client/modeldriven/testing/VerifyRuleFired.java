package org.drools.guvnor.client.modeldriven.testing;


public class VerifyRuleFired implements Expectation {

    public String ruleName;
    public Integer expectedCount;

    /**
     * This is a natural language explanation of this verification.
     * For reporting purposes.
     */
    public String explanation;

    /**
     * If this is true, then we expect it to fire at least once.
     * False means it should not fire at all (this is an alternative
     * to specifying an expected count).
     */
    public Boolean expectedFire;

    public Boolean successResult;
    public Integer actualResult;

    public VerifyRuleFired() {}
    public VerifyRuleFired(String ruleName, Integer expectedCount, Boolean expectedFire) {
        this.ruleName = ruleName;
        this.expectedCount = expectedCount;
        this.expectedFire = expectedFire;
    }


    public boolean wasSuccessful() {
        return successResult.booleanValue();
    }

}
