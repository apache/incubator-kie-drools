package org.jbpm.task.service.responsehandlers;

import java.util.List;

import org.jbpm.task.service.TaskClientHandler.QueryGenericResponseHandler;

public class BlockingQueryGenericResponseHandler  extends AbstractBlockingResponseHandler implements QueryGenericResponseHandler {

    private static final int RESULTS_WAIT_TIME = 10000;

    private volatile List<?> results;

    public synchronized void execute(List<?> results) {
        this.results = results;
        setDone(true);
    }

    public List<?> getResults() {
        // note that this method doesn't need to be synced because if waitTillDone returns true,
        // it means results is available 
        boolean done = waitTillDone(RESULTS_WAIT_TIME);

        if (!done) {
            throw new RuntimeException("Timeout : unable to retrieve results");
        }

        return results;
    }
}
