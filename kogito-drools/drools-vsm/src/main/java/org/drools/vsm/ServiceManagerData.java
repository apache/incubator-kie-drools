package org.drools.vsm;

import java.util.concurrent.atomic.AtomicInteger;

import org.drools.command.Context;
import org.drools.command.ContextManager;
import org.drools.command.impl.ContextImpl;

public class ServiceManagerData {
    private ContextManager contextManager;

    private Context        root;
    private Context        temp;

    public static String   ROOT                 = "ROOT";
    public static String   TEMP                 = "__TEMP__";
    public static String   SERVICE_MANAGER_DATA = "__ServiceManagerData__";

    private AtomicInteger  sessionIdCounter     = new AtomicInteger();

    public ServiceManagerData() {
        // Setup ROOT context, this will hold all long lived intances and instanceIds
        this.contextManager = new ContextManagerImpl();

        this.root = new ContextImpl( ROOT,
                                     this.contextManager );
        ((ContextManagerImpl) this.contextManager).addContext( this.root );
        this.root.set( SERVICE_MANAGER_DATA,
                       this );
        // Setup TEMP context, this will hold all short lived instanceId and instances
        // TODO: TEMP context should have a time/utilisation eviction queue added 
        this.temp = new ContextImpl( TEMP,
                                     this.contextManager,
                                     this.root );
        ((ContextManagerImpl) this.contextManager).addContext( this.temp );
    }

    public AtomicInteger getSessionIdCounter() {
        return sessionIdCounter;
    }

    public ContextManager getContextManager() {
        return contextManager;
    }

    public void setContextManager(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    public Context getRoot() {
        return root;
    }

    public void setRoot(Context root) {
        this.root = root;
    }

    public Context getTemp() {
        return temp;
    }

    public void setTemp(Context temp) {
        this.temp = temp;
    }

}
