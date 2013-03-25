package org.jbpm.runtime.manager.impl.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.internal.runtime.manager.Context;
import org.kie.internal.runtime.manager.Mapper;

public class InMemoryMapper implements Mapper {

    private Map<Object, Integer> mapping = new ConcurrentHashMap<Object, Integer>();
    
    
    @Override
    public void saveMapping(Context context, Integer ksessionId) {
        this.mapping.put(context.getContextId(), ksessionId);
    }

    @Override
    public Integer findMapping(Context context) {
        return this.mapping.get(context.getContextId());
    }

    @Override
    public void removeMapping(Context context) {
        this.mapping.remove(context.getContextId());
    }

    @Override
    public Object findContextId(Integer ksessionId) {
        if (mapping.containsValue(ksessionId)) {
            for (Map.Entry<Object, Integer> entry : mapping.entrySet()) {
                if (entry.getValue() == ksessionId) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

}
