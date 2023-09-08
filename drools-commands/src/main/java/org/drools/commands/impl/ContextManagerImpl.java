/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.command.Command;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.Context;
import org.kie.internal.command.ContextManager;

public class ContextManagerImpl implements ContextManager, CommandExecutor {

    private Context root;
    private final Map<String, Context> contexts;

    public static String ROOT = "ROOT";

    public ContextManagerImpl() {
        this( new HashMap<>() );
    }

    public ContextManagerImpl( Map<String, Context> contexts ) {
        this.root = new ContextImpl( ROOT, this );

        this.root.set( "world", this );

        this.contexts = contexts;
        this.contexts.put( ROOT, this.root );
    }

    public <T> T execute( Command<T> command ) {
        return null;
    }

    public Context createContext( String identifier ) {
        Context ctx = this.contexts.get( identifier );
        if ( ctx == null ) {
            ctx = new ContextImpl( identifier, this, root );
            this.contexts.put( identifier, ctx );
        }
        return ctx;
    }

    public Context getContext( String identifier ) {
        return this.contexts.get( identifier );
    }

    public Context getRootContext() {
        return this.root;
    }

    public Object get( String identifier ) {
        return root.get( identifier );
    }

    public void set( String identifier, Object value ) {
        root.set( identifier, value );
    }

    public void remove( String identifier ) {
        root.remove( identifier );
    }
}