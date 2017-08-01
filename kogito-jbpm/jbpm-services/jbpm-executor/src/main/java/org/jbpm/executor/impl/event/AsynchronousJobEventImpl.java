/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.executor.impl.event;

import org.jbpm.executor.AsynchronousJobEvent;
import org.kie.api.executor.RequestInfo;


public class AsynchronousJobEventImpl implements AsynchronousJobEvent {

    private RequestInfo job;
    private boolean failed;
    private Throwable exception;

    
    public AsynchronousJobEventImpl(RequestInfo job, Throwable exception) {
        super();
        this.job = job;        
        this.exception = exception;
        if (exception != null) {
            this.failed = true;
        }
    }

    @Override
    public RequestInfo getJob() {
        return job;
    }

    @Override
    public boolean failed() {
        return failed;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "AsynchronousJobEventImpl [job=" + job + ", failed=" + failed + ", exception=" + exception + "]";
    }

}
