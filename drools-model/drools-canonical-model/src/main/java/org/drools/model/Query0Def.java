package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query0Def extends QueryDef {

    default QueryCallViewItem call() {
        return call(true);
    }

    QueryCallViewItem call(boolean open);
}
