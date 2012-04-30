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

package org.jbpm.task.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.eventmessaging.EventTriggerTransport;
import org.jbpm.eventmessaging.Payload;

public class EventTransport implements EventTriggerTransport {

	private String uuid;
	private Map<String, SessionWriter> sessions;
	private int responseId;
	private boolean remove;

	public EventTransport(String uuid, int responseId, Map<String, SessionWriter> sessions, boolean remove) {
		this.uuid = uuid;
		this.responseId = responseId;
		this.sessions = sessions;
		this.remove = remove;
	}

	public void trigger(Payload payload) {        
		SessionWriter session = sessions.get( uuid );
		List<Object> args = new ArrayList<Object>( 1 );
		args.add(payload);
		Command cmd = new Command(responseId, CommandName.EventTriggerResponse, args);             
		try {
			session.write(cmd);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}       
	}

	public boolean isRemove() {
		return this.remove;
	}

}
