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

import java.util.List;
import java.util.function.Consumer;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNRuntimeEventManagerUtils {

    private static final Logger logger = LoggerFactory.getLogger( DMNRuntimeEventManagerUtils.class );

    public static void fireBeforeEvaluateDecision( DMNRuntimeEventManager eventManager, DecisionNode decision, DMNResult result) {
        BeforeEvaluateDecisionEvent event = new BeforeEvaluateDecisionEventImpl( decision, result );
        notifyListeners( eventManager, l -> l.beforeEvaluateDecision( event ) );
    }

    public static void fireAfterEvaluateDecision( DMNRuntimeEventManager eventManager, DecisionNode decision, DMNResult result) {
        AfterEvaluateDecisionEvent event = new AfterEvaluateDecisionEventImpl( decision, result );
        notifyListeners( eventManager, l -> l.afterEvaluateDecision( event ) );
    }

    public static void fireBeforeEvaluateBKM( DMNRuntimeEventManager eventManager, BusinessKnowledgeModelNode bkm, DMNResult result) {
        BeforeEvaluateBKMEvent event = new BeforeEvaluateBKMEventImpl( bkm, result );
        notifyListeners( eventManager, l -> l.beforeEvaluateBKM( event ) );
    }

    public static void fireAfterEvaluateBKM( DMNRuntimeEventManager eventManager, BusinessKnowledgeModelNode bkm, DMNResult result) {
        AfterEvaluateBKMEvent event = new AfterEvaluateBKMEventImpl( bkm, result );
        notifyListeners( eventManager, l -> l.afterEvaluateBKM( event ) );
    }

    public static void fireBeforeEvaluateDecisionTable( DMNRuntimeEventManager eventManager, String nodeName, String dtName, DMNResult result) {
        BeforeEvaluateDecisionTableEvent event = new BeforeEvaluateDecisionTableEventImpl( nodeName, dtName, result );
        notifyListeners( eventManager, l -> l.beforeEvaluateDecisionTable( event ) );
    }

    public static void fireAfterEvaluateDecisionTable( DMNRuntimeEventManager eventManager, String nodeName, String dtName, DMNResult result, List<Integer> matches, List<Integer> fired ) {
        AfterEvaluateDecisionTableEvent event = new AfterEvaluateDecisionTableEventImpl( nodeName, dtName, result, matches, fired );
        notifyListeners( eventManager, l -> l.afterEvaluateDecisionTable( event ) );
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
}
