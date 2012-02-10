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

package org.drools.reteoo;

import java.sql.Date;

import org.drools.common.AbstractFactHandleFactory;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.TypeDeclaration;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.FactHandleFactory;

public class ReteooFactHandleFactory extends AbstractFactHandleFactory {

    private static final long serialVersionUID = 510l;

    public ReteooFactHandleFactory() {
        super();
    }

    public ReteooFactHandleFactory(int id,
                                   long counter) {
        super( id,
               counter );
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newFactHandle(long)
     */
    public final InternalFactHandle newFactHandle(final int id,
                                                  final Object object,
                                                  final long recency,
                                                  final ObjectTypeConf conf,
                                                  final InternalWorkingMemory workingMemory,
                                                  final WorkingMemoryEntryPoint wmEntryPoint) {
        if ( conf != null && conf.isEvent() ) {
            TypeDeclaration type = conf.getTypeDeclaration();
            long timestamp;
            if ( type.getTimestampExtractor() != null ) {
                if ( Date.class.isAssignableFrom( type.getTimestampExtractor().getExtractToClass() ) ) {
                    timestamp = ((Date) type.getTimestampExtractor().getValue( workingMemory,
                                                                               object )).getTime();
                } else {
                    timestamp = type.getTimestampExtractor().getLongValue( workingMemory,
                                                                           object );
                }
            } else {
                timestamp = workingMemory.getTimerService().getCurrentTime();
            }
            long duration = 0;
            if ( type.getDurationExtractor() != null ) {
                duration = type.getDurationExtractor().getLongValue( workingMemory,
                                                                     object );
            }
            return new EventFactHandle( id,
                                        object,
                                        recency,
                                        timestamp,
                                        duration,
                                        wmEntryPoint );
        } else {
            return new DefaultFactHandle( id,
                                          object,
                                          recency,
                                          wmEntryPoint );
        }
    }

    /* (non-Javadoc)
     * @see org.drools.reteoo.FactHandleFactory#newInstance()
     */
    public FactHandleFactory newInstance() {
        return new ReteooFactHandleFactory();
    }

    public FactHandleFactory newInstance(int id,
                                         long counter) {
        return new ReteooFactHandleFactory( id,
                                            counter );
    }

    public Class getFactHandleType() {
        return DefaultFactHandle.class;
    }
}
