package org.drools.event;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.common.InternalWorkingMemory;
import org.drools.process.instance.ProcessInstance;
import org.drools.spi.RuleFlowGroup;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.WorkflowProcessInstance;

/**
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowEventSupport implements Serializable {
    
    // TODO separate out process level stuff

    private static final long serialVersionUID = 400L;
    private final List        listeners        = Collections.synchronizedList( new ArrayList() );

    public RuleFlowEventSupport() {
    }

    public void addEventListener(final RuleFlowEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(final RuleFlowEventListener listener) {
        this.listeners.remove( listener );
    }

    public List getEventListeners() {
        return Collections.unmodifiableList( this.listeners );
    }

    public int size() {
        return this.listeners.size();
    }

    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    public void fireBeforeRuleFlowProcessStarted(
            final ProcessInstance instance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowStartedEvent event = new RuleFlowStartedEvent(instance);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .beforeRuleFlowStarted(event, workingMemory);
        }
    }

    public void fireAfterRuleFlowProcessStarted(
            final ProcessInstance instance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowStartedEvent event = new RuleFlowStartedEvent(instance);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .afterRuleFlowStarted(event, workingMemory);
        }
    }

    public void fireBeforeRuleFlowProcessCompleted(
            final WorkflowProcessInstance instance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowCompletedEvent event = new RuleFlowCompletedEvent(
                instance);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .beforeRuleFlowCompleted(event, workingMemory);
        }
    }

    public void fireAfterRuleFlowProcessCompleted(
            final WorkflowProcessInstance instance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowCompletedEvent event = new RuleFlowCompletedEvent(
                instance);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .afterRuleFlowCompleted(event, workingMemory);
        }
    }

    public void fireBeforeRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent(
                ruleFlowGroup);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .beforeRuleFlowGroupActivated(event, workingMemory);
        }
    }

    public void fireAfterRuleFlowGroupActivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent(
                ruleFlowGroup);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .afterRuleFlowGroupActivated(event, workingMemory);
        }
    }

    public void fireBeforeRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent(
                ruleFlowGroup);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .beforeRuleFlowGroupDeactivated(event, workingMemory);
        }
    }

    public void fireAfterRuleFlowGroupDeactivated(
            final RuleFlowGroup ruleFlowGroup,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent(
                ruleFlowGroup);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .afterRuleFlowGroupDeactivated(event, workingMemory);
        }
    }

    public void fireBeforeRuleFlowNodeTriggered(
            final NodeInstance ruleFlowNodeInstance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent(
                ruleFlowNodeInstance);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .beforeRuleFlowNodeTriggered(event, workingMemory);
        }
    }

    public void fireAfterRuleFlowNodeTriggered(
            final NodeInstance ruleFlowNodeInstance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent(
                ruleFlowNodeInstance);

        for (int i = 0, size = this.listeners.size(); i < size; i++) {
            ((RuleFlowEventListener) this.listeners.get(i))
                    .afterRuleFlowNodeTriggered(event, workingMemory);
        }
    }

    public void reset() {
        this.listeners.clear();
    }

}