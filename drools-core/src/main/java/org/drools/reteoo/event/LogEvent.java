package org.drools.reteoo.event;

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
 * An event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen </a>
 */
public class LogEvent {
	
	public static final int OBJECT_ASSERTED = 1;
	public static final int OBJECT_MODIFIED = 2;
	public static final int OBJECT_RETRACTED = 3;
	
	public static final int ACTIVATION_CREATED = 4;
	public static final int ACTIVATION_CANCELLED = 5; 
	public static final int BEFORE_ACTIVATION_FIRE = 6;
	public static final int AFTER_ACTIVATION_FIRE = 7;
	
	private int type;
	
	/**
	 * Creates a new log event.
	 * 
	 * @param type The type of the log event.  This can be OBJECT_ASSERTED,
	 * OBJECT_MODIFIED, OBJECT_RETRACTED, ACTIVATION_CREATED, ACTIVATION_CANCELLED,
	 * BEFORE_ACTIVATION_FIRE or AFTER_ACTIVATION_FIRE.
	 */
	public LogEvent(int type) {
		this.type = type;
	}
	
	/**
	 * Returns the type of the log event as defined in this class.
	 * 
	 * @return The type of the log event.
	 */
	public int getType() {
		return type;
	}
	
}
