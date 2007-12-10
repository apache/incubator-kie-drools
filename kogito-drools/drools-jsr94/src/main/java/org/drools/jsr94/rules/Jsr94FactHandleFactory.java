package org.drools.jsr94.rules;

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
import org.drools.common.InternalFactHandle;
import org.drools.spi.FactHandleFactory;
import org.drools.temporal.SessionClock;

/**
 * A factory for creating <code>Handle</code>s.
 * @author <a href="mailto:michael.frandsen@syngenio.de">michael frandsen </a>
 */
public final class Jsr94FactHandleFactory extends AbstractFactHandleFactory {


    private static final long serialVersionUID = 4964273923122006124L;

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
     */
    protected final InternalFactHandle newFactHandle(final long id,
                                                     final Object object,
                                                     final long recency,
                                                     final boolean isEvent,
                                                     final WorkingMemory workingMemory ) {
        if ( isEvent ) {
            SessionClock clock = ((TemporalSession) workingMemory).getSessionClock(); 
            return new Jsr94EventFactHandle( id,
                                             object,
                                             recency,
                                             clock.getCurrentTime(),
                                             0 ); // for now, we are only handling primitive events
        } else {
            return new Jsr94FactHandle( id,
                                        object,
                                        recency );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newInstance()
     */
    public FactHandleFactory newInstance() {
        return new Jsr94FactHandleFactory();
    }

    public Class getFactHandleType() {
        return Jsr94FactHandle.class;
    }
}
