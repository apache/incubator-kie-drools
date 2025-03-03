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
package org.drools.model.functions;

public final class FunctionUtils {

    private FunctionUtils() { }

    public static <R> FunctionN<R> toFunctionN(final Function0<R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply() );
    }

    public static <A, R> FunctionN<R> toFunctionN(final Function1<A, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply( (A)objs[0] ) );
    }

    public static <A, B, R> FunctionN<R> toFunctionN(final Function2<A, B, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply( (A)objs[0], (B)objs[1] ) );
    }

    public static <A, B, C, R> FunctionN<R> toFunctionN(final Function3<A, B, C, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2]) );
    }

    public static <A, B, C, D, R> FunctionN<R> toFunctionN(final Function4<A, B, C, D, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3]) );
    }

    public static <A, B, C, D, E, R> FunctionN<R> toFunctionN(final Function5<A, B, C, D, E, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4]) );
    }

    public static <A, B, C, D, E, F, R> FunctionN<R> toFunctionN(final Function6<A, B, C, D, E, F, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply((A) objs[0], (B) objs[1], (C) objs[2], (D) objs[3], (E) objs[4], (F) objs[5]) );
    }

    public static <A, B, C, D, E, F, G, R> FunctionN<R> toFunctionN(final Function7<A, B, C, D, E, F, G, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply(
                (A) objs[0],
                (B) objs[1],
                (C) objs[2],
                (D) objs[3],
                (E) objs[4],
                (F) objs[5],
                (G) objs[6]
        ));
    }

    public static <A, B, C, D, E, F, G, H, R> FunctionN<R> toFunctionN(final Function8<A, B, C, D, E, F, G, H, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply(
                (A) objs[0],
                (B) objs[1],
                (C) objs[2],
                (D) objs[3],
                (E) objs[4],
                (F) objs[5],
                (G) objs[6],
                (H) objs[7]
        ));
    }

    public static <A, B, C, D, E, F, G, H, I, R> FunctionN<R> toFunctionN(final Function9<A, B, C, D, E, F, G, H, I, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply(
                (A) objs[0],
                (B) objs[1],
                (C) objs[2],
                (D) objs[3],
                (E) objs[4],
                (F) objs[5],
                (G) objs[6],
                (H) objs[7],
                (I) objs[8]
        ));
    }

    public static <A, B, C, D, E, F, G, H, I, J, R> FunctionN<R> toFunctionN(final Function10<A, B, C, D, E, F, G, H, I, J, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply(
                (A) objs[0],
                (B) objs[1],
                (C) objs[2],
                (D) objs[3],
                (E) objs[4],
                (F) objs[5],
                (G) objs[6],
                (H) objs[7],
                (I) objs[8],
                (J) objs[9]
        ));
    }

    public static <A, B, C, D, E, F, G, H, I, J, K, R> FunctionN<R> toFunctionN(final Function11<A, B, C, D, E, F, G, H, I, J, K, R> f) {
        return f == null ? null : new FunctionN.Impl<>( f, objs -> f.apply(
                (A) objs[0],
                (B) objs[1],
                (C) objs[2],
                (D) objs[3],
                (E) objs[4],
                (F) objs[5],
                (G) objs[6],
                (H) objs[7],
                (I) objs[8],
                (J) objs[9],
                (K) objs[10]
        ));
    }

}
