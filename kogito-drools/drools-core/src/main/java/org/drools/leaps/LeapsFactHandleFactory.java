package org.drools.leaps;

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

import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.spi.FactHandleFactory;

/**
 * @author Alexander Bagerman
 * 
 */
class LeapsFactHandleFactory
    implements
    FactHandleFactory {
    private static final long serialVersionUID = 8510623248591449450L;

    private long              counter;

    protected LeapsFactHandleFactory() {
        this.counter = 0L;
    }

    /**
     * it does not make sense in leaps context. so we generate fact handle as we
     * did with no counter supplied
     * 
     * @see org.drools.reteoo.FactHandleFactory
     */
    public final InternalFactHandle newFactHandle(final Object object) {
        return new FactHandleImpl( this.getNextId(),
                                   object );
    }

    /**
     * leaps handle 
     * 
     * @param object
     * @return leaps handle
     */
    public final InternalFactHandle newFactHandle(final long newId,
                                                  final Object object) {
        return newFactHandle( object );
    }

    /**
     * 
     * @return incremented id
     */
    protected synchronized long getNextId() {
        return ++this.counter;
    }

    /**
     * does nothing in leaps context
     * 
     * @see org.drools.reteoo.FactHandleFactory
     */
    public final void increaseFactHandleRecency(final InternalFactHandle factHandle) {
        ;
    }

    public void destroyFactHandle(final InternalFactHandle factHandle) {
        factHandle.invalidate();
    }

    /**
     * @see org.drools.reteoo.FactHandleFactory
     */
    public FactHandleFactory newInstance() {
        return new LeapsFactHandleFactory();
    }
    
    public Class getFactHandleType() {
        return FactHandleImpl.class;
    }
}