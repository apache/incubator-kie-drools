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
package org.kie.dmn.core.impl;

import java.util.List;
import java.util.function.Consumer;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;
import org.kie.dmn.api.core.event.AfterEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.AfterEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.AfterInvokeBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateAllEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateContextEntryEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionServiceEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateDecisionTableEvent;
import org.kie.dmn.api.core.event.BeforeInvokeBKMEvent;
import org.kie.dmn.api.core.event.DMNRuntimeEventListener;
import org.kie.dmn.api.core.event.DMNRuntimeEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DMNRuntimeEventManagerUtils {

    private static final Logger logger = LoggerFactory.getLogger( DMNRuntimeEventManagerUtils.class );

    public static BeforeEvaluateDecisionEvent fireBeforeEvaluateDecision(DMNRuntimeEventManager eventManager, DecisionNode decision, DMNResult result) {
        if( eventManager.hasListeners() ) {
            BeforeEvaluateDecisionEvent event = new BeforeEvaluateDecisionEventImpl( decision, result );
            notifyListeners( eventManager, l -> l.beforeEvaluateDecision( event ) );
            return event;
        }
        return null;
    }

    public static void fireAfterEvaluateDecision( DMNRuntimeEventManager eventManager, DecisionNode decision, DMNResult result, BeforeEvaluateDecisionEvent beforeEvaluateDecisionEvent) {
        if( eventManager.hasListeners() ) {
            AfterEvaluateDecisionEvent event = new AfterEvaluateDecisionEventImpl(decision, result, beforeEvaluateDecisionEvent);
            notifyListeners(eventManager, l -> l.afterEvaluateDecision(event));
        }
    }

    public static void fireBeforeEvaluateDecisionService(DMNRuntimeEventManager eventManager, DecisionServiceNode decision, DMNResult result) {
        if( eventManager.hasListeners() ) {
            BeforeEvaluateDecisionServiceEvent event = new BeforeEvaluateDecisionServiceEventImpl(decision, result);
            notifyListeners(eventManager, l -> l.beforeEvaluateDecisionService(event));
        }
    }

    public static void fireAfterEvaluateDecisionService(DMNRuntimeEventManager eventManager, DecisionServiceNode decision, DMNResult result) {
        if( eventManager.hasListeners() ) {
            AfterEvaluateDecisionServiceEvent event = new AfterEvaluateDecisionServiceEventImpl(decision, result);
            notifyListeners(eventManager, l -> l.afterEvaluateDecisionService(event));
        }
    }

    public static void fireBeforeEvaluateBKM( DMNRuntimeEventManager eventManager, BusinessKnowledgeModelNode bkm, DMNResult result) {
        if( eventManager.hasListeners() ) {
            BeforeEvaluateBKMEvent event = new BeforeEvaluateBKMEventImpl(bkm, result);
            notifyListeners(eventManager, l -> l.beforeEvaluateBKM(event));
        }
    }

    public static void fireAfterEvaluateBKM( DMNRuntimeEventManager eventManager, BusinessKnowledgeModelNode bkm, DMNResult result) {
        if( eventManager.hasListeners() ) {
            AfterEvaluateBKMEvent event = new AfterEvaluateBKMEventImpl(bkm, result);
            notifyListeners(eventManager, l -> l.afterEvaluateBKM(event));
        }
    }

    public static void fireBeforeEvaluateDecisionTable( DMNRuntimeEventManager eventManager, String nodeName, String dtName, String dtId, DMNResult result) {
        if( eventManager.hasListeners() ) {
            BeforeEvaluateDecisionTableEvent event = new BeforeEvaluateDecisionTableEventImpl(nodeName, dtName, dtId, result);
            notifyListeners(eventManager, l -> l.beforeEvaluateDecisionTable(event));
        }
    }

    public static void fireAfterEvaluateDecisionTable( DMNRuntimeEventManager eventManager, String nodeName, String dtName, String dtId, DMNResult result, List<Integer> matches, List<Integer> fired ) {
        if( eventManager.hasListeners() ) {
            AfterEvaluateDecisionTableEvent event = new AfterEvaluateDecisionTableEventImpl(nodeName, dtName, dtId, result, matches, fired);
            notifyListeners(eventManager, l -> l.afterEvaluateDecisionTable(event));
        }
    }

    public static void fireBeforeEvaluateContextEntry( DMNRuntimeEventManager eventManager, String nodeName, String variableName, String variableId, String expressionId, DMNResult result) {
        if( eventManager.hasListeners() ) {
            BeforeEvaluateContextEntryEvent event = new BeforeEvaluateContextEntryEventImpl(nodeName, variableName, variableId, expressionId, result);
            notifyListeners(eventManager, l -> l.beforeEvaluateContextEntry(event));
        }
    }

    public static void fireAfterEvaluateContextEntry( DMNRuntimeEventManager eventManager, String nodeName, String variableName, String variableId, String expressionId, Object expressionResult, DMNResult result ) {
        if( eventManager.hasListeners() ) {
            AfterEvaluateContextEntryEvent event = new AfterEvaluateContextEntryEventImpl(nodeName, variableName, variableId, expressionId, expressionResult, result);
            notifyListeners(eventManager, l -> l.afterEvaluateContextEntry(event));
        }
    }

    public static void fireBeforeInvokeBKM( DMNRuntimeEventManager eventManager, BusinessKnowledgeModelNode bkm, DMNResult result, List<Object> invocationParameters ) {
        if( eventManager.hasListeners() ) {
            BeforeInvokeBKMEvent event = new BeforeInvokeBKMEventImpl(bkm, result, invocationParameters);
            notifyListeners(eventManager, l -> l.beforeInvokeBKM(event));
        }
    }

    public static void fireAfterInvokeBKM( DMNRuntimeEventManager eventManager, BusinessKnowledgeModelNode bkm, DMNResult result, Object invocationResult ) {
        if (eventManager.hasListeners()) {
            AfterInvokeBKMEvent event = new AfterInvokeBKMEventImpl(bkm, result, invocationResult);
            notifyListeners(eventManager, l -> l.afterInvokeBKM(event));
        }
    }

    public static void fireBeforeEvaluateAll(DMNRuntimeEventManagerImpl eventManager, DMNModel model, DMNResultImpl result) {
        if( eventManager.hasListeners() ) {
            BeforeEvaluateAllEvent event = new BeforeEvaluateAllEventImpl(model.getNamespace(), model.getName(), result);
            notifyListeners(eventManager, l -> l.beforeEvaluateAll(event));
        }
    }

    public static void fireAfterEvaluateAll(DMNRuntimeEventManagerImpl eventManager, DMNModel model, DMNResultImpl result) {
        if( eventManager.hasListeners() ) {
            AfterEvaluateAllEvent event = new AfterEvaluateAllEventImpl(model.getNamespace(), model.getName(), result);
            notifyListeners(eventManager, l -> l.afterEvaluateAll(event));
        }
    }

    private static void notifyListeners(DMNRuntimeEventManager eventManager, Consumer<DMNRuntimeEventListener> consumer) {
        for( DMNRuntimeEventListener listener : eventManager.getListeners() ) {
            try {
                consumer.accept( listener );
            } catch ( Throwable t ) {
                logger.error( "Error notifying listener '"+listener+"'", t );
            }
        }
    }

    private DMNRuntimeEventManagerUtils() {
        // Constructing instances is not allowed for this class
    }

}
