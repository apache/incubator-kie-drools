/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.world.impl;

import org.drools.core.command.GetDefaultValue;
import org.drools.core.command.impl.ContextImpl;
import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.CommandExecutor;
import org.kie.internal.command.Context;
import org.kie.internal.command.ContextManager;

import java.util.HashMap;
import java.util.Map;

public class ContextManagerImpl
        implements ContextManager, GetDefaultValue, CommandExecutor {


    private Context root;
    private Map<String, Context> contexts;

    public static String ROOT = "ROOT";

    private CommandExecutionHandler executionHandler = new DefaultCommandExecutionHandler();

    private Object lastReturnValue;

    public ContextManagerImpl() {
        this( new HashMap<String, Context>() );
    }

    public ContextManagerImpl( Map<String, Context> contexts ) {
        this.root = new ContextImpl( ROOT,
                                     this );

        this.root.set( "world",
                       this );

        this.contexts = contexts;
        this.contexts.put( ROOT,
                           this.root );
    }

    public <T> T execute( Command<T> command ) {
        return null;
    }

    public void setCommandExecutionHandler( CommandExecutionHandler executionHandler ) {
        this.executionHandler = executionHandler;
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

    public Object getLastReturnValue() {
        return this.lastReturnValue;
    }

    public static interface CommandExecutionHandler {
        public Object execute( ExecutableCommand command,
                               Context context );
    }

    public static class DefaultCommandExecutionHandler
            implements
            CommandExecutionHandler {
        public Object execute( ExecutableCommand command,
                               Context context ) {
            return command.execute( context );
        }
    }

    public Object getObject() {
        return lastReturnValue;
    }

    public ContextManager getContextManager() {
        return this;
    }

    public String getName() {
        return root.getName();
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