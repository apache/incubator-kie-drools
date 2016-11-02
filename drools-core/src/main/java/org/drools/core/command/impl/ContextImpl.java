/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.command.impl;

import org.drools.core.world.impl.ContextManagerImpl;
import org.kie.internal.command.Context;
import org.kie.internal.command.ContextManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ContextImpl implements RegistryContext {

    public static final String        REGISTRY = "__REGISTRY__";

    private final Map<String, Object> map = new ConcurrentHashMap<String, Object>();

    private final ContextManager      manager;

    private final String              name;

    private final Context             delegate;

    public ContextImpl() {
        this( UUID.randomUUID().toString(), new ContextManagerImpl() );
    }

    public ContextImpl(String name,
                       ContextManager manager) {
        this( name, manager, null );
    }

    public ContextImpl(String name,
                       ContextManager manager,
                       Context delegate) {
        this.name = name;
        this.manager = manager;
        this.delegate = delegate;
        set(REGISTRY, new HashMap<String, Object>() );
    }

    public Object get(String identifier) {
        if(identifier == null || identifier.equals("")){
            return null;
        }

        Object object = null;
        if ( map.containsKey(identifier) ) {
            object = map.get( identifier );
        } else if ( delegate != null ) {
            object = delegate.get( identifier );
        }

        return object;
    }

    @Override
    public void set(String identifier, Object value) {
        map.put( identifier, value );
    }

    @Override
    public void remove(String identifier) {
        map.remove( identifier );
    }

    public boolean has(String identifier) {
        return map.containsKey( identifier );
    }

    public ContextManager getContextManager() {
        return this.manager;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "ContextImpl{" +
               "name='" + name + '\'' +
               '}';
    }

    @Override
    public <T> ContextImpl register( Class<T> clazz, T instance ) {
        ((Map<String, Object>)get(ContextImpl.REGISTRY)).put( clazz.getName(), instance );
        return this;
    }

    @Override
    public <T> T lookup( Class<T> clazz ) {
        return (T) ((Map<String, Object>)get(ContextImpl.REGISTRY)).get( clazz.getName() );
    }
}
