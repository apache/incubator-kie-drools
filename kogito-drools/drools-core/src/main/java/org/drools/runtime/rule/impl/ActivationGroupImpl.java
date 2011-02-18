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

import org.drools.Agenda;
import org.drools.common.InternalAgenda;
import org.drools.runtime.rule.ActivationGroup;

public class ActivationGroupImpl implements ActivationGroup, Externalizable {
    
    private String name;
    
    private InternalAgenda agenda;    
    
    ActivationGroupImpl() {
        
    }
    
    ActivationGroupImpl(org.drools.spi.ActivationGroup activationGroup, InternalAgenda agenda) {
        this.name = activationGroup.getName();
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
        this.agenda.clearAndCancelActivationGroup( this.name );
    }    

}
