/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.event.rule.impl;

import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.Match;


public class BeforeActivationFiredEventImpl extends ActivationEventImpl implements BeforeMatchFiredEvent {

    private long timestamp;

    public BeforeActivationFiredEventImpl(Match activation, KieRuntime kruntime ) {
        super( activation, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public BeforeActivationFiredEventImpl() {
        super();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "==>[BeforeActivationFiredEvent:  getActivation()=" + getMatch()
                + ", getKnowledgeRuntime()=" + getKieRuntime() + "]";
    }    
}
