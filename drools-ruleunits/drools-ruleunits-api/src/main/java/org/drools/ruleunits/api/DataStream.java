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
package org.drools.ruleunits.api;

/**
 * A {@link DataSource} of immutable data.
 * By default, this Stream doesn't retain any data and just forwards the facts appended to it to the {@link DataProcessor}s
 * that are registered at the time of insertion. In particular this means that if a fact is inserted into the DataStream declared in a
 * {@link RuleUnitData} before any {@link RuleUnitInstance} has been created from it, this fact's insertion will be simply get lost.
 * It can be optionally buffered and retain a fixed amount of the latest appended facts.
 * @param <T> The type of objects managed by this DataSource.
 */
public interface DataStream<T> extends DataSource<T> {

    /**
     * Append an object to this stream of data.
     */
    void append(T value);
}
