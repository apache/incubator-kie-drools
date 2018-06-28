/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.view;

import org.drools.model.Variable;
import org.drools.model.functions.temporal.TemporalPredicate;

public class VariableTemporalExprViewItem<T> extends TemporalExprViewItem<T> {

    private final Variable<?> var2;

    public VariableTemporalExprViewItem( Variable<T> var1, Variable<?> var2, TemporalPredicate temporalPredicate ) {
        super(var1, var2, temporalPredicate);
        this.var2 = var2;
    }

    public VariableTemporalExprViewItem( String exprId, Variable<T> var1, Variable<?> var2, TemporalPredicate temporalPredicate ) {
        super(exprId, var1, temporalPredicate);
        this.var2 = var2;
    }

    public Variable<?> getSecondVariable() {
        return var2;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable(), getSecondVariable() };
    }
}
