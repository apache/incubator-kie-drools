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
package org.drools.reactive.api;

/**
 * Controls when the rule engine fires rules after facts are ingested
 * from a reactive connector.
 */
public enum FiringStrategy {

    /**
     * Fire rules after each individual message is appended to the DataStream.
     * Simplest model; highest per-message latency but lowest rule-evaluation delay.
     */
    PER_MESSAGE,

    /**
     * Accumulate messages into micro-batches (by count or time window)
     * and fire rules once per batch. Reduces firing overhead at the cost
     * of slightly delayed rule evaluation.
     */
    MICRO_BATCH,

    /**
     * The connector only inserts facts; the caller is responsible for
     * invoking {@code fire()} or {@code fireUntilHalt()} externally.
     * Use this when you manage the firing lifecycle yourself or when
     * running in daemon/continuous mode.
     */
    EXTERNAL
}
