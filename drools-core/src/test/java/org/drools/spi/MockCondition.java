package org.drools.spi;

import org.drools.rule.Declaration;

public class MockCondition
    implements
    Condition {

    private Declaration[] declarations;
    private boolean       isAllowed;

    private boolean       testException;

    public MockCondition(Declaration[] declarations,
                         boolean isAllowed) {
        this.declarations = declarations;
        this.isAllowed = isAllowed;
    }

    public Declaration[] getRequiredTupleMembers() {
        return this.declarations;
    }

    public boolean isAllowed(Tuple tuple) throws TestException {
        if ( this.testException ) {
            throw new TestException();
        }
        return this.isAllowed;
    }

    public void setTestException(boolean testException) {
        this.testException = testException;
    }

}
