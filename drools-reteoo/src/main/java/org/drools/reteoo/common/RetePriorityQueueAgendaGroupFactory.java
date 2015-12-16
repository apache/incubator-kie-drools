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

package org.drools.reteoo.common;

import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.impl.InternalKnowledgeBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class RetePriorityQueueAgendaGroupFactory implements AgendaGroupFactory, Externalizable {

    private static final AgendaGroupFactory INSTANCE = new RetePriorityQueueAgendaGroupFactory();

    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public InternalAgendaGroup createAgendaGroup(String name, InternalKnowledgeBase kBase) {
        return new ReteAgendaGroupQueueImpl( name, kBase );
    }
}
