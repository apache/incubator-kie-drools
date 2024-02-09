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
package org.drools.core.reteoo;

import java.io.Serializable;

import org.drools.base.reteoo.InitialFactImpl;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.AbstractFactHandleFactory;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.rule.accessor.FactHandleFactory;

public class ReteooFactHandleFactory extends AbstractFactHandleFactory implements Serializable {

    private static final long serialVersionUID = 510l;

    public ReteooFactHandleFactory() {
        super();
    }

    public ReteooFactHandleFactory(long id, long counter) {
        super( id, counter );
    }

    @Override
    public DefaultFactHandle newInitialFactHandle(WorkingMemoryEntryPoint wmEntryPoint) {
        return new DefaultFactHandle(0, InitialFactImpl.getInstance(), 0, wmEntryPoint);
    }

    public FactHandleFactory newInstance() {
        return new ReteooFactHandleFactory();
    }

    public FactHandleFactory newInstance(long id, long counter) {
        return new ReteooFactHandleFactory( id, counter );
    }

    public Class getFactHandleType() {
        return DefaultFactHandle.class;
    }
}
