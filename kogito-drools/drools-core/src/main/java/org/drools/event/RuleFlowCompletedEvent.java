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

import org.drools.ruleflow.instance.RuleFlowProcessInstance;

/**
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleFlowCompletedEvent extends RuleFlowEvent {

	private static final long serialVersionUID = 7872024884293495713L;

	public RuleFlowCompletedEvent(final RuleFlowProcessInstance instance) {
        super( instance );
    }

    public String toString() {
        return "==>[RuleFlowCompleted(name=" + getRuleFlowProcessInstance().getRuleFlowProcess().getName() + "; id=" + getRuleFlowProcessInstance().getRuleFlowProcess().getId() + ")]";
    }
}