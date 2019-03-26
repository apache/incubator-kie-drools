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

package org.jbpm.persistence;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.map.EnvironmentBuilder;
import org.jbpm.persistence.api.ProcessPersistenceContextManager;

public class ProcessStorageEnvironmentBuilder
    implements
    EnvironmentBuilder {

    private ProcessStorage storage;
    private MapBasedProcessPersistenceContext context;
    
    public ProcessStorageEnvironmentBuilder(ProcessStorage storage) {
        this.storage = storage;
        this.context = new MapBasedProcessPersistenceContext( storage );
    }

    public ProcessPersistenceContextManager getPersistenceContextManager() {
        return new MapProcessPersistenceContextManager( context ) ;
    }

    public TransactionManager getTransactionManager() {
        return new ManualProcessTransactionManager( context, storage );
    }

}
