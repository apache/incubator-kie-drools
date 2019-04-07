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
package org.kie.internal.task.api;

import java.util.List;

import org.kie.internal.task.api.model.TaskDef;

/**
 * Experimental:
 *  The Task Definition Service is intended to keep
 *   meta information about a Task. This meta information
 *   can be used as a Task Template, to reuse the same
 *   template in different places without redefining the
 *   Task Structure
 */
public interface TaskDefService {

    public void deployTaskDef(TaskDef def);

    public List<TaskDef> getAllTaskDef(String filter);

    public TaskDef getTaskDefById(String id);

    public void undeployTaskDef(String id);
}
