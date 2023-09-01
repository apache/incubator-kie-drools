package org.drools.model.impl;

import org.drools.model.Argument;
import org.drools.model.Query1Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query1DefImpl<T1> extends QueryDefImpl implements ModelComponent, Query1Def<T1> {

    private final Variable<T1> arg1;

    public Query1DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1);
    }

    public Query1DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1);
    }

    public Query1DefImpl(ViewBuilder viewBuilder, String name, Class<T1> type1, String arg1name) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, arg1name);
    }

    public Query1DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<T1> type1, String arg1name) {
        super(viewBuilder, pkg, name);
        this.arg1 = declarationOf(type1, arg1name);
    }

    @Override()
    public QueryCallViewItem call(boolean open, Argument<T1> var1) {
        return new QueryCallViewItemImpl(this, open, var1);
    }

    @Override()
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { arg1 };
    }

    @Override()
    public Variable<T1> getArg1() {
        return arg1;
    }

    @Override
    public boolean isEqualTo(ModelComponent other) {
        if (this == other)
            return true;
        if (!(other instanceof Query1DefImpl))
            return false;
        Query1DefImpl that = (Query1DefImpl) other;
        return true && ModelComponent.areEqualInModel(arg1, that.arg1);
    }
}
