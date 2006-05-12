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



import java.util.Collections;
import java.util.List;

import org.drools.spi.PropagationContext;

public class MockTupleSource extends TupleSource {

    private int attached;

    private int updated;

    public MockTupleSource(int id) {
        super( id );
    }

    public void attach() {
        this.attached++;

    }

    public int getAttached() {
        return this.attached;
    }

    public int getUdated() {
        return this.updated;
    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        this.updated++;
    }

    public void remove(BaseNode node,
                       WorkingMemoryImpl[] workingMemories) {
        // TODO Auto-generated method stub

    }

    public void attach(WorkingMemoryImpl[] workingMemories) {
        // TODO Auto-generated method stub

    }

    public List getPropagatedTuples(WorkingMemoryImpl workingMemory,
                                    TupleSink sink) {
        // TODO Auto-generated method stub
        return Collections.EMPTY_LIST;
    }

}