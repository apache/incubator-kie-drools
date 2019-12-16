/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.LambdaPrinter;
import org.drools.model.functions.Predicate1;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr1ViewItemImpl;

public class SingleConstraint1<A> extends AbstractSingleConstraint {

    private final Variable<A> variable;
    private final Predicate1<A> predicate;

    public SingleConstraint1(Variable<A> variable, Predicate1<A> predicate) {
        super( LambdaPrinter.print(predicate) );
        this.variable = variable;
        this.predicate = predicate;
    }

    public SingleConstraint1(String exprId, Variable<A> variable, Predicate1<A> predicate) {
        super(exprId);
        this.variable = variable;
        this.predicate = predicate;
    }

    public SingleConstraint1(Expr1ViewItemImpl<A> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getPredicate());
        setIndex( expr.getIndex() );
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { variable };
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test( (A)objs[0] );
    }

    @Override
    public Predicate1 getPredicate1() {
        return predicate;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint1<?> that = ( SingleConstraint1<?> ) o;

        if ( !ModelComponent.areEqualInModel( variable, that.variable ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint1<A> negate() {
        return negate( new SingleConstraint1<>("!" + getExprId(), variable, predicate.negate()) );
    }

    @Override
    public SingleConstraint1<A> replaceVariable( Variable oldVar, Variable newVar ) {
        if (variable == oldVar) {
            return new SingleConstraint1<>(getExprId(), newVar, predicate);
        }
        return this;
    }
}
