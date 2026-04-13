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
package org.drools.reactive.debezium;

/**
 * A wrapper around a database change event captured by Debezium.
 * Contains the deserialized fact (the after-image for creates/updates,
 * the before-image for deletes) along with CDC metadata.
 *
 * @param <T> the deserialized fact type
 */
public class ChangeEvent<T> {

    private final ChangeEventOperation operation;
    private final T value;
    private final String source;
    private final String table;
    private final long timestamp;

    public ChangeEvent(ChangeEventOperation operation, T value, String source, String table, long timestamp) {
        this.operation = operation;
        this.value = value;
        this.source = source;
        this.table = table;
        this.timestamp = timestamp;
    }

    public ChangeEventOperation getOperation() {
        return operation;
    }

    /** The deserialized row data. May be {@code null} for delete events. */
    public T getValue() {
        return value;
    }

    public String getSource() {
        return source;
    }

    public String getTable() {
        return table;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ChangeEvent{op=" + operation + ", table=" + table + ", source=" + source + '}';
    }
}
