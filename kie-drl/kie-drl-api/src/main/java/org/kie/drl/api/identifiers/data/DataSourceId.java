package org.kie.drl.api.identifiers.data;

import org.kie.drl.api.identifiers.RuleUnitInstanceId;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class DataSourceId extends LocalUriId implements LocalId {
    public static final String PREFIX = "data-sources";

    private final RuleUnitInstanceId ruleUnitInstanceId;
    private final String dataSourceId;

    public DataSourceId(RuleUnitInstanceId ruleUnitInstanceId, String dataSourceId) {
        super(ruleUnitInstanceId.asLocalUri().append(PREFIX).append(dataSourceId));
        this.ruleUnitInstanceId = ruleUnitInstanceId;
        this.dataSourceId = dataSourceId;
    }

    public RuleUnitInstanceId ruleUnitInstanceId() {
        return this.ruleUnitInstanceId;
    }

    public String dataSourceId() {
        return dataSourceId;
    }

    public DataIds data() {
        return new DataIds(this);
    }

}
