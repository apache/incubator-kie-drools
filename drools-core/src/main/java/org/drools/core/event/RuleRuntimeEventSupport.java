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
package org.drools.core.event;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.event.rule.impl.ObjectDeletedEventImpl;
import org.drools.core.event.rule.impl.ObjectInsertedEventImpl;
import org.drools.core.event.rule.impl.ObjectUpdatedEventImpl;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;

public class RuleRuntimeEventSupport extends AbstractEventSupport<RuleRuntimeEventListener> {

    public void fireObjectInserted(final PropagationContext propagationContext,
                                   final FactHandle handle,
                                   final Object object,
                                   final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            ObjectInsertedEventImpl event = new ObjectInsertedEventImpl( asKieRuntime(reteEvaluator), propagationContext, handle, object );
            notifyAllListeners( event, ( l, e ) -> l.objectInserted( e ) );
        }
    }

    public void fireObjectUpdated(final PropagationContext propagationContext,
                                  final FactHandle handle,
                                  final Object oldObject,
                                  final Object object,
                                  final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            ObjectUpdatedEventImpl event = new ObjectUpdatedEventImpl( asKieRuntime(reteEvaluator), propagationContext, handle, oldObject, object );
            notifyAllListeners( event, ( l, e ) -> l.objectUpdated( e ) );
        }
    }

    public void fireObjectRetracted(final PropagationContext propagationContext,
                                    final FactHandle handle,
                                    final Object oldObject,
                                    final ReteEvaluator reteEvaluator) {
        if ( hasListeners() ) {
            ObjectDeletedEventImpl event = new ObjectDeletedEventImpl( asKieRuntime(reteEvaluator), propagationContext, handle, oldObject );
            notifyAllListeners( event, ( l, e ) -> l.objectDeleted( e ) );
        }
    }

    private KieRuntime asKieRuntime(ReteEvaluator reteEvaluator) {
        return reteEvaluator instanceof InternalWorkingMemory ? ((InternalWorkingMemory) reteEvaluator).getKnowledgeRuntime() : null;
    }
}
