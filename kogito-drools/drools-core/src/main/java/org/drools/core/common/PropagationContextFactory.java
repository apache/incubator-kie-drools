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
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;

public interface PropagationContextFactory {

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle,
                                                EntryPointId entryPoint,
                                                BitMask modificationMask,
                                                Class<?> modifiedClass,
                                                MarshallerReaderContext readerContext);

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle,
                                                EntryPointId entryPoint,
                                                MarshallerReaderContext readerContext);

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle,
                                                EntryPointId entryPoint);

    PropagationContext createPropagationContext(long number,
                                                PropagationContext.Type type,
                                                RuleImpl rule,
                                                TerminalNode terminalNode,
                                                InternalFactHandle factHandle);

}