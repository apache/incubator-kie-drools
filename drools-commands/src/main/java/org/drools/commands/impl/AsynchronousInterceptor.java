package org.drools.commands.impl;

import org.kie.api.runtime.Executable;
import org.kie.api.runtime.RequestContext;


public class AsynchronousInterceptor extends AbstractInterceptor {

    public RequestContext execute( Executable executable, RequestContext ctx ) {
        new Thread(new Runnable() {
            public void run() {
                executeNext(executable, ctx);
            }
        }).start();
        return ctx;
    }

}
