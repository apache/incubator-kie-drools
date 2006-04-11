package org.drools.audit.event;

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

/**
 * An object event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * It contains the fact id and a String represention of the object
 * at the time the event was logged.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen </a>
 */
public class ObjectLogEvent extends LogEvent {

	private long factId;
	private String objectToString;
	
	/**
	 * Create a new activation log event.
	 * 
	 * @param type The type of event.  This can only be LogEvent.OBJECT_ASSERTED,
	 * LogEvent.OBJECT_MODIFIED or LogEvent.OBJECT_RETRACTED.
	 * @param factId The id of the fact
	 * @param objectToString A toString of the fact 
	 */
	public ObjectLogEvent(int type, long factId, String objectToString) {
		super(type);
		this.factId = factId;
		this.objectToString = objectToString;
	}
	
	/**
	 * Returns the fact id of the object this event is about.
	 * 
	 * @return the id of the fact
	 */
	public long getFactId() {
		return factId;
	}
	
	/**
	 * Returns a toString of the fact this event is about at the
	 * time the event was created.
	 * 
	 * @return the toString of the fact
	 */
	public String getObjectToString() {
		return objectToString;
	}
}
