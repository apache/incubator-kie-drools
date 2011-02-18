/*
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

package org.drools.spi;

import java.io.Externalizable;

import org.drools.WorkingMemory;

/**
 * Used to provide a strategy for the StatelessSession global exportation, so that StatelessSessionResult can have accesso to
 * globals using during the execute(...) method that returned the StatelessSessionResult.
 *
 */
public interface GlobalExporter extends Externalizable {

    /**
     * This method is called internally by the StatelessSession, which will provide the WorkingMemory.
     * The returned GlobalResolver is used by the StatefulSessionResult
     * @param workingMemory
     * @return
     *       The GlobalResolver instance as used by the StatefulSessionResult
     */
    public GlobalResolver export(WorkingMemory workingMemory);
}
