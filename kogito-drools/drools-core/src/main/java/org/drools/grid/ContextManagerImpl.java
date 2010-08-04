/**
 * 
 */
package org.drools.grid;

import java.util.HashMap;
import java.util.Map;

import org.drools.command.Context;
import org.drools.command.ContextManager;

public class ContextManagerImpl
    implements
    ContextManager {
    private Map<String, Context> contexts;
    private Context              defaultContext;

    public ContextManagerImpl() {
        this.contexts = new HashMap<String, Context>();
    }

    public synchronized void addContext(Context context) {
        if ( this.contexts.isEmpty() ) {
            this.defaultContext = context;
        }
        this.contexts.put( context.getName(),
                           context );
    }

    public synchronized Context getContext(String identifier) {
        return this.contexts.get( identifier );
    }

    public Context getDefaultContext() {
        return this.defaultContext;
    }
}