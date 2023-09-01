package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.DataHandle;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class SessionData implements RuleUnitData {
    private final DataStore<Object> dataSource = DataSource.createStore();

    public DataSource<Object> getDataSource() {
        return dataSource;
    }

    public DataHandle add(Object obj) {
        return dataSource.add(obj);
    }

    public void remove(DataHandle dh) {
        dataSource.remove(dh);
    }

    public void update(DataHandle dh, Object obj) {
        dataSource.update(dh, obj);
    }
}
