package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * This is what the remote service will implement, as a servlet.
 * (in hosted/debug mode, you could also use an implementation that was in-process).
 */
public interface RepositoryService extends RemoteService {

    public String myMethod(String blah);
    
}
