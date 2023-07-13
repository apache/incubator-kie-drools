/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reliability.core;


import org.drools.core.common.AgendaFactory;
import org.drools.core.common.InternalAgenda;
import org.drools.core.impl.InternalRuleBase;

import java.io.Serializable;

public class ReliableAgendaFactory implements AgendaFactory, Serializable {

    private static final AgendaFactory INSTANCE = new ReliableAgendaFactory();

    public static AgendaFactory getInstance() {
        return INSTANCE;
    }

    private ReliableAgendaFactory() { }

    public InternalAgenda createAgenda(InternalRuleBase kBase, boolean initMain) {
        return new ReliableAgenda( kBase, initMain );
    }
}
