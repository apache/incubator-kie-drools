package org.drools.grid;

import java.util.HashMap;
import java.util.Map;

import org.drools.command.Context;
import org.drools.command.World;
import org.drools.command.impl.ContextImpl;

public class ContextManagerImpl
    implements
    World {
    private Map<String, Context> contexts;


    private Context              root;

    public ContextManagerImpl() {
        this.contexts = new HashMap<String, Context>();
        
        this.root = new ContextImpl( ROOT,
                                     this );
        
        this.contexts.put( ROOT,
                           this.root );        
    }

    public synchronized void addContext(Context context) {
        this.contexts.put( context.getName(),
                           context );
    }

    public synchronized Context getContext(String identifier) {
        return this.contexts.get( identifier );
    }

    public Context getRootContext() {
        return this.root;
    }

    public World getContextManager() {
        return this;
    }

    public String getName() {
        return root.getName();
    }

    public Object get(String identifier) {
        return root.get( identifier );
    }

    public void set(String identifier,
                    Object value) {
        root.set( identifier, value );
    }

    public void remove(String identifier) {
        root.remove( identifier );
    }

    public Context createContext(String identifier) {
        // TODO Auto-generated method stub
        return null;
    }
}
