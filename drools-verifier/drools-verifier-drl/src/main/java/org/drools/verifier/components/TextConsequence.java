package org.drools.verifier.components;

import org.drools.drl.ast.descr.RuleDescr;

public class TextConsequence extends RuleComponent<RuleDescr>
    implements
    Consequence {

    private String text;

    public TextConsequence(VerifierRule rule) {
        super( rule );
    }

    @Override
    public String getPath() {
        return getRulePath() + "/consequence";
    }

    public ConsequenceType getConsequenceType() {
        return ConsequenceType.TEXT;
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

    public String toString() {
        return "TextConsequence: {\n" + text + "\n";
    }

}
