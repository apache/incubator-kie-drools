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
package org.drools.ruleunits.impl.factory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.ruleunits.api.DataHandle;

public class DataHandleImpl implements DataHandle {
    private static final AtomicLong counter = new AtomicLong();

    private final long id = counter.incrementAndGet();
    private Object object;

    public DataHandleImpl(Object object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DataHandleImpl that = (DataHandleImpl) o;
        return id == that.id;
    }

    @Override
    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public long getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DataHandleImpl{" +
                "id=" + id +
                ", object=" + object +
                '}';
    }
}
