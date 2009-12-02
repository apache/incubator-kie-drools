package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierCollectDescr extends Source {

    private String insidePatternGuid;
    private String classMethodName;

    public String getInsidePatternGuid() {
        return insidePatternGuid;
    }

    public void setInsidePatternGuid(String guid) {
        this.insidePatternGuid = guid;
    }

    public String getClassMethodName() {
        return classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.COLLECT;
    }
}
