/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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