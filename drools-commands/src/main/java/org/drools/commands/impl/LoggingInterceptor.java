package org.drools.commands.impl;

import org.kie.api.runtime.Executable;
import org.kie.api.runtime.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoggingInterceptor extends AbstractInterceptor {

    protected static final transient Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    public RequestContext execute( Executable executable, RequestContext ctx ) {
        logger.info("Executing --> " + executable);
        executeNext(executable, ctx);
        logger.info("Done executing --> " + executable);
        return ctx;
    }
}
