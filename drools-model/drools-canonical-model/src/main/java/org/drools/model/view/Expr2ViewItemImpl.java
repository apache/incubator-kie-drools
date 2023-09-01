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

import org.drools.model.BetaIndex;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate2;
import org.drools.model.index.BetaIndexImpl;

public class Expr2ViewItemImpl<T, U> extends AbstractExprViewItem<T> implements Expr2ViewItem<T, U> {

    private final Variable<U> var2;
    private final Predicate2<T, U> predicate;

    private BetaIndex<T, U, ?> index;

    public Expr2ViewItemImpl( Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate ) {
        super(predicate.toString(), var1);
        this.var2 = var2;
        this.predicate = predicate;
    }

    public Expr2ViewItemImpl( String exprId, Variable<T> var1, Variable<U> var2, Predicate2<T, U> predicate ) {
        super(exprId, var1);
        this.var2 = var2;
        this.predicate = predicate;
    }

    public Predicate2<T, U> getPredicate() {
        return predicate;
    }

    public Variable<U> getVar2() {
        return var2;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable(), getVar2() };
    }

    @Override
    public Condition.Type getType() {
        return Type.PATTERN;
    }

    public BetaIndex<T, U, ?> getIndex() {
        return index;
    }

    @Override
    public <V> Expr2ViewItemImpl<T, U> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, V> leftOperandExtractor, Function1<U, ?> rightOperandExtractor ) {
        index = new BetaIndexImpl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor );
        return this;
    }

    @Override
    public <V> Expr2ViewItemImpl<T, U> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, V> leftOperandExtractor, Function1<U, ?> rightOperandExtractor, Class<?> rightReturnType ) {
        index = new BetaIndexImpl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, rightReturnType );
        return this;
    }
}
