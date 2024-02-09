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
package org.drools.mvel;

public class GenericTest {

    public static class A {
        public void go() {

        }
    }

    public static class B extends A {
        public void stop() {

        }
    }

    public static interface FactHandle1<T> {
        public T getObject();

        <K> K as(Class<K> klass) throws ClassCastException;
    }

    public static interface FactHandle2 {
        public Object getObject();

        <K> K as(Class<K> klass) throws ClassCastException;
    }

    public void test1() {
        FactHandle1<A> fh = null;
        fh.getObject().go();
        fh.as(B.class).stop();
    }

    public void test2() {
        FactHandle2 fh = null;
        ((A)fh.getObject()).go();
        fh.as(B.class).stop();
    }
}
