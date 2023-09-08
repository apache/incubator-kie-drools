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
package org.drools.model.constraints;

import org.drools.model.Variable;
import org.drools.model.functions.LambdaPrinter;
import org.drools.model.functions.Predicate2;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr2ViewItemImpl;

public class SingleConstraint2<A, B> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Predicate2<A, B> predicate;

    public SingleConstraint2(Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        super( LambdaPrinter.print(predicate), predicate.predicateInformation());
        this.var1 = var1;
        this.var2 = var2;
        this.predicate = predicate;
    }

    public SingleConstraint2(String exprId, Variable<A> var1, Variable<B> var2, Predicate2<A, B> predicate) {
        super(exprId, predicate.predicateInformation());
        this.var1 = var1;
        this.var2 = var2;
        this.predicate = predicate;
    }

    public SingleConstraint2(Expr2ViewItemImpl<A, B> expr) {
        this( expr.getExprId(), expr.getFirstVariable(), expr.getVar2(), expr.getPredicate() );
        setIndex( expr.getIndex() );
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Predicate2 getPredicate2() {
        return predicate;
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[] { var1, var2 };
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test( (A)objs[0], (B)objs[1] );
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint2<?, ?> that = ( SingleConstraint2<?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint2<A, B> negate() {
        return negate( new SingleConstraint2<>("!" + getExprId(), var1, var2, predicate.negate()) );
    }

    @Override
    public SingleConstraint2<A, B> replaceVariable( Variable oldVar, Variable newVar ) {
        if (var1 == oldVar) {
            return new SingleConstraint2<>(getExprId(), newVar, var2, predicate);
        }
        if (var2 == oldVar) {
            return new SingleConstraint2<>(getExprId(), var1, newVar, predicate);
        }
        return this;
    }
}
