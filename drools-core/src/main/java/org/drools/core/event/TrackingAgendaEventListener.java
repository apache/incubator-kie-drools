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
package org.drools.core.event;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

public class TrackingAgendaEventListener implements AgendaEventListener {
	
	private List<String> matchCreated = new ArrayList<>();
	private List<String> matchCancelled = new ArrayList<>();
	private List<String> beforeMatchFired = new ArrayList<>();
	private List<String> afterMatchFired = new ArrayList<>();
	private List<String> agendaGroupPopped = new ArrayList<>();
	private List<String> agendaGroupPushed = new ArrayList<>();
	private List<String> beforeRuleFlowGroupActivated = new ArrayList<>();
	private List<String> afterRuleFlowGroupActivated = new ArrayList<>();
	private List<String> beforeRuleFlowGroupDeactivated = new ArrayList<>();
	private List<String> afterRuleFlowGroupDeactivated = new ArrayList<>();
	
	public TrackingAgendaEventListener() {
        // intentionally left blank
    }
	
    public List<String> getMatchCreated() {
		return matchCreated;
	}

	public List<String> getMatchCancelled() {
		return matchCancelled;
	}

	public List<String> getBeforeMatchFired() {
		return beforeMatchFired;
	}

	public List<String> getAfterMatchFired() {
		return afterMatchFired;
	}

	public List<String> getAgendaGroupPopped() {
		return agendaGroupPopped;
	}

	public List<String> getAgendaGroupPushed() {
		return agendaGroupPushed;
	}

	public List<String> getBeforeRuleFlowGroupActivated() {
		return beforeRuleFlowGroupActivated;
	}

	public List<String> getAfterRuleFlowGroupActivated() {
		return afterRuleFlowGroupActivated;
	}

	public List<String> getBeforeRuleFlowGroupDeactivated() {
		return beforeRuleFlowGroupDeactivated;
	}

	public List<String> getAfterRuleFlowGroupDeactivated() {
		return afterRuleFlowGroupDeactivated;
	}

    public void matchCreated(MatchCreatedEvent event) {
        matchCreated.add(event.getMatch().getRule().getName());
    }

    public void matchCancelled(MatchCancelledEvent event) {
    	matchCancelled.add(event.getMatch().getRule().getName());
    }

    public void beforeMatchFired(BeforeMatchFiredEvent event) {
    	beforeMatchFired.add(event.getMatch().getRule().getName());
    }

    public void afterMatchFired(AfterMatchFiredEvent event) {
    	afterMatchFired.add(event.getMatch().getRule().getName());
    }

    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {
    	agendaGroupPopped.add(event.getAgendaGroup().getName());
    }

    public void agendaGroupPushed(AgendaGroupPushedEvent event) {
    	agendaGroupPushed.add(event.getAgendaGroup().getName());
    }

    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    	beforeRuleFlowGroupActivated.add(event.getRuleFlowGroup().getName());
    }

    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {
    	afterRuleFlowGroupActivated.add(event.getRuleFlowGroup().getName());
    }

    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    	beforeRuleFlowGroupDeactivated.add(event.getRuleFlowGroup().getName());
    }

    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {
    	afterRuleFlowGroupDeactivated.add(event.getRuleFlowGroup().getName());
    }
    
    public void resetAllEvents() {
    	matchCreated = new ArrayList<>();
    	matchCancelled = new ArrayList<>();
    	beforeMatchFired = new ArrayList<>();
    	afterMatchFired = new ArrayList<>();
    	agendaGroupPopped = new ArrayList<>();
    	agendaGroupPushed = new ArrayList<>();
    	beforeRuleFlowGroupActivated = new ArrayList<>();
    	afterRuleFlowGroupActivated = new ArrayList<>();
    	beforeRuleFlowGroupDeactivated = new ArrayList<>();
    	afterRuleFlowGroupDeactivated = new ArrayList<>();    }
}
