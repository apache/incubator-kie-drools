package org.drools.verifier.components;

import org.drools.verifier.report.components.CauseType;

public class TextConsequence extends RuleComponent
    implements
    Consequence {

    private String text;

    public ConsequenceType getConsequenceType() {
        return ConsequenceType.TEXT;
    }

    public CauseType getCauseType() {
        return CauseType.CONSEQUENCE;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.CONSEQUENCE;
    }
}
