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

package org.jbpm.task.service;

/**
 * Exception that is thrown when a <code>User</code> try to perform an <code>Operation</code> on a <code>Task</code>
 *
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 * @see org.jbpm.task.service.TaskServiceSession#taskOperation(Operation, long, String, String, ContentData, List)  
 */
public class PermissionDeniedException extends TaskException{
    public PermissionDeniedException(String message) {
        super(message);
    }
}
