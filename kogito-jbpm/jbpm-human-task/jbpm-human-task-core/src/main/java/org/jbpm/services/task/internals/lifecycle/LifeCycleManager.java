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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.internals.lifecycle;


import java.util.List;
import java.util.Map;

import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.model.Operation;
import org.kie.internal.task.exception.TaskException;

/**
 *
 */
public interface LifeCycleManager {
    public void taskOperation(final Operation operation, final long taskId, final String userId,
                              final String targetEntityId, final Map<String, Object> data,
                              List<String> groupIds, OrganizationalEntity...entities) throws TaskException;
}
