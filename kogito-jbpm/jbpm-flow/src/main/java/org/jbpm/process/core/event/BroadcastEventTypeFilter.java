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

package org.jbpm.process.core.event;


/**
 * This variant of the {@link EventTypeFilter} can be used with structures such 
 * as Escalations, for which Intermediate (Catching) Events can be triggered
 * by both 
 * 
 *
 */
public class BroadcastEventTypeFilter extends EventTypeFilter {

	private static final long serialVersionUID = 510l;
	
	public boolean acceptsEvent(String type, Object event) {
	    if( type == null ) { 
	        return false;
	    }
	    boolean accepts = false;
		if (this.type != null ) { 
		    if( this.type.equals(type)) {
		        accepts = true;
		    } else if( type != null && type.startsWith(this.type) ) { 
		        accepts = true;
		    } 
		} 
		return accepts;
	}

	public String toString() { 
	    return "Broadcast Event filter: [" + this.type + "]";
	}
}
