/*
 * Copyright 2013 JBoss Inc
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
package org.jbpm.runtime.manager.impl.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.api.runtime.manager.Context;
import org.kie.internal.runtime.manager.Mapper;

/**
 * An in-memory implementation of the context to <code>KieSession</code> identifier mapping.
 * Used only when the <code>RuntimeManager</code> is used without persistence. 
 *
 */
public class InMemoryMapper implements Mapper {

    private Map<Object, Integer> mapping = new ConcurrentHashMap<Object, Integer>();
    
    
    @Override
    public void saveMapping(Context<?> context, Integer ksessionId, String ownerId) {
        this.mapping.put(context.getContextId(), ksessionId);
    }

    @Override
    public Integer findMapping(Context<?> context, String ownerId) {
        return this.mapping.get(context.getContextId());
    }

    @Override
    public void removeMapping(Context<?> context, String ownerId) {
        this.mapping.remove(context.getContextId());
    }

    @Override
    public Object findContextId(Integer ksessionId, String ownerId) {
        if (mapping.containsValue(ksessionId)) {
            for (Map.Entry<Object, Integer> entry : mapping.entrySet()) {
                if (entry.getValue() == ksessionId) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public boolean hasContext(Integer ksessionId) {
    	return mapping.containsValue(ksessionId);
    }
}
