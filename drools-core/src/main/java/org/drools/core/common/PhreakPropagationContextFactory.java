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
package org.drools.core.common;

import java.io.Serializable;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.EntryPointId;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.util.bitmask.BitMask;

public class PhreakPropagationContextFactory implements PropagationContextFactory, Serializable  {

    private static final PropagationContextFactory INSTANCE = new PhreakPropagationContextFactory();

    public static PropagationContextFactory getInstance() {
        return INSTANCE;
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final TerminalNode terminalNode,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final BitMask modificationMask,
                                                       final Class<?> modifiedClass,
                                                       final MarshallerReaderContext readerContext) {
        return new PhreakPropagationContext(number, type, rule, terminalNode, factHandle, entryPoint, modificationMask, modifiedClass, readerContext);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final TerminalNode terminalNode,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final MarshallerReaderContext readerContext) {
        return new PhreakPropagationContext(number, type, rule, terminalNode, factHandle, entryPoint, readerContext);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final TerminalNode terminalNode,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint) {
        return new PhreakPropagationContext(number, type, rule, terminalNode, factHandle, entryPoint);
    }

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final TerminalNode terminalNode,
                                                       final InternalFactHandle factHandle) {
        return new PhreakPropagationContext(number, type, rule, terminalNode, factHandle);
    }

    public static PropagationContext createPropagationContextForFact( ReteEvaluator reteEvaluator, InternalFactHandle factHandle, PropagationContext.Type propagationType ) {
        PropagationContextFactory pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();

        // if the fact is still in the working memory (since it may have been previously retracted already
        return pctxFactory.createPropagationContext( reteEvaluator.getNextPropagationIdCounter(), propagationType,
                                                     null, null, factHandle );
    }
}
