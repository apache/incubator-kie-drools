package org.drools.command.vsm;

import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.vsm.ServiceManagerServer;

public class ServiceManagerServerContext
    implements
    Context {
    private Context              context;
    private ServiceManagerServer server;

    public ServiceManagerServerContext(Context context,
                                       ServiceManagerServer server) {
        this.server = server;
    }

    public ServiceManagerServer getServiceManager() {
        return server;
    }

    public ContextManager getContextManager() {
        return context.getContextManager();
    }

    public String getName() {
        return context.getName();
    }

    public Object get(String identifier) {
        return context.get( identifier );
    }

    public void set(String identifier,
                    Object value) {
        context.set( identifier,
                     value );
    }

}
