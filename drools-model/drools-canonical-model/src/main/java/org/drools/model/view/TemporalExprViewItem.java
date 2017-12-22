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

import org.drools.model.Condition.Type;
import org.drools.model.Variable;
import org.drools.model.functions.temporal.TemporalPredicate;

public class TemporalExprViewItem<T> extends AbstractExprViewItem<T> {

    private final Variable<?> var2;

    private final TemporalPredicate temporalPredicate;

    public TemporalExprViewItem( Variable<T> var1, Variable<?> var2, TemporalPredicate temporalPredicate ) {
        super(getConstraintId(temporalPredicate, var1, var2), var1);
        this.var2 = var2;
        this.temporalPredicate = temporalPredicate;
    }

    public TemporalExprViewItem( String exprId, Variable<T> var1, Variable<?> var2, TemporalPredicate temporalPredicate ) {
        super(exprId, var1);
        this.var2 = var2;
        this.temporalPredicate = temporalPredicate;
    }

    public Variable<?> getSecondVariable() {
        return var2;
    }

    public TemporalPredicate getTemporalPredicate() {
        return temporalPredicate;
    }

    @Override
    public Type getType() {
        return Type.TEMPORAL;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable(), getSecondVariable() };
    }

    private static String getConstraintId(TemporalPredicate temporalPredicate, Variable<?> var1, Variable<?> var2) {
        return temporalPredicate + "_" + var1 + "_" + var2;
    }
}
