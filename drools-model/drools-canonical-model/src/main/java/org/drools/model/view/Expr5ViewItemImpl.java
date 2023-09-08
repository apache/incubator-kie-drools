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

import org.drools.model.BetaIndex4;
import org.drools.model.Condition.Type;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function4;
import org.drools.model.functions.Predicate5;
import org.drools.model.index.BetaIndex4Impl;

public class Expr5ViewItemImpl<A, B, C, D, E> extends AbstractExprViewItem<A> implements Expr5ViewItem<A, B, C, D, E> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Variable<E> var5;
    private final Predicate5<A, B, C, D, E> predicate;

    private BetaIndex4<A, B, C, D, E, ?> index;

    public Expr5ViewItemImpl( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.predicate = predicate;
    }

    public Expr5ViewItemImpl( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Variable<E> var5, Predicate5<A, B, C, D, E> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.var5 = var5;
        this.predicate = predicate;
    }

    public Predicate5<A, B, C, D, E> getPredicate() {
        return predicate;
    }

    public Variable<B> getVar2() {
        return var2;
    }

    public Variable<C> getVar3() {
        return var3;
    }

    public Variable<D> getVar4() {
        return var4;
    }

    public Variable<E> getVar5() {
        return var5;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getVar2(), getVar3(), getVar4(), getVar5()};
    }

    @Override
    public Type getType() {
        return Type.PATTERN;
    }

    public BetaIndex4<A, B, C, D, E, ?> getIndex() {
        return index;
    }

    @Override
    public <V> Expr5ViewItemImpl<A, B, C, D, E> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor ) {
        index = new BetaIndex4Impl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, Object.class );
        return this;
    }

    @Override
    public <V> Expr5ViewItemImpl<A, B, C, D, E> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor, Class<?> rightReturnType ) {
        index = new BetaIndex4Impl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, rightReturnType );
        return this;
    }
}
