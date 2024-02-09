/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
