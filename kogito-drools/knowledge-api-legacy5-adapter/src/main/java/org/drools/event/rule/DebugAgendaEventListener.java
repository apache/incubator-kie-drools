/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.event.rule;

import java.io.PrintStream;

public class DebugAgendaEventListener
    implements
    AgendaEventListener {
    
    private PrintStream stream;
    
    public DebugAgendaEventListener() {
        this.stream =  System.err;
    }
    
    public DebugAgendaEventListener(PrintStream stream) {
        this.stream = stream;
    }

    public void activationCancelled(ActivationCancelledEvent event) {
        stream.println( event );
    }

    public void activationCreated(ActivationCreatedEvent event) {
        stream.println( event );
    }

    public void afterActivationFired(AfterActivationFiredEvent event) {
        stream.println( event );
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
        stream.println( event );
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
        stream.println( event );
    }

    public void beforeActivationFired(BeforeActivationFiredEvent event) {
        stream.println( event );
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        stream.println( event );  
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
        stream.println( event );
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        stream.println( event );
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
        stream.println( event );
    }

}
