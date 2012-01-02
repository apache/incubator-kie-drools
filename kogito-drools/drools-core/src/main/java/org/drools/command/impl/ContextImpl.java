/*
 * Copyright 2010 JBoss Inc
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

package org.drools.command.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.command.Context;
import org.drools.command.World;

public class ContextImpl
    implements
    Context {

    private World               manager;

    private String              name;

    private Map<String, Object> context = new ConcurrentHashMap<String, Object>();

    private Context             parent;

    public ContextImpl(String name,
                       World manager) {
        this.name = name;
        this.manager = manager;
    }

    public ContextImpl(String name,
                       World manager,
                       Context delegate) {
        this.name = name;
        this.manager = manager;
        setParent( delegate );
    }

    public void setParent(Context delegate) {
        this.parent = delegate;
    }

    public World getContextManager() {
        return this.manager;
    }

    public String getName() {
        return this.name;
    }

    public Object get(String identifier) {
        Object object = context.get( identifier );
        if ( object == null && parent != null ) {
            object = this.parent.get( identifier );
        }
        return object;
    }

    public void set(String name,
                    Object object) {
        context.put( name,
                     object );
    }

    public void remove(String name) {
        context.remove( name );
    }

    @Override
    public String toString() {
        return "ContextImpl [name=" + name + ", parent=" + parent.getName() + ", context=" + context + "]";
    }

}
