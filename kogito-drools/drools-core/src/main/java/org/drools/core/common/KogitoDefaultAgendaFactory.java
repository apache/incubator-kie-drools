/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.io.Serializable;

import org.drools.core.impl.InternalKnowledgeBase;

public class KogitoDefaultAgendaFactory implements AgendaFactory, Serializable {

    private static final AgendaFactory INSTANCE = new KogitoDefaultAgendaFactory();

    public static AgendaFactory getInstance() {
        return INSTANCE;
    }

    private KogitoDefaultAgendaFactory() { }

    public InternalAgenda createAgenda(InternalKnowledgeBase kBase, boolean initMain) {
        return kBase.getConfiguration().isMultithreadEvaluation() ?
               new CompositeDefaultAgenda(kBase, initMain ) :
               new KogitoDefaultAgenda(kBase, initMain );
    }

    public InternalAgenda createAgenda(InternalKnowledgeBase kBase) {
        return kBase.getConfiguration().isMultithreadEvaluation() ?
               new CompositeDefaultAgenda(kBase ) :
               new KogitoDefaultAgenda(kBase );
    }

}
