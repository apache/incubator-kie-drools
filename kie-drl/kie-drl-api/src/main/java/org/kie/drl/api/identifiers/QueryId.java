package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class QueryId extends LocalUriId implements LocalId {
    public static final String PREFIX = "queries";

    private final RuleUnitId ruleUnitId;
    private final String queryId;

    public QueryId(RuleUnitId ruleUnitId, String queryId) {
        super(ruleUnitId.asLocalUri().append(PREFIX).append(queryId));
        LocalId localId = ruleUnitId.toLocalId();
        if (!localId.asLocalUri().startsWith(RuleUnitId.PREFIX)) {
            throw new InvalidRuleUnitIdException(localId); // fixme use typed exception
        }

        this.ruleUnitId = ruleUnitId;
        this.queryId = queryId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    public RuleUnitId ruleUnitId() {
        return ruleUnitId;
    }

    public String queryId() {
        return queryId;
    }

}
