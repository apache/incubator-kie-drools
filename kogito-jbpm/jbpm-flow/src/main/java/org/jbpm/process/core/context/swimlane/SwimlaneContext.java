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

package org.jbpm.process.core.context.swimlane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.Context;
import org.jbpm.process.core.context.AbstractContext;

public class SwimlaneContext extends AbstractContext {
	
    private static final long serialVersionUID = 510l;

    public static final String SWIMLANE_SCOPE = "SwimlaneScope";
    
	private Map<String, Swimlane> swimlanes = new HashMap<String, Swimlane>();

	public String getType() {
		return SWIMLANE_SCOPE;
	}

    public void addSwimlane(Swimlane swimlane) {
        this.swimlanes.put(swimlane.getName(), swimlane);
    }
    
    public Swimlane getSwimlane(String name) {
        return this.swimlanes.get(name);
    }

    public void removeSwimlane(String name) {
        this.swimlanes.remove(name);
    }
    
    public Collection<Swimlane> getSwimlanes() {
        return new ArrayList<Swimlane>(swimlanes.values());
    }
    
    public void setSwimlanes(Collection<Swimlane> swimlanes) {
        this.swimlanes.clear();
        for (Swimlane swimlane: swimlanes) {
            addSwimlane(swimlane);
        }
    }

	public Context resolveContext(Object param) {
		if (param instanceof String) {
            return getSwimlane((String) param) == null ? null : this;
        }
        throw new IllegalArgumentException(
            "Swimlanes can only resolve swimlane names: " + param);
    }

}
