/**
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

package org.drools.workflow.core.node;

import java.util.HashMap;
import java.util.Map;

import org.drools.process.core.timer.Timer;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.impl.ExtendedNodeImpl;

public class StateBasedNode extends ExtendedNodeImpl {

    private static final long serialVersionUID = 400L;

	private Map<Timer, DroolsAction> timers;
	
	public Map<Timer, DroolsAction> getTimers() {
		return timers;
	}
	
	public void addTimer(Timer timer, DroolsAction action) {
		if (timers == null) {
			timers = new HashMap<Timer, DroolsAction>();
		}
		if (timer.getId() == 0) {
			long id = 0;
	        for (Timer t: timers.keySet()) {
	            if (t.getId() > id) {
	                id = t.getId();
	            }
	        }
	        timer.setId(++id);
		}
		timers.put(timer, action);
	}
	
	public void removeAllTimers() {
		if (timers != null) {
			timers.clear();
		}
	}
	
}