/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.executor;

/**
 * Executor's Command are dedicated to contain purely business logic that should be executed.
 * It should not have any reference to underlying process engine and should not be concerned
 * with any process runtime related logic such us completing work item, sending signals, etc.
 * <br>
 * Information that are taken from process will be delivered as part of data instance of
 * <code>CommandContext</code>. Depending on the execution context that data can vary but
 * in most of the cases following will be given:
 * <ul>
 *  <li></li>
 *  <li>businessKey - usually unique identifier of the caller</li>
 *  <li>callbacks - FQCN of the <code>CommandCollback</code> that shall be used on command completion</li>
 * </ul>
 * When executed as part of the process (work item handler) additional data can be expected:
 * <ul>
 *  <li>workItem - the actual work item that is being executed with all it's parameters</li>
 *  <li>processInstanceId - id of the process instance that triggered this work</li>
 *  <li>deploymentId - if given process instance is part of an active deployment</li>
 * </ul>
 * Important note about implementations is that it shall always be possible to be initialized with default constructor
 * as executor service is an async component so it will initialize the command on demand using reflection.
 * In case there is a heavy logic on initialization it should be placed in another service implementation that
 * can be looked up from within command.
 */
public interface Command {

    /**
     * Executed this command's logic.
     * @param ctx - contextual data given by the executor service
     * @return returns any results in case of successful execution
     * @throws Exception in case execution failed and shall be retried if possible
     */
    ExecutionResults execute(CommandContext ctx) throws Exception;
}
