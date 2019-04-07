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


public interface VariableInstanceLogDeleteBuilder extends AuditDateDeleteBuilder<VariableInstanceLogDeleteBuilder>{

    /**
     * Specify one or more external ids to use as a criteria. In some cases,
     * the external id is the deployment unit id or runtime manager id.
     * @param externalId one or more string external ids
     * @return The current query builder instance
     */
    public VariableInstanceLogDeleteBuilder externalId(String... externalId);

}
