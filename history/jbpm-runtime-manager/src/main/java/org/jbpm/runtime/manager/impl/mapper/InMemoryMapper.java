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
package org.jbpm.runtime.manager.impl.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.manager.Context;

/**
 * An in-memory implementation of the context to <code>KieSession</code> identifier mapping.
 * Used only when the <code>RuntimeManager</code> is used without persistence. 
 *
 */
public class InMemoryMapper extends InternalMapper {

    private Map<Object, Long> mapping = new ConcurrentHashMap<Object, Long>();
    
    
    @Override
    public void saveMapping(Context<?> context, Long ksessionId, String ownerId) {
        this.mapping.put(context.getContextId(), ksessionId);
    }

    @Override
    public Long findMapping(Context<?> context, String ownerId) {
        return this.mapping.get(context.getContextId());
    }

    @Override
    public void removeMapping(Context<?> context, String ownerId) {
        this.mapping.remove(context.getContextId());
    }

    @Override
    public Object findContextId(Long ksessionId, String ownerId) {
        if (mapping.containsValue(ksessionId)) {
            for (Map.Entry<Object, Long> entry : mapping.entrySet()) {
                if (entry.getValue().equals(ksessionId)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public boolean hasContext(Long ksessionId) {
    	return mapping.containsValue(ksessionId);
    }

    @Override
    public List<String> findContextIdForEvent(String eventType, String ownerId) {
        List<String> contextIds = new ArrayList<String>(); 
        
        if (mapping != null && !mapping.isEmpty()) {
            for (Object contextId : mapping.keySet()) {
                contextIds.add(contextId.toString());
            }
        }
        return contextIds;
    }
}
