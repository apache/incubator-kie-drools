package org.drools.command.vsm;

import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.vsm.ServiceManagerData;

public class ServiceManagerServerContext
    implements
    Context {
    private Context            context;
    private ServiceManagerData data;

    public ServiceManagerServerContext(Context context,
                                       ServiceManagerData data) {
        this.data = data;
    }

    public ServiceManagerData getServiceManagerData() {
        return this.data;
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
