/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import org.drools.core.impl.InternalKnowledgeBase;

import java.io.Serializable;

public class DefaultAgendaFactory implements AgendaFactory, Serializable {

    private static final AgendaFactory INSTANCE = new DefaultAgendaFactory();

    public static AgendaFactory getInstance() {
        return INSTANCE;
    }

    private DefaultAgendaFactory() { }

    public InternalAgenda createAgenda(InternalKnowledgeBase kBase, boolean initMain) {
        return kBase.getConfiguration().isMultithreadEvaluation() ?
               new CompositeDefaultAgenda( kBase, initMain ) :
               new DefaultAgenda( kBase, initMain );
    }

    public InternalAgenda createAgenda(InternalKnowledgeBase kBase) {
        return kBase.getConfiguration().isMultithreadEvaluation() ?
               new CompositeDefaultAgenda( kBase ) :
               new DefaultAgenda( kBase );
    }

}
