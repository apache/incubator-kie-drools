/*
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.rule.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;

import org.drools.common.InternalAgenda;
import org.drools.runtime.rule.Activation;
import org.drools.runtime.rule.AgendaGroup;

public class AgendaGroupImpl implements AgendaGroup, Externalizable {
    
    private String name;
    
    private InternalAgenda agenda;
    
    AgendaGroupImpl() {
        
    }
    
    AgendaGroupImpl(org.drools.spi.AgendaGroup agendaGroup, InternalAgenda agenda) {
        this.name = agendaGroup.getName();
        this.agenda = agenda;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( this.name );
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.name = in.readUTF();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void clear() {
        this.agenda.clearAndCancelAgendaGroup( this.name );
    }
    
    public void setFocus() {
        this.agenda.setFocus( this.name );
    }

//    public Collection<Activation> getActivations() {
//        return this.agenda.getAgendaGroup( this.name ).getActivations();
//    }

}
