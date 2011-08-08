/*
 * Copyright 2005 JBoss Inc
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

package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.util.Entry;
import org.drools.core.util.LinkedListNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.time.JobHandle;

public class ScheduledAgendaItem extends AgendaItem
    implements
    Activation,
    Externalizable,
    LinkedListNode {

    private static final long        serialVersionUID = 510l;

    private LinkedListNode           previous;

    private LinkedListNode           next;

//
    private InternalAgenda     agenda;
    
    private JobHandle jobHandle;

    public ScheduledAgendaItem(final long activationNumber,
                               final LeftTuple tuple,
                               final InternalAgenda agenda,
                               final PropagationContext context,
                               final RuleTerminalNode rtn) {
        super(activationNumber, tuple, 0, context, rtn );
        this.agenda = agenda;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );        
        agenda    = (InternalAgenda)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject(agenda);
    }
    public LinkedListNode getNext() {
        return this.next;
    }

    public void setNext(final LinkedListNode next) {
        this.next = next;
    }    

    public void setNext(Entry next) {
        this.next = (LinkedListNode) next;        
    }    

    public LinkedListNode getPrevious() {
        return this.previous;
    }

    public void setPrevious(final LinkedListNode previous) {
        this.previous = previous;
    }

    public void remove() {
        this.agenda.removeScheduleItem( this );
    }
    
    public JobHandle getJobHandle() {
        return this.jobHandle;
    }

    public void setJobHandle(JobHandle jobHandle) {
        this.jobHandle = jobHandle;
    }
    
    public String toString() {
        return "[ScheduledActivation rule=" + getRule().getName() + ", tuple=" + getTuple() + "]";
    }

}
