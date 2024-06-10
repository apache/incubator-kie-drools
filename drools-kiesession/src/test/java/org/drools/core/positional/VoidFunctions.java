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

package org.drools.core.positional;

public class VoidFunctions {

    interface VoidFunction1<A>  {
        void apply(A a);
    }

    interface VoidFunction2<A, B>  {
        void apply(A a, B b);
    }

    interface VoidFunction3<A, B, C>  {
        void apply(A a, B b, C c);
    }

    interface VoidFunction4<A, B, C, D>  {
        void apply(A a, B b, C c, D d);
    }

    interface VoidFunction5<A, B, C, D, E>  {
        void apply(A a, B b, C c, D d, E e);
    }

    interface VoidFunction6<A, B, C, D, E, F>  {
        void apply(A a, B b, C c, D d, E e, F f);
    }
}
