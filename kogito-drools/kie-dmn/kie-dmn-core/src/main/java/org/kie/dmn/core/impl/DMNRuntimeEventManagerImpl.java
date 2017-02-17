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

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.kie.dmn.core.api.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DMNRuntimeEventManagerImpl implements DMNRuntimeEventManager {
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

    public void fireBeforeEvaluateDecision( DecisionNode decision, DMNResult result) {
        BeforeEvaluateDecisionEvent event = new BeforeEvaluateDecisionEventImpl( decision, result );
        notifyListeners( l -> l.beforeEvaluateDecision( event ) );
    }

    public void fireAfterEvaluateDecision( DecisionNode decision, DMNResult result) {
        AfterEvaluateDecisionEvent event = new AfterEvaluateDecisionEventImpl( decision, result );
        notifyListeners( l -> l.afterEvaluateDecision( event ) );
    }

    public void fireBeforeEvaluateBKM(BusinessKnowledgeModelNode bkm, DMNResult result) {
        BeforeEvaluateBKMEvent event = new BeforeEvaluateBKMEventImpl( bkm, result );
        notifyListeners( l -> l.beforeEvaluateBKM( event ) );
    }

    public void fireAfterEvaluateBKM(BusinessKnowledgeModelNode bkm, DMNResult result) {
        AfterEvaluateBKMEvent event = new AfterEvaluateBKMEventImpl( bkm, result );
        notifyListeners( l -> l.afterEvaluateBKM( event ) );
    }

    public void fireBeforeEvaluateDecisionTable(String nodeName, String dtName, DMNResult result) {
        BeforeEvaluateDecisionTableEvent event = new BeforeEvaluateDecisionTableEventImpl( nodeName, dtName, result );
        notifyListeners( l -> l.beforeEvaluateDecisionTable( event ) );
    }

    public void fireAfterEvaluateDecisionTable(String nodeName, String dtName, DMNResult result, List<Integer> matches, List<Integer> fired ) {
        AfterEvaluateDecisionTableEvent event = new AfterEvaluateDecisionTableEventImpl( nodeName, dtName, result, matches, fired );
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
