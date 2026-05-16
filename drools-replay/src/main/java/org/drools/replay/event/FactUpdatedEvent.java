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
package org.drools.replay.event;

public class FactUpdatedEvent extends ExecutionEvent {

    private final String factClassName;
    private final String factIdentity;
    private final String oldFactToString;
    private final String newFactToString;
    private final String triggeringRule;

    public FactUpdatedEvent(long sequenceNumber, String factClassName, String factIdentity,
                            String oldFactToString, String newFactToString, String triggeringRule) {
        super(sequenceNumber, EventType.FACT_UPDATED);
        this.factClassName = factClassName;
        this.factIdentity = factIdentity;
        this.oldFactToString = oldFactToString;
        this.newFactToString = newFactToString;
        this.triggeringRule = triggeringRule;
    }

    public String getFactClassName() {
        return factClassName;
    }

    public String getFactIdentity() {
        return factIdentity;
    }

    public String getOldFactToString() {
        return oldFactToString;
    }

    public String getNewFactToString() {
        return newFactToString;
    }

    public String getTriggeringRule() {
        return triggeringRule;
    }

    @Override
    public String toString() {
        return String.format("%s UPDATE %s(%s)%s",
                super.toString(), factClassName, factIdentity,
                triggeringRule != null ? " by " + triggeringRule : "");
    }
}