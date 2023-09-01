package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class InstanceQueryId extends LocalUriId implements LocalId {
    public static final String PREFIX = "queries";

    private final RuleUnitInstanceId ruleUnitInstanceId;
    private final String queryId;

    public InstanceQueryId(RuleUnitInstanceId ruleUnitInstanceId, String queryId) {
        super(ruleUnitInstanceId.asLocalUri().append(PREFIX).append(queryId));
        this.ruleUnitInstanceId = ruleUnitInstanceId;
        this.queryId = queryId;
    }

    public RuleUnitInstanceId ruleUnitInstanceId() {
        return ruleUnitInstanceId;
    }

    public String queryId() {
        return queryId;
    }
}
