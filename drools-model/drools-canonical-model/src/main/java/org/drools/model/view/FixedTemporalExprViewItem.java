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

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.temporal.TemporalPredicate;

public class FixedTemporalExprViewItem<T> extends TemporalExprViewItem<T> {

    private final Function1<?,?> f1;
    private final long value;

    public FixedTemporalExprViewItem( Variable<T> var1, Function1<?,?> f1, long value, TemporalPredicate temporalPredicate ) {
        super(var1, value, temporalPredicate);
        this.f1 = f1;
        this.value = value;
    }

    public FixedTemporalExprViewItem( String exprId, Variable<T> var1, Function1<?,?> f1, long value, TemporalPredicate temporalPredicate ) {
        super(exprId, var1, temporalPredicate);
        this.f1 = f1;
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public Function1<?, ?> getF1() {
        return f1;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable() };
    }
}
