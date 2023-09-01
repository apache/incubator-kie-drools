package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query2Def<T1, T2> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1, Argument<T2> var2) {
        return call(true, var1, var2);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2);

    Variable<T1> getArg1();

    Variable<T2> getArg2();
}
