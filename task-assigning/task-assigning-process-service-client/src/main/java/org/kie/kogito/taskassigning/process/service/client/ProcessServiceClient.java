/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.process.service.client;

import java.io.Closeable;
import java.util.List;
import java.util.Set;

/**
 * Defines a set of convenient operations for manipulating tasks and processes executing as kogito services.
 */
public interface ProcessServiceClient extends Closeable {

    Set<String> getAvailablePhases(String processId, String processInstanceId, String taskId, String workItemId,
                                   String user, List<String> groups);

    void transitionTask(String processId, String processInstanceId, String taskId, String workItemId, String phase,
                        String user, List<String> groups);
}
