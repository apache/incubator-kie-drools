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
package org.drools.testcoverage.common.model;

import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractBean {
    private static final AtomicLong idGenerator = new AtomicLong(0 );

    private final long id;
    private int value;

    protected AbstractBean() {
        id = idGenerator.getAndIncrement();
    }

    public AbstractBean(final int value) {
        this();
        this.value = value;
    }

    public AbstractBean(final long id, final int value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return (int)(id ^ (id >>> 32));
    }

    @Override
    public boolean equals( Object obj ) {
        return this.getClass() == obj.getClass() && id == ((AbstractBean)obj).id;
    }

    public long getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + id + ")";
    }
}
