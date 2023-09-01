package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query1Def<T1> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1) {
        return call(true, var1);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1);

    Variable<T1> getArg1();
}
