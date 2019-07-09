package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query5Def<T1, T2, T3, T4, T5> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5) {
        return call(true, var1, var2, var3, var4, var5);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5);

    public Variable<T1> getArg1();

    public Variable<T2> getArg2();

    public Variable<T3> getArg3();

    public Variable<T4> getArg4();

    public Variable<T5> getArg5();
}
