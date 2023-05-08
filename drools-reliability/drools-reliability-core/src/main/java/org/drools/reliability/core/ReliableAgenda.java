/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.drools.reliability.core;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Storage;
import org.drools.core.impl.RuleBase;
import org.drools.core.phreak.PropagationList;
import org.drools.kiesession.agenda.DefaultAgenda;

public class ReliableAgenda extends DefaultAgenda {

    public ReliableAgenda() { }

    public ReliableAgenda(RuleBase kBase) {
        super( kBase );
    }

    public ReliableAgenda(RuleBase kBase, boolean initMain) {
        super( kBase, initMain );
    }

    @Override
    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        super.setWorkingMemory(workingMemory);
    }

    @Override
    protected PropagationList createPropagationList() {
        Storage<String, Object> componentsStorage = StorageManagerFactory.get().getStorageManager().getOrCreateStorageForSession(workingMemory, "components");
        ReliablePropagationList propagationList = (ReliablePropagationList) componentsStorage.get("PropagationList");
        if (propagationList == null) {
            propagationList = new ReliablePropagationList(workingMemory);
            componentsStorage.put("PropagationList", propagationList);
        } else {
            //propagationList = new ReliablePropagationList(workingMemory, propagationList);
            propagationList.setReteEvaluator(workingMemory);
        }
        return propagationList;
    }
}
