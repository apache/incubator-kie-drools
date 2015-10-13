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

package org.drools.core.base;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;

public class StandardQueryViewChangedEventListener
    extends AbstractQueryViewListener {

    public InternalFactHandle getHandle(InternalFactHandle originalHandle) {
        // can be null for eval, not and exists that have no right input
        return new DefaultFactHandle( originalHandle.getId(),
                                      ( originalHandle.getEntryPoint() != null ) ?  originalHandle.getEntryPoint().getEntryPointId() : null,
                                      originalHandle.getIdentityHashCode(),
                                      originalHandle.getObjectHashCode(),
                                      originalHandle.getRecency(),
                                      originalHandle.getObject() );
    }

}
