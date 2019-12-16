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
import org.drools.model.functions.Predicate9;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr9ViewItemImpl;

public class SingleConstraint9<A, B, C, D, E, F, G, H, I> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Variable<G> var7;
    private final Variable<H> var8;
    private final Variable<I> var9;
    private final Predicate9<A, B, C, D, E, F, G, H, I> predicate;

    public SingleConstraint9( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                              Variable<H> var8, Variable<I> var9,
                              Predicate9<A, B, C, D, E, F, G, H, I> predicate) {
        super( LambdaPrinter.print(predicate) );
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.predicate = predicate;
    }

    public SingleConstraint9( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Variable<F> var6, Variable<G> var7,
                              Variable<H> var8, Variable<I> var9,
                              Predicate9<A, B, C, D, E, F, G, H, I> predicate) {
        super(exprId);
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.predicate = predicate;
    }

    public SingleConstraint9( Expr9ViewItemImpl<A, B, C, D, E, F, G, H, I> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getVar2(), expr.getVar3(), expr.getVar4(), expr.getVar5(), expr.getVar6(), expr.getVar7(),
                expr.getVar8(), expr.getVar9(), expr.getPredicate());
        setReactivitySpecs( expr.getReactivitySpecs() );
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3, var4, var5, var6, var7, var8, var9};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> {
            return predicate.test((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4], (F) objs[5], (G) objs[6], (H) objs[7], (I) objs[8]);
        };
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        SingleConstraint9<?, ?, ?, ?, ?, ?, ?, ?, ?> that = ( SingleConstraint9<?, ?, ?, ?, ?, ?, ?, ?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( var1, that.var1 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var2, that.var2 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var3, that.var3 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var4, that.var4 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var5, that.var5 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var6, that.var6 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var7, that.var7 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var8, that.var8 ) ) return false;
        if ( !ModelComponent.areEqualInModel( var9, that.var9 ) ) return false;
        return predicate.equals( that.predicate );
    }

    @Override
    public SingleConstraint9<A, B, C, D, E, F, G, H, I> negate() {
        return negate( new SingleConstraint9<>("!" + getExprId(), var1, var2, var3, var4, var5, var6, var7, var8, var9, predicate.negate()) );
    }

    @Override
    public SingleConstraint9<A, B, C, D, E, F, G, H, I> replaceVariable( Variable oldVar, Variable newVar ) {
        if (var1 == oldVar) {
            return new SingleConstraint9<>(getExprId(), newVar, var2, var3, var4, var5, var6, var7, var8, var9, predicate);
        }
        if (var2 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, newVar, var3, var4, var5, var6, var7, var8, var9, predicate);
        }
        if (var3 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, var2, newVar, var4, var5, var6, var7, var8, var9, predicate);
        }
        if (var4 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, var2, var3, newVar, var5, var6, var7, var8, var9, predicate);
        }
        if (var5 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, var2, var3, var4, newVar, var6, var7, var8, var9, predicate);
        }
        if (var6 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, var2, var3, var4, var5, newVar, var7, var8, var9, predicate);
        }
        if (var7 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, var2, var3, var4, var5, var6, newVar, var8, var9, predicate);
        }
        if (var8 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, newVar, var9, predicate);
        }
        if (var9 == oldVar) {
            return new SingleConstraint9<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8, newVar, predicate);
        }
        return this;
    }
}
