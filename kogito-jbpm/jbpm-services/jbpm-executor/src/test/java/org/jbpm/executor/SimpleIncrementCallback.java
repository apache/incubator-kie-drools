package org.jbpm.executor;

import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Named;

import org.jbpm.executor.api.CommandCallback;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author salaboy
 */
@Named(value = "SimpleIncrementCallback")
public class SimpleIncrementCallback implements CommandCallback {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleIncrementCallback.class);

    public void onCommandDone(CommandContext ctx, ExecutionResults results) {
        String businessKey = (String) ctx.getData("businessKey");
        logger.info("Before Incrementing = {}", ((AtomicLong) BasicExecutorBaseTest.cachedEntities.get(businessKey)).get());
        ((AtomicLong) BasicExecutorBaseTest.cachedEntities.get(businessKey)).incrementAndGet();
        logger.info("After Incrementing = {}", BasicExecutorBaseTest.cachedEntities.get(businessKey));

    }
}
