package org.drools.leaps;

/*
 * Copyright 2006 Alexander Bagerman
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

/**
 * @author Alexander Bagerman
 * 
 */
class HandleFactory
    implements
    FactHandleFactory {
    private static final long serialVersionUID = 8510623248591449450L;

    private long              counter;

    HandleFactory() {
        this.counter = 0L;
    }

    /**
     * fact handle with no object
     * 
     * @see org.drools.reteoo.FactHandleFactory
     */
    public final FactHandle newFactHandle() {
        return new FactHandleImpl( this.getNextId(),
                                   null );
    }

    /**
     * leaps handle 
     * 
     * @param object
     * @return leaps handle
     */
    public final FactHandle newFactHandle(Object object) {
        return new FactHandleImpl( this.getNextId(),
                                   object );
    }

    /**
     * 
     * @return incremented id
     */
    protected synchronized long getNextId() {
        return ++this.counter;
    }

    /**
     * it does not make sense in leaps context. so we generate fact handle as we
     * did with no counter supplied
     * 
     * @see org.drools.reteoo.FactHandleFactory
     */
    public final FactHandle newFactHandle(long newId) {
        return this.newFactHandle();
    }

    /**
     * does nothing in leaps context
     * 
     * @see org.drools.reteoo.FactHandleFactory
     */
    public final void increaseFactHandleRecency(FactHandle factHandle) {
        ;
    }

    /**
     * @see org.drools.reteoo.FactHandleFactory
     */
    public FactHandleFactory newInstance() {
        return new HandleFactory();
    }
}
