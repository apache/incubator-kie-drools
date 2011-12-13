package org.drools.grid;

import java.util.HashMap;
import java.util.Map;

import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.command.impl.ContextImpl;

public class ContextManagerImpl
    implements
    ContextManager {
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
}
