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
package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;

public class ActivationCreatedEventImpl extends ActivationEventImpl implements MatchCreatedEvent {

    public ActivationCreatedEventImpl(Match activation, KieRuntime kruntime ) {
        super( activation, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public ActivationCreatedEventImpl() {
        super();
    }

    @Override
    public String toString() {
        return "==>[ActivationCreatedEvent: getActivation()=" + getMatch()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + "]";
    }    
}
