/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.services.cdi.impl.producers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.event.DebugProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.internal.runtime.manager.EventListenerProducer;

@org.jbpm.runtime.manager.api.qualifiers.Process
public class CustomProcessEventListenerProducer implements EventListenerProducer<ProcessEventListener> {

	@Override
	public List<ProcessEventListener> getEventListeners(String identifier, Map<String, Object> params) {
		
		List<ProcessEventListener> processEventListeners = new ArrayList<ProcessEventListener>();
		processEventListeners.add(new DebugProcessEventListener());
		
		return processEventListeners;
	}

}
