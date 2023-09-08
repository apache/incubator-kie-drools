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

import org.drools.model.BetaIndex2;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.functions.Predicate3;
import org.drools.model.index.BetaIndex2Impl;

public class Expr3ViewItemImpl<A, B, C> extends AbstractExprViewItem<A> implements Expr3ViewItem<A, B, C> {

    private final Variable<B> var2;
    private final Variable<C> var3;
    private final Predicate3<A, B, C> predicate;

    private BetaIndex2<A, B, C, ?> index;

    public Expr3ViewItemImpl(Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public Expr3ViewItemImpl(String exprId, Variable<A> var1, Variable<B> var2, Variable<C> var3, Predicate3<A, B, C> predicate) {
        super(exprId, var1);
        this.var2 = var2;
        this.var3 = var3;
        this.predicate = predicate;
    }

    public Predicate3<A, B, C> getPredicate() {
        return predicate;
    }

    public Variable<B> getVar2() {
        return var2;
    }

    public Variable<C> getVar3() {
        return var3;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{getFirstVariable(), getVar2(), getVar3()};
    }

    @Override
    public Condition.Type getType() {
        return Type.PATTERN;
    }

    public BetaIndex2<A, B, C, ?> getIndex() {
        return index;
    }

    @Override
    public <V> Expr3ViewItemImpl<A, B, C> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function2<B, C, ?> rightOperandExtractor ) {
        index = new BetaIndex2Impl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, Object.class );
        return this;
    }

    @Override
    public <V> Expr3ViewItemImpl<A, B, C> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function2<B, C, ?> rightOperandExtractor, Class<?> rightReturnType ) {
        index = new BetaIndex2Impl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, rightReturnType );
        return this;
    }
}
