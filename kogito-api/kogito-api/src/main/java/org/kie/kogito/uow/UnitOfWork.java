/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.uow;

/**
 * Unit of Work allows to group related activities and operation
 * into single unit. It it can be then completed or aborted as one
 * making the execution consistent.
 * 
 * Depending on the implementation it can rely on some additional frameworks 
 * or capabilities to carry on with the execution semantics.
 *
 */
public interface UnitOfWork {

    /**
     * Initiates this unit of work if not already started. It is safe to call start 
     * multiple times unless the unit has already been completed or aborted.
     */
    void start(); 
    
    /**
     * Completes this unit of work ensuring all awaiting work is invoked.
     */
    void end();
    
    /**
     * Aborts this unit of work and ignores any awaiting work.
     */
    void abort();
    
    /**
     * Intercepts work that should be done as part of this unit of work.
     * @param work actual work to be invoked as part of this unit of work.
     */
    void intercept(WorkUnit work);
}
