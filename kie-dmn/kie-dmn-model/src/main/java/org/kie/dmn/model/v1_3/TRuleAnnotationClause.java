package org.kie.dmn.model.v1_3;

import org.kie.dmn.model.api.RuleAnnotationClause;


public class TRuleAnnotationClause extends KieDMNModelInstrumentedBase implements RuleAnnotationClause {

    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

}
