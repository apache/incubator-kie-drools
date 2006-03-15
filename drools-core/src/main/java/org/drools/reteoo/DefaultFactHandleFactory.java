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

import org.drools.FactHandle;
import org.drools.spi.FactHandleFactory;

public class DefaultFactHandleFactory
    implements
    FactHandleFactory {
    /** The fact id. */
    private long id;

    /** The number of facts created - used for recency. */
    private long counter;

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newFactHandle()
     */
    public final FactHandle newFactHandle() {
        return newFactHandle( this.id++ );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
     */
    public final FactHandle newFactHandle(long id) {
        return new FactHandleImpl( id,
                                   this.counter++ );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#increaseFactHandleRecency(org.drools.FactHandle)
     */
    public final void increaseFactHandleRecency(FactHandle factHandle) {
        ((FactHandleImpl) factHandle).setRecency( ++this.counter );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newInstance()
     */
    public FactHandleFactory newInstance() {
        return new DefaultFactHandleFactory();
    }
}
