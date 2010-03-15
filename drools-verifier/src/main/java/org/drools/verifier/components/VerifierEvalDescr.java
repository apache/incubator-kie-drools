package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

/**
 *
 * @author Toni Rikkola
 */
public class VerifierEvalDescr extends RuleComponent
    implements
    Cause {

    private String content;
    private String classMethodName;

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
        return "Eval, content: " + content;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.EVAL;
    }
}
