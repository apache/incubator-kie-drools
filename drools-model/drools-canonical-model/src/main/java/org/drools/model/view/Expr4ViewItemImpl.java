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

import org.drools.model.BetaIndex3;
import org.drools.model.Condition.Type;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Predicate4;
import org.drools.model.index.BetaIndex3Impl;

public class Expr4ViewItemImpl<A, B, C, D> extends AbstractExprViewItem<A> implements Expr4ViewItem<A, B, C, D> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Variable<D> var4;
    private final Predicate4<A, B, C, D> predicate;

    private BetaIndex3<A, B, C, D, ?> index;

    public Expr4ViewItemImpl( Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Predicate4<A, B, C, D> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.predicate = predicate;
    }

    public Expr4ViewItemImpl( String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Variable<D> var4, Predicate4<A, B, C, D> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.var4 = var4;
        this.predicate = predicate;
    }

    public Predicate4<A, B, C, D> getPredicate() {
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

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getVar2(), getVar3(), getVar4()};
    }

    @Override
    public Type getType() {
        return Type.PATTERN;
    }

    public BetaIndex3<A, B, C, D, ?> getIndex() {
        return index;
    }

    @Override
    public <V> Expr4ViewItemImpl<A, B, C, D> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function3<B, C, D, ?> rightOperandExtractor ) {
        index = new BetaIndex3Impl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, Object.class );
        return this;
    }

    @Override
    public <V> Expr4ViewItemImpl<A, B, C, D> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function3<B, C, D, ?> rightOperandExtractor, Class<?> rightReturnType ) {
        index = new BetaIndex3Impl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, rightReturnType );
        return this;
    }
}
