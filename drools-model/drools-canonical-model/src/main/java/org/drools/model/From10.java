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

package org.drools.model;

import org.drools.model.functions.Function10;

public interface From10<A, B, C, D, E, F, G, H, I, J> extends From<A> {
    Variable<B> getVariable2();
    Variable<C> getVariable3();
    Variable<D> getVariable4();
    Variable<E> getVariable5();
    Variable<F> getVariable6();
    Variable<G> getVariable7();
    Variable<H> getVariable8();
    Variable<I> getVariable9();
    Variable<J> getVariable10();
    Function10<A, B, C, D, E, F, G, H, I, J, ?> getProvider();
}