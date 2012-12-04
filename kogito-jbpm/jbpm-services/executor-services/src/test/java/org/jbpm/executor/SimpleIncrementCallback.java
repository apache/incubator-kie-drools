package org.jbpm.executor;

import java.util.concurrent.atomic.AtomicLong;
import javax.inject.Named;
import org.jbpm.executor.api.CommandCallback;
import org.jbpm.executor.api.CommandContext;
import org.jbpm.executor.api.ExecutionResults;

/**
 *
 * @author salaboy
 */
@Named(value = "SimpleIncrementCallback")
public class SimpleIncrementCallback implements CommandCallback {

    public void onCommandDone(CommandContext ctx, ExecutionResults results) {
        String businessKey = (String) ctx.getData("businessKey");
        System.out.println(" >>> Before Incrementing = " + ((AtomicLong) BasicExecutorBaseTest.cachedEntities.get(businessKey)).get());
        ((AtomicLong) BasicExecutorBaseTest.cachedEntities.get(businessKey)).incrementAndGet();
        System.out.println(" >>> After Incrementing = " + BasicExecutorBaseTest.cachedEntities.get(businessKey));

    }
}
