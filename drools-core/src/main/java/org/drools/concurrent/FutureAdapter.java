/**
 * 
 */
package org.drools.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureAdapter implements Future {
    org.drools.concurrent.Future future; 
    
    public FutureAdapter(org.drools.concurrent.Future future) {
        this.future = future;
    }
    
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public Object get() throws InterruptedException,
                       ExecutionException {
        if ( this.future.exceptionThrown() ) {
            throw new ExecutionException( this.future.getException() );
        }
        return this.future.getObject();
    }

    public Object get(long timeout,
                      TimeUnit unit) throws InterruptedException,
                                    ExecutionException,
                                    TimeoutException {
        throw new UnsupportedOperationException();
    }

    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    public boolean isDone() {
        return this.future.isDone();
    }
}