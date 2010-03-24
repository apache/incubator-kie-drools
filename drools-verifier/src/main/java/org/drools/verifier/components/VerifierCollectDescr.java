package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierCollectDescr extends PatternComponentSource {

    private String classMethodName;

    public String getClassMethodName() {
        return classMethodName;
    }

    public VerifierCollectDescr(Pattern pattern) {
        super( pattern );
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.COLLECT;
    }
}
