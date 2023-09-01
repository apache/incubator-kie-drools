package org.kie.drl.api.identifiers;

public class InstanceQueryIds {
    private final RuleUnitInstanceId ruleUnitInstanceId;

    public InstanceQueryIds(RuleUnitInstanceId ruleUnitInstanceId) {
        this.ruleUnitInstanceId = ruleUnitInstanceId;
    }

    public RuleUnitInstanceId ruleUnitInstanceId() {
        return ruleUnitInstanceId;
    }

    public InstanceQueryId get(String queryId) {
        return new InstanceQueryId(ruleUnitInstanceId, queryId);
    }
}
