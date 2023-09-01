package org.drools.model.impl;

import org.drools.model.Query0Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;

import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query0DefImpl extends QueryDefImpl implements Query0Def, ModelComponent {

    public Query0DefImpl( ViewBuilder viewBuilder, String name ) {
        this(viewBuilder, DEFAULT_PACKAGE, name);
    }

    public Query0DefImpl( ViewBuilder viewBuilder, String pkg, String name ) {
        super( viewBuilder, pkg, name );
    }

    @Override
    public QueryCallViewItem call(boolean open) {
        return new QueryCallViewItemImpl( this, open );
    }

    @Override
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { };
    }

    @Override
    public boolean isEqualTo( ModelComponent other ) {
        return other instanceof Query0DefImpl;
    }
}
