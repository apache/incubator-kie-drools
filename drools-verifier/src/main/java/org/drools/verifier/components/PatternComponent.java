package org.drools.verifier.components;

public abstract class PatternComponent extends RuleComponent {

    private String patternName;
    private String patternGuid;

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public String getPatternGuid() {
        return patternGuid;
    }

    public void setPatternGuid(String patternGuid) {
        this.patternGuid = patternGuid;
    }

}
