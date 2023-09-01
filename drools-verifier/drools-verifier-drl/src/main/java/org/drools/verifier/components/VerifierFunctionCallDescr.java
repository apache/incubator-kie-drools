package org.drools.verifier.components;

public class VerifierFunctionCallDescr extends RuleComponent {

    private String name;
    private String arguments;

    public VerifierFunctionCallDescr(VerifierRule rule) {
        super( rule );
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.FUNCTION_CALL;
    }
}
