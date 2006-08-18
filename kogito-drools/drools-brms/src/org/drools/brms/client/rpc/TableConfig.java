package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TableConfig
    implements
    IsSerializable {
    
    public String[] headers;
    public int rowsPerPage;

}
