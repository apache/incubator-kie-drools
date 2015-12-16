/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.audit;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.WorkingMemory;
import org.drools.core.audit.event.LogEvent;
import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkingMemoryConsoleLogger extends WorkingMemoryLogger {

    protected static final transient Logger logger = LoggerFactory.getLogger(WorkingMemoryConsoleLogger.class);

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    public WorkingMemoryConsoleLogger(WorkingMemory workingMemory) {
        super(workingMemory);
    }
    
    public WorkingMemoryConsoleLogger(KnowledgeRuntimeEventManager session) {
        super(session);
    }

    public void logEventCreated(LogEvent logEvent) {
        logger.info(logEvent.toString());
    }

}
