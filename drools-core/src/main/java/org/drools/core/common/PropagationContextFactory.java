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

package org.drools.core.common;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.bitmask.BitMask;

public interface PropagationContextFactory {

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final Tuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final BitMask modificationMask,
                                                       final Class<?> modifiedClass,
                                                       final MarshallerReaderContext readerContext);

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final Tuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint,
                                                       final MarshallerReaderContext readerContext);

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final Tuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final int activeActivations,
                                                       final int dormantActivations,
                                                       final EntryPointId entryPoint,
                                                       final BitMask modificationMask);

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final Tuple leftTuple,
                                                       final InternalFactHandle factHandle,
                                                       final EntryPointId entryPoint);

    public PropagationContext createPropagationContext(final long number,
                                                       final PropagationContext.Type type,
                                                       final RuleImpl rule,
                                                       final Tuple leftTuple,
                                                       final InternalFactHandle factHandle);

}