package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.CauseType;

/**
 *
 * @author Toni Rikkola
 */
public class VerifierPredicateDescr extends RuleComponent
    implements
    Cause {

    private String content;
    private String classMethodName;

    public CauseType getCauseType() {
        return CauseType.PREDICATE;
    }

    public String getClassMethodName() {
        return classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Predicate id: " + getGuid() + " content: " + content;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.PREDICATE;
    }
}
