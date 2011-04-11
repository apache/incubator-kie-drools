/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.informer.listener;

import org.drools.event.rule.*;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaGroup;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class DebugAgendaEventListener implements AgendaEventListener {
	
//	private final static Logger logger = LoggerFactory.getLogger(DebugAgendaEventListener.class);
	
    public void activationCreated(ActivationCreatedEvent activationcreatedevent){
    	Activation activation = activationcreatedevent.getActivation();     	   
        String debugStr = "==>[ActivationCreated(" + activation.getPropagationContext().getPropagationNumber() + 
        				  "): rule=" + activation.getRule().getName() + ";]";
//    	logger.debug(debugStr);
    }

    public void activationCancelled(ActivationCancelledEvent activationcancelledevent){
    	Activation activation = activationcancelledevent.getActivation();     	   
        String debugStr = "==>[activationCancelled(" + activation.getPropagationContext().getPropagationNumber() + 
        				  "): rule=" + activation.getRule().getName() + ";]";
//    	logger.debug(debugStr);
    }

    public void beforeActivationFired(BeforeActivationFiredEvent beforeactivationfiredevent){
    	Activation activation = beforeactivationfiredevent.getActivation();     	   
        String debugStr = "==>[beforeActivationFired(" + activation.getPropagationContext().getPropagationNumber() + 
        				  "): rule=" + activation.getRule().getName() + ";]";
//    	logger.debug(debugStr);
    }

    public void afterActivationFired(AfterActivationFiredEvent afteractivationfiredevent){
    	Activation activation = afteractivationfiredevent.getActivation();     	   
        String debugStr = "==>[afterActivationFired(" + activation.getPropagationContext().getPropagationNumber() + 
        				  "): rule=" + activation.getRule().getName() + ";]";
//    	logger.debug(debugStr);
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent agendagrouppoppedevent){
    	AgendaGroup agendaGroup = agendagrouppoppedevent.getAgendaGroup();     	   
        String debugStr = "<==[AgendaGroupPopped(" + agendaGroup.getName() + "]";
//    	logger.debug(debugStr);
    }
    
    public void agendaGroupPushed(AgendaGroupPushedEvent agendagrouppushedevent){
    	AgendaGroup agendaGroup = agendagrouppushedevent.getAgendaGroup();
    	String debugStr = "<==[agendaGroupPushed(" + agendaGroup.getName() + "]";
//    	logger.debug(debugStr);
    }
}