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
import org.drools.model.functions.Predicate5;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr5ViewItemImpl;

public class SingleConstraint5<A, B, C, D, E> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Predicate5<A, B, C, D, E> predicate;

    public SingleConstraint5( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        super( LambdaPrinter.print(predicate), predicate.predicateInformation() );
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.predicate = predicate;
    }

    public SingleConstraint5( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        super(exprId, predicate.predicateInformation());
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.predicate = predicate;
    }

    public SingleConstraint5( Expr5ViewItemImpl<A, B, C, D, E> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getVar2(), expr.getVar3(), expr.getVar4(), expr.getVar5(), expr.getPredicate());
        setIndex( expr.getIndex() );
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3, var4, var5};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4]);
    }

    @Override
    public Predicate5 getPredicate5() {
        return predicate;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint5<?, ?, ?, ?, ?> that = ( SingleConstraint5<?, ?, ?, ?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var3, that.var3 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var4, that.var4 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var5, that.var5 ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint5<A, B, C, D, E> negate() {
        return negate( new SingleConstraint5<>("!" + getExprId(), var1, var2, var3, var4, var5, predicate.negate()) );
    }

    @Override
    public SingleConstraint5<A, B, C, D, E> replaceVariable( Variable oldVar, Variable newVar ) {
        if (var1 == oldVar) {
            return new SingleConstraint5<>(getExprId(), newVar, var2, var3, var4, var5, predicate);
        }
        if (var2 == oldVar) {
            return new SingleConstraint5<>(getExprId(), var1, newVar, var3, var4, var5, predicate);
        }
        if (var3 == oldVar) {
            return new SingleConstraint5<>(getExprId(), var1, var2, newVar, var4, var5, predicate);
        }
        if (var4 == oldVar) {
            return new SingleConstraint5<>(getExprId(), var1, var2, var3, newVar, var5, predicate);
        }
        if (var5 == oldVar) {
            return new SingleConstraint5<>(getExprId(), var1, var2, var3, var4, newVar, predicate);
        }
        return this;
    }
}
