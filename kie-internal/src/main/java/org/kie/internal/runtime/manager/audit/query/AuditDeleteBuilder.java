/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.runtime.manager.audit.query;




/**
 * This interface defines methods that are used by all of the Audit implementations.
 *
 */
public interface AuditDeleteBuilder<T> {

    /**
     * Specify one or more process instance ids as criteria in the query
     * @param processInstanceId one or more a process instance ids
     * @return The current query builder instance
     */
    public T processInstanceId(long... processInstanceId);
    
    /**
     * Specify one or more process (definition) id's as criteria in the query
     * @param processId one or more process ids
     * @return The current query builder instance
     */
    public T processId(String... processId);
    
}
