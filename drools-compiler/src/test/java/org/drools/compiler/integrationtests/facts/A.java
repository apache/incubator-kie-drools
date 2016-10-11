/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.facts;

import java.util.concurrent.atomic.AtomicLong;

public class A {

    private static final AtomicLong idGenerator = new AtomicLong( 0 );

    private final long id;
    private int value;

    protected A() {
        id = idGenerator.getAndIncrement();
    }

    public A(final int value) {
        this();
        this.value = value;
    }

    public A(final long id, final int value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }

    @Override
    public boolean equals( Object obj ) {
        return this.getClass() == obj.getClass() && id == ((A)obj).id;
    }

    public long getId() {
        return id;
    }

    public static long getAndIncrementIdGeneratorValue() {
        return idGenerator.getAndIncrement();
    }

    public static void setIdGeneratorValue(final long value) {
        idGenerator.set(value);
    }

    public int getValue() {
        return value;
    }

    public Integer getBoxedValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }
}
