package org.drools.verifier.components;

public class PatternVariable extends RuleComponent implements Variable {

    private String name;

    public PatternVariable(VerifierRule rule) {
        super(rule);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Variable name: " + name;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.PATTERN_LEVEL_VARIABLE;
    }
}
