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