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

import org.drools.model.Condition;
import org.drools.model.Variable;

public class ExistentialExprViewItem<T> extends AbstractExprViewItem<T> {

    private final Condition.Type type;
    private final ExprViewItem expression;

    public ExistentialExprViewItem(Condition.Type type, ExprViewItem expression) {
        super(expression.getFirstVariable());
        this.type = type;
        this.expression = expression;
    }

    public ExprViewItem getExpression() {
        return expression;
    }

    @Override
    public Variable<?>[] getVariables() {
        return expression.getVariables();
    }

    @Override
    public Condition.Type getType() {
        return type;
    }
}
