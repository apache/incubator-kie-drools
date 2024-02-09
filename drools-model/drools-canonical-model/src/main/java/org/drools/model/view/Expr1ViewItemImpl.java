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

import org.drools.model.AlphaIndex;
import org.drools.model.Condition;
import org.drools.model.Condition.Type;
import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Predicate1;
import org.drools.model.index.AlphaIndexImpl;

public class Expr1ViewItemImpl<T> extends AbstractExprViewItem<T> implements Expr1ViewItem<T> {

    private final Predicate1<T> predicate;

    private AlphaIndex<T, ?> index;

    public Expr1ViewItemImpl( Variable<T> var, Predicate1<T> predicate ) {
        super(predicate.toString(), var);
        this.predicate = predicate;
    }

    public Expr1ViewItemImpl( String exprId, Variable<T> var, Predicate1<T> predicate ) {
        super(exprId, var);
        this.predicate = predicate;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { getFirstVariable() };
    }

    public Predicate1<T> getPredicate() {
        return predicate;
    }

    @Override
    public Condition.Type getType() {
        return Type.PATTERN;
    }

    public AlphaIndex<T, ?> getIndex() {
        return index;
    }

    @Override
    public <U> Expr1ViewItemImpl<T> indexedBy( Class<U> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, U> leftOperandExtractor, U rightValue ) {
        index = new AlphaIndexImpl<>( indexedClass, constraintType, indexId, leftOperandExtractor, rightValue);
        return this;
    }
}
