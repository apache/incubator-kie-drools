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
import org.drools.model.functions.Predicate4;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr4ViewItemImpl;

public class SingleConstraint4<A, B, C, D> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Predicate4<A, B, C, D> predicate;

    public SingleConstraint4( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Predicate4<A, B, C, D> predicate) {
        super( LambdaPrinter.print(predicate), predicate.predicateInformation() );
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.predicate = predicate;
    }

    public SingleConstraint4( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Predicate4<A, B, C, D> predicate) {
        super(exprId, predicate.predicateInformation());
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.predicate = predicate;
    }

    public SingleConstraint4( Expr4ViewItemImpl<A, B, C, D> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getVar2(), expr.getVar3(), expr.getVar4(), expr.getPredicate());
        setIndex( expr.getIndex() );
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3, var4};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3]);
    }

    @Override
    public Predicate4 getPredicate4() {
        return predicate;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint4<?, ?, ?, ?> that = ( SingleConstraint4<?, ?, ?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var3, that.var3 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var4, that.var4 ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint4<A, B, C, D> negate() {
        return negate( new SingleConstraint4<>("!" + getExprId(), var1, var2, var3, var4, predicate.negate()) );
    }

    @Override
    public SingleConstraint4<A, B, C, D> replaceVariable( Variable oldVar, Variable newVar ) {
        if (var1 == oldVar) {
            return new SingleConstraint4<>(getExprId(), newVar, var2, var3, var4, predicate);
        }
        if (var2 == oldVar) {
            return new SingleConstraint4<>(getExprId(), var1, newVar, var3, var4, predicate);
        }
        if (var3 == oldVar) {
            return new SingleConstraint4<>(getExprId(), var1, var2, newVar, var4, predicate);
        }
        if (var4 == oldVar) {
            return new SingleConstraint4<>(getExprId(), var1, var2, var3, newVar, predicate);
        }
        return this;
    }
}
