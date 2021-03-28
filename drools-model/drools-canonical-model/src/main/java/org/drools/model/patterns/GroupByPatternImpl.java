/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
