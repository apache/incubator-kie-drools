/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.util;

/**
 * A tuple of four values.
 * Two instances {@link #equals(Object) are equal} if all four values in the first instance are equal to their counterpart in
 * the other instance.
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public interface Quadruple<A, B, C, D> {

    static <A, B, C, D> Quadruple<A, B, C, D> of(A a, B b, C c, D d) {
        return new MutableQuadrupleImpl<>(a, b, c, d);
    }

    A getA();

    B getB();

    C getC();

    D getD();

}
