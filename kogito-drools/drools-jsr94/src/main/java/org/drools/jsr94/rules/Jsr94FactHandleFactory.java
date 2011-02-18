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

package org.drools.jsr94.rules;

import org.drools.common.AbstractFactHandleFactory;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.TypeDeclaration;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.FactHandleFactory;

/**
 * A factory for creating <code>Handle</code>s.
 * 
 */
public final class Jsr94FactHandleFactory extends AbstractFactHandleFactory {

    private static final long serialVersionUID = 510l;

    protected final InternalFactHandle newFactHandle(final int id,
                                                     final Object object,
                                                     final long recency,
                                                     final ObjectTypeConf conf,
                                                     final InternalWorkingMemory workingMemory,
                                                     final WorkingMemoryEntryPoint entryPoint) {
        if ( conf != null && conf.isEvent() ) {
            // later we need to centralize the following code snippet in a common method
            // shared by all fact handle factory implementations
            TypeDeclaration type = conf.getTypeDeclaration();
            long timestamp = workingMemory.getSessionClock().getCurrentTime();
            long duration = 0;
            if ( type.getDurationExtractor() != null ) {
                duration = type.getDurationExtractor().getLongValue( workingMemory,
                                                                     object );
            }
            return new Jsr94EventFactHandle( id,
                                             object,
                                             recency,
                                             timestamp,
                                             duration,
                                             entryPoint );
        } else {
            return new Jsr94FactHandle( id,
                                        object,
                                        recency,
                                        entryPoint );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.reteoo.FactHandleFactory#newInstance()
     */
    public FactHandleFactory newInstance() {
        return new Jsr94FactHandleFactory();
    }

    public Class getFactHandleType() {
        return Jsr94FactHandle.class;
    }

    public FactHandleFactory newInstance(int id,
                                         long counter) {
        // TODO Auto-generated method stub
        return null;
    }
}
