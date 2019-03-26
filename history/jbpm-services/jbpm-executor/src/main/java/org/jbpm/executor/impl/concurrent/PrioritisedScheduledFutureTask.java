/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.executor.impl.concurrent;


import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PrioritisedScheduledFutureTask<V> implements RunnableScheduledFuture<V> {

    private RunnableScheduledFuture<V> delegate;
    private Integer priority;
    private Date fireDate;
    
    public PrioritisedScheduledFutureTask(RunnableScheduledFuture<V> delegate, Integer priority, Date fireDate) {
        super();
        this.delegate = delegate;
        this.priority = priority;
        this.fireDate = fireDate;
    }

    public void run() {
        delegate.run();
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    public boolean isDone() {
        return delegate.isDone();
    }

    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }

    public long getDelay(TimeUnit unit) {
        
        return delegate.getDelay(unit);
    }

    public int compareTo(Delayed o) {
        
        
        if ( o instanceof PrioritisedScheduledFutureTask) {
            int result = fireDate.compareTo(((PrioritisedScheduledFutureTask<?>)o).fireDate);
            if (result == 0) {
                result = ((PrioritisedScheduledFutureTask<?>)o).priority.compareTo(priority);
            }
            
            return result;
        }
        return delegate.compareTo(o);
    }

    public boolean isPeriodic() {
        return delegate.isPeriodic();
    }

}
