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

package org.drools.core.time;

import java.io.Serializable;
import java.util.Optional;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;

public interface JobContext extends Serializable {
    /**
     * This method should only be called by the scheduler
     */    
    void setJobHandle(JobHandle jobHandle);
    
    JobHandle getJobHandle();

    InternalWorkingMemory getWorkingMemory();

    default Optional<InternalKnowledgeRuntime> getInternalKnowledgeRuntime() {
        return getWorkingMemory() != null ? Optional.ofNullable(getWorkingMemory().getKnowledgeRuntime()) : Optional.empty();
    }

    default boolean isNew() {
        return false;
    }
}
