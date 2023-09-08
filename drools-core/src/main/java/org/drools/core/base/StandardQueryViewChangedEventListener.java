/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.base;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.kie.api.runtime.rule.FactHandle;

public class StandardQueryViewChangedEventListener
    extends AbstractQueryViewListener {

    public FactHandle getHandle(FactHandle originalHandle) {
        InternalFactHandle fh = (InternalFactHandle) originalHandle;
        // can be null for eval, not and exists that have no right input
        return new DefaultFactHandle( fh.getId(),
                                      fh.getEntryPointId() != null ? fh.getEntryPointId().getEntryPointId() : null,
                                      fh.getIdentityHashCode(),
                                      fh.getObjectHashCode(),
                                      fh.getRecency(),
                                      fh.getObject() );
    }

}
