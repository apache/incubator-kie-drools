/*
 * Copyright 2013 JBoss Inc
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
 * CommandCallback represents logic that shall be executed after command invocation.
 * Depending on the result of invocation either <code>onCommandDone</code> or <code>onCommandError</code> will be called.
 * <br>
 * Important note about implementations is that it shall always be possible to be initialized with default constructor
 * as executor service is an async component so it will initialize the command on demand using reflection.
 * In case there is a heavy logic on initialization it should be placed in another service implementation that 
 * can be looked up from within command.
 */
public interface CommandCallback {

    /**
     * Executed as soon as command is executed successfully.
     * @param ctx - contextual data given by the executor service
     * @param results - result produced by command
     */
    void onCommandDone(CommandContext ctx, ExecutionResults results);
    
    /**
     * Executed only when command failed and all possible retries were already invoked. This indicates that executor will not
     * attempt any more execution of given command as part of the request.
     * @param ctx - contextual data given by the executor service
     * @param exception - exception that was thrown on last attempt to execute command
     */
    void onCommandError(CommandContext ctx, Throwable exception);
}
