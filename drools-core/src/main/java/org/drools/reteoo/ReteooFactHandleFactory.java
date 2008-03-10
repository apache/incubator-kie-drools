package org.drools.reteoo;

/*
 * Copyright 2005 JBoss Inc
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

import org.drools.TemporalSession;
import org.drools.WorkingMemory;
import org.drools.common.AbstractFactHandleFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.spi.FactHandleFactory;
import org.drools.temporal.SessionClock;

public class ReteooFactHandleFactory extends AbstractFactHandleFactory {

    private static final long serialVersionUID = 400L;
    
    public ReteooFactHandleFactory() {
        super();
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
     */
    protected final InternalFactHandle newFactHandle(final int id,
                                                     final Object object,
                                                     final long recency,
                                                     final boolean isEvent,
                                                     final WorkingMemory workingMemory ) {
        if ( isEvent ) {
            SessionClock clock = ((TemporalSession) workingMemory).getSessionClock(); 
            return new EventFactHandle( id,
                                        object,
                                        recency,
                                        clock.getCurrentTime(),
                                        0 );  // primitive events have 0 duration
        } else {
            return new DefaultFactHandle( id,
                                          object,
                                          recency );
        }
    }
    
    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newInstance()
     */
    public FactHandleFactory newInstance() {
        return new ReteooFactHandleFactory();
    }

    public Class getFactHandleType() {
        return DefaultFactHandle.class;
    }
}
