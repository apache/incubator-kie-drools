/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import java.io.Serializable;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.AbstractFactHandleFactory;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.FactHandleFactory;

public class ReteooFactHandleFactory extends AbstractFactHandleFactory implements Serializable {

    private static final long serialVersionUID = 510l;

    public ReteooFactHandleFactory() {
        super();
    }

    public ReteooFactHandleFactory(long id,
                                   long counter) {
        super( id,
               counter );
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.FactHandleFactory#newFactHandle(long)
     */
    public InternalFactHandle newFactHandle( final long id,
                                             final Object object,
                                             final long recency,
                                             final ObjectTypeConf conf,
                                             final InternalWorkingMemory workingMemory,
                                             final WorkingMemoryEntryPoint wmEntryPoint ) {
        if ( conf != null && conf.isEvent() ) {
            TypeDeclaration type = conf.getTypeDeclaration();
            long timestamp;
            if ( type != null && type.getTimestampExtractor() != null ) {
                timestamp = type.getTimestampExtractor().getLongValue( workingMemory,
                                                                       object );
            } else {
                timestamp = workingMemory.getTimerService().getCurrentTime();
            }
            long duration = 0;
            if ( type != null && type.getDurationExtractor() != null ) {
                duration = type.getDurationExtractor().getLongValue( workingMemory,
                                                                     object );
            }
            return new EventFactHandle( id,
                                        object,
                                        recency,
                                        timestamp,
                                        duration,
                                        wmEntryPoint != null ? wmEntryPoint : workingMemory,
                                        conf != null && conf.isTrait() );
        } else {
            return new DefaultFactHandle( id,
                                          object,
                                          recency,
                                          wmEntryPoint != null ? wmEntryPoint : workingMemory,
                                          conf != null && conf.isTrait() );
        }
    }

    @Override
    public DefaultFactHandle createDefaultFactHandle(long id, Object initialFact, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        return new DefaultFactHandle(0, initialFact, 0, wmEntryPoint );
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.FactHandleFactory#newInstance()
     */
    public FactHandleFactory newInstance() {
        return new ReteooFactHandleFactory();
    }

    public FactHandleFactory newInstance(long id,
                                         long counter) {
        return new ReteooFactHandleFactory( id,
                                            counter );
    }

    public Class getFactHandleType() {
        return DefaultFactHandle.class;
    }
}
