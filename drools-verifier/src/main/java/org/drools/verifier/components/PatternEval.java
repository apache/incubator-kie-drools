package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

/**
 *
 * @author Toni Rikkola
 */
public class PatternEval extends PatternComponent
    implements
    Eval,
    Cause {

    private String content;
    private String classMethodName;

    public PatternEval(Pattern pattern) {
        super( pattern );
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
        return "Eval content: " + content;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.PREDICATE;
    }
}
