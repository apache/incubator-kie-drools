package org.drools.verifier.components;

public class NamedConsequence
        extends TextConsequence {

    private final String name;

    public NamedConsequence(VerifierRule rule, String name, String consequence) {
        super(rule);
        this.name = name;
        setText(consequence);
    }

    public String getName() {
        return name;
    }

}
