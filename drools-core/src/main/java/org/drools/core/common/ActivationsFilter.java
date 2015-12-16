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

package org.drools.core.common;

import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;

/**
 * A filter interface for agenda activations
 */
public interface ActivationsFilter {

    /**
     * Returns true if a new activation should be created for the given propagation
     * or false otherwise
     * 
     * @param tuple
     * @param context
     * @param workingMemory
     * @param rtn
     * @return
     */
    boolean accept(Activation activation,
                   InternalWorkingMemory workingMemory,
                   TerminalNode rtn );

}
