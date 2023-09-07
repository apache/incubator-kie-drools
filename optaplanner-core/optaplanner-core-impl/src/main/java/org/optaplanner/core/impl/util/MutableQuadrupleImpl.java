/*
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

package org.optaplanner.core.impl.util;

import java.util.Objects;

final class MutableQuadrupleImpl<A, B, C, D> implements MutableQuadruple<A, B, C, D> {

    private A a;
    private B b;
    private C c;
    private D d;

    MutableQuadrupleImpl(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public MutableQuadruple<A, B, C, D> setA(A a) {
        this.a = a;
        return this;
    }

    @Override
    public MutableQuadruple<A, B, C, D> setB(B b) {
        this.b = b;
        return this;
    }

    @Override
    public MutableQuadruple<A, B, C, D> setC(C c) {
        this.c = c;
        return this;
    }

    @Override
    public MutableQuadruple<A, B, C, D> setD(D d) {
        this.d = d;
        return this;
    }

    @Override
    public A getA() {
        return a;
    }

    @Override
    public B getB() {
        return b;
    }

    @Override
    public C getC() {
        return c;
    }

    @Override
    public D getD() {
        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MutableQuadrupleImpl<A, B, C, D> that = (MutableQuadrupleImpl<A, B, C, D>) o;
        return Objects.equals(a, that.a)
                && Objects.equals(b, that.b)
                && Objects.equals(c, that.c)
                && Objects.equals(d, that.d);
    }

    @Override
    public int hashCode() { // Not using Objects.hash(Object...) as that would create an array on the hot path.
        int result = Objects.hashCode(a);
        result = 31 * result + Objects.hashCode(b);
        result = 31 * result + Objects.hashCode(c);
        result = 31 * result + Objects.hashCode(d);
        return result;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ", " + d + ")";
    }

}
