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

import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.RuleFlowGroup;

public class RuleFlowGroupActivatedEventImpl extends RuleFlowGroupEventImpl implements RuleFlowGroupActivatedEvent {

    private static final long serialVersionUID = 510L;

    public RuleFlowGroupActivatedEventImpl(final RuleFlowGroup ruleFlowGroup, KieRuntime kruntime ) {
        super( ruleFlowGroup, kruntime );
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public RuleFlowGroupActivatedEventImpl() {
        super();
    }

    @Override
    public String toString() {
        return "==>[RuleFlowGroupActivated(name=" + getRuleFlowGroup().getName() + ")]";
    }
}
