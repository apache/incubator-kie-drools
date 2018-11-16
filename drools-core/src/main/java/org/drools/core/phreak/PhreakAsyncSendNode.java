/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.phreak;

import java.util.LinkedHashMap;
import java.util.concurrent.Executor;

import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.AsyncMessage;
import org.drools.core.reteoo.AsyncMessagesCoordinator;
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.reteoo.AsyncSendNode.AsyncSendMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.kie.internal.concurrent.ExecutorProviderFactory;

public class PhreakAsyncSendNode {

    private Executor executor() {
        return ExecutorProviderFactory.getExecutorProvider().getExecutor();
    }

    public void doNode(AsyncSendNode node,
                       AsyncSendMemory memory,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples) {

        if (srcLeftTuples.getInsertFirst() != null) {
            doLeftInserts(node, memory, wm, srcLeftTuples);
        }

        srcLeftTuples.resetAll();
    }

    public void doLeftInserts(AsyncSendNode node,
                              AsyncSendMemory memory,
                              InternalWorkingMemory wm,
                              TupleSets<LeftTuple> srcLeftTuples) {

        BetaMemory bm = memory.getBetaMemory();
        ContextEntry[] context = bm.getContext();
        BetaConstraints betaConstraints = node.getBetaConstraints();
        AlphaNodeFieldConstraint[] alphaConstraints = node.getAlphaConstraints();
        DataProvider dataProvider = node.getDataProvider();
        Class<?> resultClass = node.getResultClass();

        for (LeftTuple leftTuple = srcLeftTuples.getInsertFirst(); leftTuple != null; ) {
            LeftTuple next = leftTuple.getStagedNext();

            PropagationContext propagationContext = leftTuple.getPropagationContext();

            boolean useLeftMemory = RuleNetworkEvaluator.useLeftMemory(node, leftTuple);

            if (useLeftMemory) {
                memory.getBetaMemory().getLeftTupleMemory().add(leftTuple);
                leftTuple.setContextObject( new LinkedHashMap<>() );
            }

            betaConstraints.updateFromTuple(context, wm, leftTuple);

            LeftTuple finalLeftTuple = leftTuple;

            executor().execute( () -> {
                // TODO context is not thread safe, it needs to be cloned
                fetchAndSendResults( node, memory, wm, context, betaConstraints, alphaConstraints, dataProvider,
                        resultClass, finalLeftTuple, propagationContext );
            } );

            leftTuple.clearStaged();
            leftTuple = next;
        }
        betaConstraints.resetTuple(context);
    }

    private void fetchAndSendResults( AsyncSendNode node, AsyncSendMemory memory, InternalWorkingMemory wm,
                                      ContextEntry[] context, BetaConstraints betaConstraints, AlphaNodeFieldConstraint[] alphaConstraints,
                                      DataProvider dataProvider, Class<?> resultClass, LeftTuple leftTuple, PropagationContext propagationContext ) {
        for (final java.util.Iterator<?> it = dataProvider.getResults(leftTuple,
                                                                      wm,
                                                                      propagationContext,
                                                                      memory.providerContext); it.hasNext(); ) {
            final Object object = it.next();
            if ( (object == null) || !resultClass.isAssignableFrom( object.getClass() ) ) {
                continue; // skip anything if it not assignable
            }

            InternalFactHandle factHandle = node.createFactHandle(leftTuple, propagationContext, wm, object);

            if ( isAllowed( factHandle, alphaConstraints, wm ) ) {
                propagate( node, wm, factHandle, betaConstraints, context );
            }
        }
    }

    public static boolean isAllowed( InternalFactHandle factHandle,
                                     AlphaNodeFieldConstraint[] alphaConstraints,
                                     InternalWorkingMemory wm ) {
        if (alphaConstraints != null) {
            for (AlphaNodeFieldConstraint alphaConstraint : alphaConstraints) {
                if ( !alphaConstraint.isAllowed( factHandle, wm ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    public void propagate( AsyncSendNode node,
                           InternalWorkingMemory wm,
                           InternalFactHandle factHandle,
                           BetaConstraints betaConstraints,
                           ContextEntry[] context ) {
        if (betaConstraints.isAllowedCachedLeft(context, factHandle)) {
            AsyncMessagesCoordinator.get().propagate( node.getMessageId(), new AsyncMessage( wm, factHandle.getObject() ) );
        }
    }
}
