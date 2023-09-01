package org.drools.model.view;

import org.drools.model.Condition;
import org.drools.model.Variable;
import org.drools.model.functions.FunctionN;
import org.drools.model.functions.accumulate.AccumulateFunction;

public class GroupByExprViewItem<T, K> extends AccumulateExprViewItem<T> {

    private final Variable[] vars;
    private final Variable<K> varKey;
    private final FunctionN groupingFunction;

    public GroupByExprViewItem( ViewItem<T> expr, Variable[] vars, Variable<K> varKey, FunctionN groupingFunction, AccumulateFunction[] accumulateFunctions ) {
        super( expr, accumulateFunctions );
        this.vars = vars;
        this.varKey = varKey;
        this.groupingFunction = groupingFunction;
    }

    @Override
    public Condition.Type getType() {
        return Condition.Type.GROUP_BY;
    }

    public Variable[] getVars() {
        return vars;
    }

    public Variable<K> getVarKey() {
        return varKey;
    }

    public FunctionN getGroupingFunction() {
        return groupingFunction;
    }
}
