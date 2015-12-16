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

import org.drools.core.SessionConfiguration;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.spi.FactHandleFactory;
import org.kie.api.runtime.Environment;

public interface WorkingMemoryFactory {
    InternalWorkingMemory createWorkingMemory(final long id,
                                              final InternalKnowledgeBase kBase,
                                              final SessionConfiguration config,
                                              final Environment environment);

    InternalWorkingMemory createWorkingMemory(final long id,
                                              final InternalKnowledgeBase kBase,
                                              final FactHandleFactory handleFactory,
                                              final long propagationContext,
                                              final SessionConfiguration config,
                                              final InternalAgenda agenda,
                                              final Environment environment);

    InternalWorkingMemory createWorkingMemory(final long id,
                                              final InternalKnowledgeBase kBase,
                                              final FactHandleFactory handleFactory,
                                              final InternalFactHandle initialFactHandle,
                                              final long propagationContext,
                                              final SessionConfiguration config,
                                              final Environment environment,
                                              final RuleRuntimeEventSupport workingMemoryEventSupport,
                                              final AgendaEventSupport agendaEventSupport,
                                              final RuleEventListenerSupport ruleEventListenerSupport,
                                              final InternalAgenda agenda);
}

