/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.traits.core.reteoo;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ReteooFactHandleFactory;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.FactHandleFactory;
import org.drools.traits.core.common.TraitDefaultFactHandle;

public class TraitFactHandleFactory extends ReteooFactHandleFactory {

    @Override
    public DefaultFactHandle createDefaultFactHandle(long id, Object initialFact, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        return new TraitDefaultFactHandle(id, initialFact, recency, wmEntryPoint);
    }

    @Override
    public FactHandleFactory newInstance() {
        return new TraitFactHandleFactory();
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.FactHandleFactory#newFactHandle(long)
     */
    public InternalFactHandle newFactHandle(final long id,
                                            final Object object,
                                            final long recency,
                                            final ObjectTypeConf conf,
                                            final ReteEvaluator reteEvaluator,
                                            final WorkingMemoryEntryPoint wmEntryPoint ) {
        if ( conf != null && conf.isEvent() ) {
            TypeDeclaration type = conf.getTypeDeclaration();
            long timestamp;
            if ( type != null && type.getTimestampExtractor() != null ) {
                timestamp = type.getTimestampExtractor().getLongValue( reteEvaluator, object );
            } else {
                timestamp = reteEvaluator.getTimerService().getCurrentTime();
            }
            long duration = 0;
            if ( type != null && type.getDurationExtractor() != null ) {
                duration = type.getDurationExtractor().getLongValue( reteEvaluator, object );
            }
            return new EventFactHandle(id,
                                       object,
                                       recency,
                                       timestamp,
                                       duration,
                                       wmEntryPoint != null ? wmEntryPoint : reteEvaluator.getDefaultEntryPoint(),
                                       conf != null && conf.isTrait() );
        } else {
            return new TraitDefaultFactHandle(id,
                                              object,
                                              recency,
                                              wmEntryPoint != null ? wmEntryPoint : reteEvaluator.getDefaultEntryPoint(),
                                              conf != null && conf.isTrait() );
        }
    }
}
