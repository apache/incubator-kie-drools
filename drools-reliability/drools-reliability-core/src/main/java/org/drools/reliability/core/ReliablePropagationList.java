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

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.SynchronizedPropagationList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ReliablePropagationList extends SynchronizedPropagationList implements Externalizable {

    public ReliablePropagationList(ReteEvaluator reteEvaluator) {
        super(reteEvaluator);
    }

    public ReliablePropagationList(){super();}

    public void setReteEvaluator(ReteEvaluator reteEvaluator){this.reteEvaluator=reteEvaluator;}

    public ReliablePropagationList(ReteEvaluator reteEvaluator, ReliablePropagationList originalList) {
        super(reteEvaluator);
        this.head = originalList.head;
        this.tail = originalList.tail;
    }

    public boolean entryInTheList(Object entry){

        boolean inTheList=false;

        PropagationEntry current = this.head;

        while (current!=null && !inTheList){
            if (current instanceof  PropagationEntry.Insert){
                InternalFactHandle fh = ((PropagationEntry.Insert) current).getHandle();
                inTheList = fh.getObject().equals(entry);
            }else if (entry instanceof PropagationEntry.Update){

            }
            current = current.getNext();
        }

        return inTheList;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(head);
        out.writeObject(tail);
        out.writeBoolean(disposed);
        out.writeBoolean(hasEntriesDeferringExpiration);
        out.writeBoolean(firingUntilHalt);
   }
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.head = (PropagationEntry) in.readObject();
        this.tail = (PropagationEntry) in.readObject();
        this.disposed = in.readBoolean();
        this.hasEntriesDeferringExpiration = in.readBoolean();
        this.firingUntilHalt = in.readBoolean();
    }
}
