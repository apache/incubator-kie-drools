package org.drools.model;

import org.drools.model.functions.FunctionN;

public interface GroupByPattern<T, K> extends AccumulatePattern<T> {

    Variable[] getVars();

    Variable<K> getVarKey();

    FunctionN getGroupingFunction();
}
