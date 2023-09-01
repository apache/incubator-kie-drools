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
import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.accumulate.AccumulateFunction;

public class AccumulateExprViewItem<T> extends AbstractExprViewItem<T> {

    private final ViewItem<T> expr;
    private final AccumulateFunction[] accumulateFunctions;

    public AccumulateExprViewItem(ViewItem<T> expr, AccumulateFunction... accumulateFunctions) {
        super(expr.getFirstVariable());
        this.expr = expr.get();
        this.accumulateFunctions = accumulateFunctions;
    }

    @Override
    public Condition.Type getType() {
        return Type.ACCUMULATE;
    }

    @Override
    public Variable<?>[] getVariables() {
        return expr.getVariables();
    }

    public ViewItem<T> getExpr() {
        return expr;
    }

    public AccumulateFunction[] getAccumulateFunctions() {
        return accumulateFunctions;
    }
}
