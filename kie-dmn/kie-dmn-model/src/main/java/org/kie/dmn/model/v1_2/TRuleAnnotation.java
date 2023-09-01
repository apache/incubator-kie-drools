package org.kie.dmn.model.v1_2;

import org.kie.dmn.model.api.RuleAnnotation;

public class TRuleAnnotation extends KieDMNModelInstrumentedBase implements RuleAnnotation {

    protected String text;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String value) {
        this.text = value;
    }

}
