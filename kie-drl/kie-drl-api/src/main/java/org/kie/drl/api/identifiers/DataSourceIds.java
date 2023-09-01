package org.kie.drl.api.identifiers;

import org.kie.drl.api.identifiers.data.DataSourceId;

public class DataSourceIds {
    private final RuleUnitInstanceId ruleUnitInstanceId;

    public DataSourceIds(RuleUnitInstanceId ruleUnitInstanceId) {
        this.ruleUnitInstanceId = ruleUnitInstanceId;
    }

    public DataSourceId get(String dataSourceId) {
        return new DataSourceId(ruleUnitInstanceId, dataSourceId);
    }
}
