/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.fluent;

public interface CheckableFluent<P> {

    <A> BoundCheckable1<P, A> as(Class<A> a);

    interface Predicate1<A> {
        boolean test( A a );
    }

    interface BoundCheckable1<P, A> {
        P test( Predicate1<A> predicate );
        P test( Predicate1<A> predicate, String reason );

        UnboundCheckable1<P, A> given(String name);
    }

    interface UnboundCheckable1<P, A> {
        <B> BoundCheckable2<P, A, B> as(Class<B> b);
    }

    interface Predicate2<A, B> {
        boolean test( A a, B b );
    }

    interface BoundCheckable2<P, A, B> {
        P test( Predicate2<A, B> predicate );
        P test( Predicate2<A, B> predicate, String reason );
    }
}
