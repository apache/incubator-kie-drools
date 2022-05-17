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

public interface MutableQuadruple<A, B, C, D> extends Quadruple<A, B, C, D> {

    static <A, B, C, D> MutableQuadruple<A, B, C, D> of(A a, B b, C c, D d) {
        return new MutableQuadrupleImpl<>(a, b, c, d);
    }

    MutableQuadruple<A, B, C, D> setA(A a);

    MutableQuadruple<A, B, C, D> setB(B b);

    MutableQuadruple<A, B, C, D> setC(C c);

    MutableQuadruple<A, B, C, D> setD(D d);

}
