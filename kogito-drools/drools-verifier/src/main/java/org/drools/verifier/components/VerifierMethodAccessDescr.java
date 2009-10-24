package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class VerifierMethodAccessDescr extends RuleComponent {

    private String methodName;
    private String arguments;

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.METHOD_ACCESSOR;
    }
}
