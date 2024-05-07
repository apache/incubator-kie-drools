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

public class Functions {

    interface Function1<A, R>  {
        R apply(A a);
    }

    interface Function2<A, B, R>  {
        R apply(A a, B b);
    }

    interface Function3<A, B, C, R>  {
        R apply(A a, B b, C c);
    }

    interface Function4<A, B, C, D, R>  {
        R apply(A a, B b, C c, D d);
    }

    interface Function5<A, B, C, D, E, R>  {
        R apply(A a, B b, C c, D d, E e);
    }

    interface Function6<A, B, C, D, E, F, R>  {
        R apply(A a, B b, C c, D d, E e, F f);
    }
}
