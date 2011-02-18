/*
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

import java.io.Externalizable;

/**
 * This class instance is configed by the RuleBaseConfiguration and is responsible for thread management
 * of the async services.
 *
 */
public interface ExecutorService extends Externalizable {

    /**
     * The CommandExecutor is a producer/consumer style class that handles the queue and execution
     * of the async actions
     * @param executor
     */
    public void setCommandExecutor(CommandExecutor executor);

    /**
     * Submit a command for execution, adds it ot the commandExecutor's queue
     * @param command
     * @return
     */
    Future submit(Command command);

    /**
     * Shutdown this ExecutorService
     *
     */
    void shutDown();

    /**
     * Startup this ExecutorService, typically called on first submit for lazy startup.
     *
     */
    void startUp();
}
