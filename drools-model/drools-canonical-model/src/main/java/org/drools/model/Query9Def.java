package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query9Def<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6, Argument<T7> var7, Argument<T8> var8, Argument<T9> var9) {
        return call(true, var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6, Argument<T7> var7, Argument<T8> var8, Argument<T9> var9);

    Variable<T1> getArg1();

    Variable<T2> getArg2();

    Variable<T3> getArg3();

    Variable<T4> getArg4();

    Variable<T5> getArg5();

    Variable<T6> getArg6();

    Variable<T7> getArg7();

    Variable<T8> getArg8();

    Variable<T9> getArg9();
}
