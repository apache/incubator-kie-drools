/*
 * Copyright 2010 JBoss Inc
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
package org.drools.management;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * Simple POJO that encapsulates DroolsManagmentAgent to make it work with Spring Framework
 * @author Lucas Amador
 *
 */
public class DroolsManagementAgentBean {

	private DroolsManagementAgent instance;
	private List<KnowledgeBase> knowledgeBases;
	private List<StatefulKnowledgeSession> statefulKnowledgeSessions;

	public DroolsManagementAgentBean() {
	}

	public void start() {
		instance = DroolsManagementAgent.getInstance();
		if (knowledgeBases!= null) {
			for (KnowledgeBase kbase : knowledgeBases) {
				instance.registerKnowledgeBase((ReteooRuleBase) ((KnowledgeBaseImpl)kbase).getRuleBase());
			}
		}
		if (statefulKnowledgeSessions!=null) {
			for (StatefulKnowledgeSession ksession : statefulKnowledgeSessions) {
				instance.registerKnowledgeSession(((StatefulKnowledgeSessionImpl)ksession).getInternalWorkingMemory());
			}
		}
	}

	public void setKnowledgeBases(List<KnowledgeBase> knowledgeBases) {
		this.knowledgeBases = knowledgeBases;
	}

	public void setStatefulKnowledgeSessions(List<StatefulKnowledgeSession> statefulKnowledgeSessions) {
		this.statefulKnowledgeSessions = statefulKnowledgeSessions;
	}

}
