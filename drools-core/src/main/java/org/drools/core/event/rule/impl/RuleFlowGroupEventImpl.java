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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.kie.api.event.rule.RuleFlowGroupEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.RuleFlowGroup;

public class RuleFlowGroupEventImpl implements RuleFlowGroupEvent, Externalizable  {
    private RuleFlowGroup ruleFlowGroup;
    private KieRuntime kruntime;

    public RuleFlowGroupEventImpl(RuleFlowGroup ruleFlowGroup, KieRuntime kruntime) {
        this.ruleFlowGroup = ruleFlowGroup;
        this.kruntime = kruntime;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public RuleFlowGroupEventImpl() {
    }

    public RuleFlowGroup getRuleFlowGroup() {
        return ruleFlowGroup;
    }

    public KieRuntime getKieRuntime() {
        return this.kruntime;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        new SerializableRuleFlowGroup( this.ruleFlowGroup ).writeExternal( out );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.ruleFlowGroup = new SerializableRuleFlowGroup( );
        ((SerializableRuleFlowGroup)this.ruleFlowGroup).readExternal( in );
        this.kruntime = null; // we null this as it isn't serializable
    }

    @Override
    public String toString() {
        return "==>[RuleFlowGroupEventImpl: getRuleFlowGroup()=" + getRuleFlowGroup() + ", getKnowledgeRuntime()="
                + getKieRuntime() + "]";
    }
}
