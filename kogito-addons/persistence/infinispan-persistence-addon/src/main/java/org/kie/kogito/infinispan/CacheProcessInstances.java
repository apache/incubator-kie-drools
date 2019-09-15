/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.infinispan;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.process.impl.marshalling.ProcessInstanceMarshaller;

@SuppressWarnings({"rawtypes"})
public class CacheProcessInstances implements MutableProcessInstances {
        
    private final RemoteCache<String, byte[]> cache;
    private ProcessInstanceMarshaller marshaller;
    
    private org.kie.kogito.process.Process<?> process;
    
    public CacheProcessInstances(Process<?> process, RemoteCacheManager cacheManager, String templateName, String proto, MessageMarshaller<?>...marshallers) {
        this.process = process;    
        this.cache = cacheManager.administration().getOrCreateCache(process.id() + "_store", ignoreNullOrEmpty(templateName));
        
        this.marshaller = new ProcessInstanceMarshaller(new ProtoStreamObjectMarshallingStrategy(proto, marshallers));
    }

    
    @Override
    public Optional<? extends ProcessInstance> findById(String id) {
        byte[] data = cache.get(id);
        if (data == null) {
            return Optional.empty();
        }
        
        return (Optional<? extends ProcessInstance>) Optional.of(marshaller.unmarshallProcessInstance(data, process));
    }

    
    @Override
    public Collection<? extends ProcessInstance> values() {
        return (Collection<? extends ProcessInstance>) cache.values()
                .parallelStream()
                .map(data -> marshaller.unmarshallProcessInstance(data, process))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(String id, ProcessInstance instance) {
        if (isActive(instance)) {
            byte[] data = marshaller.marhsallProcessInstance(instance);
            
            cache.put(instance.id(), data);
            
            ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(() -> {
                byte[] reloaded = cache.get(id);
                if (reloaded != null) {
                    return ((AbstractProcessInstance<?>)marshaller.unmarshallProcessInstance(reloaded, process, (AbstractProcessInstance<?>) instance)).internalGetProcessInstance();                    
                }
                
                return null;
            });
        }
    }

    @Override
    public void remove(String id) {
        cache.remove(id);
    }

    protected String ignoreNullOrEmpty(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        return value;
    }
}
