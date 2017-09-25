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

package org.drools.model.consequences;

import org.drools.model.Condition;
import org.drools.model.SingleConstraint;
import org.drools.model.Variable;

public class ConditionalNamedConsequenceImpl implements Condition {
    private final SingleConstraint expr;
    private final NamedConsequenceImpl thenConsequence;
    private final ConditionalNamedConsequenceImpl elseBranch;

    public ConditionalNamedConsequenceImpl( SingleConstraint expr, NamedConsequenceImpl thenConsequence, ConditionalNamedConsequenceImpl elseBranch ) {
        this.expr = expr;
        this.thenConsequence = thenConsequence;
        this.elseBranch = elseBranch;
    }

    public SingleConstraint getExpr() {
        return expr;
    }

    public NamedConsequenceImpl getThenConsequence() {
        return thenConsequence;
    }

    public ConditionalNamedConsequenceImpl getElseBranch() {
        return elseBranch;
    }

    @Override
    public Type getType() {
        return Type.CONSEQUENCE;
    }

    @Override
    public Variable<?>[] getBoundVariables() {
        throw new UnsupportedOperationException();
    }
}
