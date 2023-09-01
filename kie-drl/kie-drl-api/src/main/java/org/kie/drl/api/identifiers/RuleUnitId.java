package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class RuleUnitId extends LocalUriId implements LocalId {
    public static final String PREFIX = "rule-units";

    private final String ruleUnitId;

    RuleUnitId(String ruleUnitId) {
        super(LocalUri.Root.append(PREFIX).append(ruleUnitId));
        this.ruleUnitId = ruleUnitId;
    }

    public String ruleUnitId() {
        return ruleUnitId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    public RuleUnitInstanceIds instances() {
        return new RuleUnitInstanceIds(this);
    }

    public QueryIds queries() {
        return new QueryIds(this);
    }
}
