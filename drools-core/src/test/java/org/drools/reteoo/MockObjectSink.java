package org.drools.reteoo;
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



import java.util.ArrayList;
import java.util.List;

import org.drools.spi.PropagationContext;

public class MockObjectSink
    implements
    ObjectSink {
    private List asserted  = new ArrayList();
    private List retracted = new ArrayList();
    private List modified = new ArrayList();    

    public void assertObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        this.asserted.add( new Object[]{handle, context, workingMemory} );
    }

    public void retractObject(FactHandleImpl handle,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) {
        this.retracted.add( new Object[]{handle, context, workingMemory} );
    }

    public void modifyObject(FactHandleImpl handle,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        this.modified.add( new Object[]{handle, context, workingMemory} );

    }    
    
    public List getAsserted() {
        return this.asserted;
    }

    public List getRetracted() {
        return this.retracted;
    }

    public List getModified() {
        return this.modified;
    }

}