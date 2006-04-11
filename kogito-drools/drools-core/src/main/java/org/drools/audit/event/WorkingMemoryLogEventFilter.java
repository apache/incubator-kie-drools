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
 * An event filter that can be used to filter working memory events.
 * By default, all events are allowed.  You can filter out any of the
 * three types of working memory events by setting the allow boolean
 * for that type to false.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen </a>
 */
public class WorkingMemoryLogEventFilter implements ILogEventFilter {

	private boolean allowAssertEvents = true;
	private boolean allowModifyEvents = true;
	private boolean allowRetractEvents = true;
	
	public WorkingMemoryLogEventFilter(boolean allowAssertEvents,
			boolean allowModifyEvents, boolean allowRetractEvents) {
		setAllowAssertEvents(allowAssertEvents);
		setAllowModifyEvents(allowModifyEvents);
		setAllowRetractEvents(allowRetractEvents);
	}
	
	/**
	 * @see org.drools.audit.event.ILogEventFilter
	 */
	public boolean acceptEvent(LogEvent event) {
		switch (event.getType()) {
			case LogEvent.OBJECT_ASSERTED: return allowAssertEvents;
			case LogEvent.OBJECT_MODIFIED: return allowModifyEvents;
			case LogEvent.OBJECT_RETRACTED: return allowRetractEvents;
			default: return true;
		}
	}
	
	public void setAllowAssertEvents(boolean allowAssertEvents) {
		this.allowAssertEvents = allowAssertEvents;
	}

	public void setAllowModifyEvents(boolean allowModifyEvents) {
		this.allowModifyEvents = allowModifyEvents;
	}

	public void setAllowRetractEvents(boolean allowRetractEvents) {
		this.allowRetractEvents = allowRetractEvents;
	}
}
