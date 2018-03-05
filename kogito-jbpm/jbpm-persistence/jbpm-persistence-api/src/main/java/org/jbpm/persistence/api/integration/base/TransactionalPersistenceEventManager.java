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
package org.jbpm.persistence.api.integration.base;

import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerFactory;
import org.drools.persistence.api.TransactionSynchronization;
import org.jbpm.persistence.api.integration.EventCollection;
import org.jbpm.persistence.api.integration.EventEmitter;
import org.jbpm.persistence.api.integration.InstanceView;
import org.jbpm.persistence.api.integration.PersistenceEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of <code>PersistenceEventManager</code> that binds into transaction
 * to secure delivery of events to be consistent with persistence layer.
 *
 */
public class TransactionalPersistenceEventManager implements PersistenceEventManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalPersistenceEventManager.class);
    private static final String EVENT_COLLECTION = "org.jbpm.integration.events";
    
    private TransactionManager tm;
    private EventEmitter emitter;
    
    
    public TransactionalPersistenceEventManager() {
        this.tm = TransactionManagerFactory.get().newTransactionManager();
        
        ServiceLoader<EventEmitter> found = ServiceLoader.load(EventEmitter.class);
        Iterator<EventEmitter> it = found.iterator();
        if (it.hasNext()) {
            emitter = it.next();
            logger.debug("EventEmitter {} was found and is going to be used", emitter);
        }
    }
    
    @Override
    public void create(InstanceView<?> item) {
        if (!isActive()) {
            return;
        }
        
        EventCollection collection = getCollection(); 
        collection.add(item);
    }
    
    @Override
    public void update(InstanceView<?> item) {
        if (!isActive()) {
            return;
        }
        EventCollection collection = getCollection();
        collection.update(item);
    }
    
    @Override
    public void delete(InstanceView<?> item) {
        if (!isActive()) {
            return;
        }
        EventCollection collection = getCollection();
        collection.remove(item);
    }
    
    public boolean isActive() {
        return emitter != null;
    }
    
    protected EventCollection getCollection() {
        EventCollection collection = (EventCollection) this.tm.getResource(EVENT_COLLECTION);
        if (collection == null) {
            collection = emitter.newCollection();
            this.tm.putResource(EVENT_COLLECTION, collection);
            registerSync();
        }
        
        return collection;
    }
    
    protected void registerSync() {
        
        try {
            this.tm.registerTransactionSynchronization(new TransactionSynchronization() {
                
                private Collection<InstanceView<?>> events;
                @Override
                public void beforeCompletion() {
                    EventCollection collection = (EventCollection) tm.getResource(EVENT_COLLECTION);
                    logger.debug("About to deliver {} to emitter {}", collection, emitter);
                    Collection<InstanceView<?>> data = collection.getEvents();
                    
                    this.events = data.stream()
                            .map(event -> {
                                event.copyFromSource();
                                return event;
                                })
                            .collect(Collectors.toSet());
                    
                    emitter.deliver(events);
                    logger.debug("Collection {} delivered to {}", collection, emitter);
                   
                }
                
                @Override
                public void afterCompletion(int status) {
                    if ( status == TransactionManager.STATUS_COMMITTED ) {
                        logger.debug("Completed successfull so applying events on emitter {}", emitter);
                        emitter.apply(events);
                        logger.debug("Emitter {} successfully applied events", emitter);
                    } else {
                        logger.debug("Failed at completion so dropping events on emitter {}", emitter);
                        emitter.drop(events);
                        logger.debug("Emitter {} successfully dropped events", emitter);
                    }
                }
            });
        } catch (Exception e) {
            logger.warn("Unable to register transaction synchronization for event handling due to {}", e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (!isActive()) {
            return;
        }
        
        this.emitter.close();
    }
}
