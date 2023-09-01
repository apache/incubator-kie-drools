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
package org.kie.dmn.api.core.event;

public interface DMNRuntimeEventListener {

    default void beforeEvaluateDecision(BeforeEvaluateDecisionEvent event) {}

    default void afterEvaluateDecision(AfterEvaluateDecisionEvent event) {}

    default void beforeEvaluateBKM(BeforeEvaluateBKMEvent event) {}

    default void afterEvaluateBKM(AfterEvaluateBKMEvent event) {}

    default void beforeEvaluateContextEntry(BeforeEvaluateContextEntryEvent event) {}

    default void afterEvaluateContextEntry(AfterEvaluateContextEntryEvent event) {}

    default void beforeEvaluateDecisionTable(BeforeEvaluateDecisionTableEvent event) {}

    default void afterEvaluateDecisionTable(AfterEvaluateDecisionTableEvent event) {}

    default void beforeEvaluateDecisionService(BeforeEvaluateDecisionServiceEvent event) {}

    default void afterEvaluateDecisionService(AfterEvaluateDecisionServiceEvent event) {}

    default void beforeInvokeBKM(BeforeInvokeBKMEvent event) {}

    default void afterInvokeBKM(AfterInvokeBKMEvent event) {}

    default void beforeEvaluateAll(BeforeEvaluateAllEvent event) {}

    default void afterEvaluateAll(AfterEvaluateAllEvent event) {}
}
