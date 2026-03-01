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
package org.drools.reactive.pulsar;

/**
 * Maps to Pulsar's subscription types, controlling how messages are
 * distributed across consumers in the same subscription.
 */
public enum PulsarSubscriptionType {

    /** Only one consumer receives messages. */
    EXCLUSIVE,

    /** Messages are distributed across consumers round-robin. */
    SHARED,

    /** One active consumer at a time; failover to the next on disconnect. */
    FAILOVER,

    /** Messages are distributed by key. */
    KEY_SHARED
}
