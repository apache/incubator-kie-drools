package org.kie.drl.api.identifiers.data;

public class DataIds {
    private final DataSourceId dataSourceId;

    public DataIds(DataSourceId dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public DataId get(String dataId) {
        return new DataId(dataSourceId, dataId);
    }
}
