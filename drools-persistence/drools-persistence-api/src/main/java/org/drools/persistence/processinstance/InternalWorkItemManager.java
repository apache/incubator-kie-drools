/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.persistence.processinstance;

import java.util.Set;

import org.drools.core.process.instance.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * Exposes the work items outside of the manager.
 */
public interface InternalWorkItemManager extends WorkItemManager {

	void clearWorkItems();

	Set<WorkItem> getWorkItems();
}
