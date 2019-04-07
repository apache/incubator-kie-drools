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

package org.jbpm.process.instance.context.swimlane;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.core.context.swimlane.SwimlaneContext;
import org.jbpm.process.instance.context.AbstractContextInstance;

public class SwimlaneContextInstance extends AbstractContextInstance {

    private static final long serialVersionUID = 510l;
    
    private Map<String, String> swimlaneActors = new HashMap<String, String>();

    public String getContextType() {
        return SwimlaneContext.SWIMLANE_SCOPE;
    }
    
    public SwimlaneContext getSwimlaneContext() {
        return (SwimlaneContext) getContext();
    }

    public String getActorId(String swimlane) {
        return swimlaneActors.get(swimlane);
    }

    public void setActorId(String swimlane, String actorId) {
        swimlaneActors.put(swimlane, actorId);
    }
    
    public Map<String, String> getSwimlaneActors() {
    	return swimlaneActors;
    }
    
}
