/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.impl;

import org.kie.dmn.core.api.event.*;
import org.kie.dmn.core.ast.DecisionNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DMNRuntimeEventManagerImpl implements InternalDMNRuntimeEventManager {
    private static final Logger logger = LoggerFactory.getLogger( DMNRuntimeEventManagerImpl.class );

    private Set<DMNRuntimeEventListener> listeners = new HashSet<>();

    @Override
    public void addListener(DMNRuntimeEventListener listener) {
        if( listener != null ) {
            this.listeners.add( listener );
        }
    }

    @Override
    public void removeListener(DMNRuntimeEventListener listener) {
        this.listeners.remove( listener );
    }

    @Override
    public Set<DMNRuntimeEventListener> getListeners() {
        return listeners;
    }

    @Override
    public void fireBeforeEvaluateDecision( DecisionNode decision, DMNResultImpl result) {
        BeforeEvaluateDecisionEvent event = new BeforeEvaluateDecisionEventImpl( decision, result );
        notifyListeners( l -> l.beforeEvaluateDecision( event ) );
    }

    @Override
    public void fireAfterEvaluateDecision( DecisionNode decision, DMNResultImpl result) {
        AfterEvaluateDecisionEvent event = new AfterEvaluateDecisionEventImpl( decision, result );
        notifyListeners( l -> l.afterEvaluateDecision( event ) );
    }

    @Override
    public void fireBeforeEvaluateDecisionTable(String dtName, DMNResultImpl result) {
        BeforeEvaluateDecisionTableEvent event = new BeforeEvaluateDecisionTableEventImpl( dtName, result );
        notifyListeners( l -> l.beforeEvaluateDecisionTable( event ) );
    }

    @Override
    public void fireAfterEvaluateDecisionTable(String dtName, DMNResultImpl result, List<Integer> matches ) {
        AfterEvaluateDecisionTableEvent event = new AfterEvaluateDecisionTableEventImpl( dtName, result, matches );
        notifyListeners( l -> l.afterEvaluateDecisionTable( event ) );
    }

    private void notifyListeners(Consumer<DMNRuntimeEventListener> consumer) {
        for( DMNRuntimeEventListener listener : listeners ) {
            try {
                consumer.accept( listener );
            } catch ( Throwable t ) {
                logger.error( "Error notifying listener '"+listener+"'", t );
            }
        }
    }
}
