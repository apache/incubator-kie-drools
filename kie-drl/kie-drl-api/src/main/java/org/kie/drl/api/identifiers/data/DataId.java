package org.kie.drl.api.identifiers.data;

import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class DataId extends LocalUriId implements LocalId {

    public static final String PREFIX = "data";
    private final DataSourceId parent;
    private final String dataId;

    public DataId(DataSourceId parent, String dataId) {
        super(parent.asLocalUri().append(PREFIX).append(dataId));
        this.parent = parent;
        this.dataId = dataId;
    }

    public DataSourceId dataSourceId() {
        return parent;
    }

    public String dataId() {
        return dataId;
    }
}
