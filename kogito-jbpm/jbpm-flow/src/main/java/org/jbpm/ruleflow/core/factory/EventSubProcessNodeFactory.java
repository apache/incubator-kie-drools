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
package org.jbpm.ruleflow.core.factory;

import org.jbpm.process.core.event.EventTypeFilter;
import org.jbpm.process.core.timer.Timer;
import org.jbpm.ruleflow.core.RuleFlowNodeContainerFactory;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.NodeContainer;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.EventSubProcessNode;
import org.kie.api.definition.process.WorkflowElementIdentifier;

import static org.jbpm.ruleflow.core.Metadata.MESSAGE_REF;

public class EventSubProcessNodeFactory<T extends RuleFlowNodeContainerFactory<T, ?>> extends AbstractCompositeNodeFactory<EventSubProcessNodeFactory<T>, T> {

    public static final String METHOD_KEEP_ACTIVE = "keepActive";
    public static final String METHOD_EVENT = "event";
    public static final String METHOD_TIMER = "timer";

    public EventSubProcessNodeFactory(T nodeContainerFactory, NodeContainer nodeContainer, WorkflowElementIdentifier id) {
        super(nodeContainerFactory, nodeContainer, new EventSubProcessNode(), id);
    }

    public EventSubProcessNodeFactory<T> keepActive(boolean keepActive) {
        ((EventSubProcessNode) getCompositeNode()).setKeepActive(keepActive);
        return this;
    }

    public EventSubProcessNodeFactory<T> event(String event) {
        EventTypeFilter filter = new EventTypeFilter();
        filter.setType(event);
        filter.setCorrelationManager(((RuleFlowProcess) getCompositeNode().getProcess()).getCorrelationManager());
        filter.setMessageRef((String) getNode().getMetaData().get(MESSAGE_REF));
        ((EventSubProcessNode) getCompositeNode()).addEvent(filter);
        return this;
    }

    public EventSubProcessNodeFactory<T> timer(String delay, String period, String date, int timeType) {
        Timer timer = new Timer();
        timer.setDate(date);
        timer.setDelay(delay);
        timer.setPeriod(period);
        timer.setTimeType(timeType);
        ((EventSubProcessNode) getCompositeNode()).addTimer(timer, new DroolsConsequenceAction("java", ""));
        return this;
    }
}
