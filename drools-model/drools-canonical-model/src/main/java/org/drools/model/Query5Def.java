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

package org.drools.model;

import org.drools.model.view.QueryCallViewItem;

public interface Query5Def<A, B, C, D, E> extends QueryDef {

    default QueryCallViewItem call(Argument<A> aVar, Argument<B> bVar, Argument<C> cVar, Argument<D> dVar, Argument<E> eVar) {
        return call( true, aVar, bVar, cVar, dVar, eVar);
    }

    QueryCallViewItem call(boolean open, Argument<A> aVar, Argument<B> bVar, Argument<C> cVar, Argument<D> dVar, Argument<E> eVar);

    Variable<A> getArg1();
    Variable<B> getArg2();
    Variable<C> getArg3();
    Variable<D> getArg4();
    Variable<E> getArg5();
}
