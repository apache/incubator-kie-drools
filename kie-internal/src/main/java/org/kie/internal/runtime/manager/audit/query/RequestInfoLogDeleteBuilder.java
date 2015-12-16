/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.executor.STATUS;

public interface RequestInfoLogDeleteBuilder extends AuditDateDeleteBuilder<RequestInfoLogDeleteBuilder>{

    /**
     * Specify one or more deployment ids to use as a criteria.
     * @param deploymentId one or more string deployment ids
     * @return The current query builder instance
     */
    public RequestInfoLogDeleteBuilder deploymentId(String... deploymentId);
    
    /**
     * Specify one more statuses (in the form of an int) as criteria.
     * @param status one or more int statuses
     * @return The current instance of this query builder
     */
    public RequestInfoLogDeleteBuilder status(STATUS... status);
    
}
