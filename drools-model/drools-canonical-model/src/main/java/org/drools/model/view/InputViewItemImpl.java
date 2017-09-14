package org.drools.model.view;

import org.drools.model.DataSourceDefinition;
import org.drools.model.Variable;

public class InputViewItemImpl<T> implements InputViewItem<T> {
    private final Variable<T> var;
    private final DataSourceDefinition dataSourceDefinition;

    public InputViewItemImpl( Variable<T> var, DataSourceDefinition dataSourceDefinition ) {
        this.var = var;
        this.dataSourceDefinition = dataSourceDefinition;
    }

    @Override
    public Variable getFirstVariable() {
        return var;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { var };
    }

    public DataSourceDefinition getDataSourceDefinition() {
        return dataSourceDefinition;
    }
}
