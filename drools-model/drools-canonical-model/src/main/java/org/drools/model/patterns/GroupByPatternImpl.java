package org.drools.model.patterns;

import org.drools.model.Condition;
import org.drools.model.GroupByPattern;
import org.drools.model.Variable;
import org.drools.model.functions.FunctionN;
import org.drools.model.functions.accumulate.AccumulateFunction;

public class GroupByPatternImpl<T, K> extends AccumulatePatternImpl<T> implements GroupByPattern<T, K> {

    private final Variable[] vars;
    private final Variable<K> varKey;
    private final FunctionN groupingFunction;

    public GroupByPatternImpl( Condition condition, Variable[] vars, Variable<K> varKey, FunctionN groupingFunction, AccumulateFunction... accumulateFunctions ) {
        super( condition, varKey, accumulateFunctions );
        this.vars = vars;
        this.varKey = varKey;
        this.groupingFunction = groupingFunction;
    }

    @Override
    public Condition.Type getType() {
        return Condition.Type.GROUP_BY;
    }

    @Override
    public Variable[] getVars() {
        return vars;
    }

    @Override
    public Variable<K> getVarKey() {
        return varKey;
    }

    @Override
    public FunctionN getGroupingFunction() {
        return groupingFunction;
    }
}
