package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query10Def<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> extends QueryDef {

    default QueryCallViewItem call(Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6, Argument<T7> var7, Argument<T8> var8, Argument<T9> var9, Argument<T10> var10) {
        return call(true, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
    }

    QueryCallViewItem call(boolean open, Argument<T1> var1, Argument<T2> var2, Argument<T3> var3, Argument<T4> var4, Argument<T5> var5, Argument<T6> var6, Argument<T7> var7, Argument<T8> var8, Argument<T9> var9, Argument<T10> var10);

    public Variable<T1> getArg1();

    public Variable<T2> getArg2();

    public Variable<T3> getArg3();

    public Variable<T4> getArg4();

    public Variable<T5> getArg5();

    public Variable<T6> getArg6();

    public Variable<T7> getArg7();

    public Variable<T8> getArg8();

    public Variable<T9> getArg9();

    public Variable<T10> getArg10();
}
