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
package org.jbpm.workflow.core.node;

import org.jbpm.process.core.event.EventFilter;
import java.util.function.Function;

public class BoundaryEventNode extends EventNode {

    private static final long serialVersionUID = 3448981074702415561L;
    
    private String attachedToNodeId;

    public String getAttachedToNodeId() {
        return attachedToNodeId;
    }

    public void setAttachedToNodeId(String attachedToNodeId) {
        this.attachedToNodeId = attachedToNodeId;
    }

    @Override
    public boolean acceptsEvent(String type, Object event, Function<String, String> resolver) {
        if (resolver == null) {
            return acceptsEvent(type, event);
        }

        for( EventFilter filter : getEventFilters() ) {
            if( filter.acceptsEvent(type, event, resolver) ) {
                return true;
            }
        }
        return super.acceptsEvent(type, event);
    }
}
