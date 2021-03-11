/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.view;

import org.drools.model.Index;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function4;

public interface Expr5ViewItem<A, B, C, D, E> extends ExprNViewItem<A> {

    <V> Expr5ViewItemImpl<A, B, C, D, E> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor );

    <V> Expr5ViewItemImpl<A, B, C, D, E> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor, Class<?> rightReturnType );

}
