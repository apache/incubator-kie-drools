package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class RuleUnitInstanceId extends LocalUriId implements LocalId {

    public static final String PREFIX = "instances";

    private final RuleUnitId ruleUnitId;
    private final String ruleUnitInstanceId;

    public RuleUnitInstanceId(RuleUnitId processId, String ruleUnitInstanceId) {
        super(processId.asLocalUri().append(PREFIX).append(ruleUnitInstanceId));
        LocalId localDecisionId = processId.toLocalId();
        if (!localDecisionId.asLocalUri().startsWith(RuleUnitId.PREFIX)) {
            throw new IllegalArgumentException("Not a valid process path"); // fixme use typed exception
        }

        this.ruleUnitId = processId;
        this.ruleUnitInstanceId = ruleUnitInstanceId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    public RuleUnitId ruleUnitId() {
        return ruleUnitId;
    }

    public InstanceQueryIds queries() {
        return new InstanceQueryIds(this);
    }

    public DataSourceIds dataSources() {
        return new DataSourceIds(this);
    }

    public String ruleUnitInstanceId() {
        return ruleUnitInstanceId;
    }

}