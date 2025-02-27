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
import org.drools.model.functions.Predicate18;
import org.drools.model.functions.PredicateN;
import org.drools.model.impl.ModelComponent;
import org.drools.model.view.Expr18ViewItemImpl;

public class SingleConstraint18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> extends AbstractSingleConstraint {

    private final Variable<A> var1;
    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Variable<F> var6;
    private final Variable<G> var7;
    private final Variable<H> var8;
    private final Variable<I> var9;
    private final Variable<J> var10;
    private final Variable<K> var11;
    private final Variable<L> var12;
    private final Variable<M> var13;
    private final Variable<N> var14;
    private final Variable<O> var15;
    private final Variable<P> var16;
    private final Variable<Q> var17;
    private final Variable<R> var18;
    private final Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> predicate;

    public SingleConstraint18(Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5,
                              Variable<F> var6, Variable<G> var7, Variable<H> var8, Variable<I> var9,
                              Variable<J> var10, Variable<K> var11, Variable<L> var12, Variable<M> var13,
                              Variable<N> var14, Variable<O> var15, Variable<P> var16, Variable<Q> var17,
                              Variable<R> var18,
                              Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> predicate) {
        super(LambdaPrinter.print(predicate), predicate.predicateInformation());
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.var10 = var10;
        this.var11 = var11;
        this.var12 = var12;
        this.var13 = var13;
        this.var14 = var14;
        this.var15 = var15;
        this.var16 = var16;
        this.var17 = var17;
        this.var18 = var18;
        this.predicate = predicate;
    }

    public SingleConstraint18(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4,
                              Variable<E> var5, Variable<F> var6, Variable<G> var7, Variable<H> var8,
                              Variable<I> var9, Variable<J> var10, Variable<K> var11, Variable<L> var12,
                              Variable<M> var13, Variable<N> var14, Variable<O> var15, Variable<P> var16,
                              Variable<Q> var17, Variable<R> var18,
                              Predicate18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> predicate) {
        super(exprId, predicate.predicateInformation());
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.var6 = var6;
        this.var7 = var7;
        this.var8 = var8;
        this.var9 = var9;
        this.var10 = var10;
        this.var11 = var11;
        this.var12 = var12;
        this.var13 = var13;
        this.var14 = var14;
        this.var15 = var15;
        this.var16 = var16;
        this.var17 = var17;
        this.var18 = var18;
        this.predicate = predicate;
    }

    public SingleConstraint18(Expr18ViewItemImpl<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> expr) {
        this(expr.getExprId(), expr.getFirstVariable(), expr.getVar2(), expr.getVar3(), expr.getVar4(),
                expr.getVar5(), expr.getVar6(), expr.getVar7(), expr.getVar8(), expr.getVar9(),
                expr.getVar10(), expr.getVar11(), expr.getVar12(), expr.getVar13(), expr.getVar14(),
                expr.getVar15(), expr.getVar16(), expr.getVar17(), expr.getVar18(), expr.getPredicate());
        setReactivitySpecs(expr.getReactivitySpecs());
    }

    @Override
    public Variable[] getVariables() {
        return new Variable[]{var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18};
    }

    @Override
    public PredicateN getPredicate() {
        return objs -> predicate.test((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4],
                (F) objs[5], (G) objs[6], (H) objs[7], (I) objs[8], (J) objs[9],
                (K) objs[10], (L) objs[11], (M) objs[12], (N) objs[13], (O) objs[14],
                (P) objs[15], (Q) objs[16], (R) objs[17]);
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SingleConstraint18<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> that = (SingleConstraint18<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?>) o;

        if (!ModelComponent.areEqualInModel(var1, that.var1)) return false;
        if (!ModelComponent.areEqualInModel(var2, that.var2)) return false;
        if (!ModelComponent.areEqualInModel(var3, that.var3)) return false;
        if (!ModelComponent.areEqualInModel(var4, that.var4)) return false;
        if (!ModelComponent.areEqualInModel(var5, that.var5)) return false;
        if (!ModelComponent.areEqualInModel(var6, that.var6)) return false;
        if (!ModelComponent.areEqualInModel(var7, that.var7)) return false;
        if (!ModelComponent.areEqualInModel(var8, that.var8)) return false;
        if (!ModelComponent.areEqualInModel(var9, that.var9)) return false;
        if (!ModelComponent.areEqualInModel(var10, that.var10)) return false;
        if (!ModelComponent.areEqualInModel(var11, that.var11)) return false;
        if (!ModelComponent.areEqualInModel(var12, that.var12)) return false;
        if (!ModelComponent.areEqualInModel(var13, that.var13)) return false;
        if (!ModelComponent.areEqualInModel(var14, that.var14)) return false;
        if (!ModelComponent.areEqualInModel(var15, that.var15)) return false;
        if (!ModelComponent.areEqualInModel(var16, that.var16)) return false;
        if (!ModelComponent.areEqualInModel(var17, that.var17)) return false;
        if (!ModelComponent.areEqualInModel(var18, that.var18)) return false;
        return predicate.equals(that.predicate);
    }

    @Override
    public SingleConstraint18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> negate() {
        return negate(new SingleConstraint18<>("!" + getExprId(), var1, var2, var3, var4, var5, var6, var7,
                var8, var9, var10, var11, var12, var13, var14, var15,
                var16, var17, var18, predicate.negate()));
    }

    @Override
    public SingleConstraint18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> replaceVariable(Variable oldVar, Variable newVar) {
        if (var1 == oldVar) {
            return new SingleConstraint18<>(getExprId(), newVar, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var2 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, newVar, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var3 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, newVar, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var4 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, newVar, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var5 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, newVar, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var6 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, newVar, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var7 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, newVar, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var8 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, newVar,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var9 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    newVar, var10, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var10 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, newVar, var11, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var11 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, newVar, var12, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var12 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, newVar, var13, var14, var15, var16, var17, var18, predicate);
        }
        if (var13 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, newVar, var14, var15, var16, var17, var18, predicate);
        }
        if (var14 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, newVar, var15, var16, var17, var18, predicate);
        }
        if (var15 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, newVar, var16, var17, var18, predicate);
        }
        if (var16 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, newVar, var17, var18, predicate);
        }
        if (var17 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, newVar,var18, predicate);
        }
        if (var18 == oldVar) {
            return new SingleConstraint18<>(getExprId(), var1, var2, var3, var4, var5, var6, var7, var8,
                    var9, var10, var11, var12, var13, var14, var15, var16, var17, newVar, predicate);
        }
        return this;
    }
}
