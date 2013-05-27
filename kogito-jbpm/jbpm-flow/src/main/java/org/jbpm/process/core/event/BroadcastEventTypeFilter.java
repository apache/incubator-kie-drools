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

package org.jbpm.process.core.event;


// OCRAM: how broadcast signalling shoudl work
public class BroadcastEventTypeFilter extends EventTypeFilter {
 
    private static final long serialVersionUID = 510L;

    protected String base = null;
    
    public void setType(String broadcastPrefix) { 
        this.base = broadcastPrefix;
        this.type = this.base;
    }
    
    public void setType(String broadcastPrefix, String specificSuffix) { 
        this.base = broadcastPrefix;
        this.type = this.base + specificSuffix;
    }
    
    public boolean acceptsEvent(String type, Object event) {
        if( type == null ) { // same as EventTypeFilter
            return false;
        }
		if (this.type != null ) {
            if( this.type.equals(type) ) { 
                return true;
            } else if( this.base != null ) { 
                // broadcast
		        if( type.startsWith(this.base) ) { 
		            return true;
		        }
		    } 
		}
		return false;
	}

}
