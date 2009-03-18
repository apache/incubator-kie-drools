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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.spi.RuleFlowGroup;

/**
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowEventSupport implements Externalizable {

    // TODO separate out process level stuff

    private static final long                 serialVersionUID = 400L;
    private List<RuleFlowEventListener> listeners        = new CopyOnWriteArrayList<RuleFlowEventListener>();

    public RuleFlowEventSupport() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        listeners   = (List<RuleFlowEventListener>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(listeners);
    }

    public void addEventListener(final RuleFlowEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(final RuleFlowEventListener listener) {
        this.listeners.remove( listener );
    }

    public List<RuleFlowEventListener> getEventListeners() {
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

        final RuleFlowStartedEvent event = new RuleFlowStartedEvent( instance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.beforeRuleFlowStarted( event, workingMemory );
        }
    }

    public void fireAfterRuleFlowProcessStarted(
            final ProcessInstance instance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowStartedEvent event = new RuleFlowStartedEvent( instance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.afterRuleFlowStarted( event, workingMemory );
        }
    }

    public void fireBeforeRuleFlowProcessCompleted(
            final WorkflowProcessInstance instance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowCompletedEvent event = new RuleFlowCompletedEvent( instance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.beforeRuleFlowCompleted( event, workingMemory );
        }
    }

    public void fireAfterRuleFlowProcessCompleted(
            final WorkflowProcessInstance instance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowCompletedEvent event = new RuleFlowCompletedEvent( instance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.afterRuleFlowCompleted( event, workingMemory );
        }
    }

    public void fireBeforeRuleFlowGroupActivated(final RuleFlowGroup ruleFlowGroup,
                                                 final InternalWorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent( ruleFlowGroup );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.beforeRuleFlowGroupActivated( event, workingMemory );
        }
    }

    public void fireAfterRuleFlowGroupActivated(final RuleFlowGroup ruleFlowGroup,
                                                final InternalWorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final RuleFlowGroupActivatedEvent event = new RuleFlowGroupActivatedEvent( ruleFlowGroup );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.afterRuleFlowGroupActivated( event, workingMemory );
        }
    }

    public void fireBeforeRuleFlowGroupDeactivated(final RuleFlowGroup ruleFlowGroup,
                                                   final InternalWorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent( ruleFlowGroup );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.beforeRuleFlowGroupDeactivated( event, workingMemory );
        }
    }

    public void fireAfterRuleFlowGroupDeactivated(final RuleFlowGroup ruleFlowGroup,
                                                  final InternalWorkingMemory workingMemory) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final RuleFlowGroupDeactivatedEvent event = new RuleFlowGroupDeactivatedEvent( ruleFlowGroup );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.afterRuleFlowGroupDeactivated( event, workingMemory );
        }
    }

    public void fireBeforeRuleFlowNodeTriggered(
            final NodeInstance ruleFlowNodeInstance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent( ruleFlowNodeInstance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.beforeRuleFlowNodeTriggered( event, workingMemory );
        }
    }

    public void fireAfterRuleFlowNodeTriggered(
            final NodeInstance ruleFlowNodeInstance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent( ruleFlowNodeInstance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.afterRuleFlowNodeTriggered( event, workingMemory );
        }
    }

    public void fireBeforeRuleFlowNodeLeft(
            final NodeInstance ruleFlowNodeInstance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent( ruleFlowNodeInstance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.beforeRuleFlowNodeLeft( event, workingMemory );
        }
    }

    public void fireAfterRuleFlowNodeLeft(
            final NodeInstance ruleFlowNodeInstance,
            final InternalWorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowNodeTriggeredEvent event = new RuleFlowNodeTriggeredEvent( ruleFlowNodeInstance );

        for ( RuleFlowEventListener listener: listeners ) {
            listener.afterRuleFlowNodeLeft( event, workingMemory );
        }
    }

    public void fireBeforeVariableChange(final ProcessInstance instance,
                                            final String name,
                                            final Object value,
                                            WorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowVariableChangeEvent event = new RuleFlowVariableChangeEvent(instance, name, value );

        for ( RuleFlowEventListener listener: listeners ) {
            if(listener instanceof RuleFlowEventListenerExtension) {
                ((RuleFlowEventListenerExtension) listener).beforeVariableChange(event, workingMemory);
            }
        }
    }

     public void fireAfterVariableChange(final ProcessInstance instance,
                                            final String name,
                                            final Object value,
                                            WorkingMemory workingMemory) {
        if (this.listeners.isEmpty()) {
            return;
        }

        final RuleFlowVariableChangeEvent event = new RuleFlowVariableChangeEvent(instance, name, value );

        for ( RuleFlowEventListener listener: listeners ) {
            if(listener instanceof RuleFlowEventListenerExtension) {
                ((RuleFlowEventListenerExtension) listener).afterVariableChange(event, workingMemory);
            }
            
        }
    }

    public void reset() {
        this.listeners.clear();
    }

}
