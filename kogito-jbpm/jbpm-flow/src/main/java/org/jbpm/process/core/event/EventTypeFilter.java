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

import java.io.Serializable;
import java.util.function.Function;

public class EventTypeFilter implements EventFilter, Serializable {

	private static final long serialVersionUID = 510l;
	
	protected String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean acceptsEvent(String type, Object event) {
		if (this.type != null && this.type.equals(type)) {
			return true;
		}
		return false;
	}

	public String toString() { 
	    return "Event filter: [" + this.type + "]";
	}

    @Override
    public boolean acceptsEvent(String type, Object event, Function<String, String> resolver) {
        if (this.type != null && resolver.apply(this.type).equals(type)) {
            return true;
        }
        return false;
    }
}
