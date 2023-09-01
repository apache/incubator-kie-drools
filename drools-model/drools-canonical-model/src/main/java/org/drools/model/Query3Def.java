package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query3Def<T1, T2, T3> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1, Argument<T2> var2, Argument<T3> var3) {
        return call(true, var1, var2, var3);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3);

    Variable<T1> getArg1();

    Variable<T2> getArg2();

    Variable<T3> getArg3();
}
