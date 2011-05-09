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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.util.ObjectHashMap;
import org.drools.rule.ContextEntry;

public class BetaMemory
    implements
    Externalizable, Unlinkable {

    private static final long serialVersionUID = 510l;

    private LeftTupleMemory   leftTupleMemory;
    private RightTupleMemory  rightTupleMemory;
    private ObjectHashMap     createdHandles;
    private ContextEntry[]    context;
    private Object            behaviorContext;
    
    /* Let's start with only right unlinked. */
    private boolean           isLeftUnlinked = false;
    private boolean           isRightUnlinked = true;

    private boolean           open;

    public BetaMemory() {
    }

    public BetaMemory(final LeftTupleMemory tupleMemory,
                      final RightTupleMemory objectMemory,
                      final ContextEntry[] context) {
        this.leftTupleMemory = tupleMemory;
        this.rightTupleMemory = objectMemory;
        this.context = context;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        leftTupleMemory = (LeftTupleMemory) in.readObject();
        rightTupleMemory = (RightTupleMemory) in.readObject();
        createdHandles = (ObjectHashMap) in.readObject();
        context = (ContextEntry[]) in.readObject();
        behaviorContext = (Object) in.readObject();

        isLeftUnlinked = in.readBoolean();
        isRightUnlinked = in.readBoolean();

        open = ( boolean ) in.readBoolean();

    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( leftTupleMemory );
        out.writeObject( rightTupleMemory );
        out.writeObject( createdHandles );
        out.writeObject( context );
        out.writeObject( behaviorContext );
        out.writeBoolean( isLeftUnlinked );
        out.writeBoolean( isRightUnlinked );
        out.writeBoolean(  open  );
    }

    public RightTupleMemory getRightTupleMemory() {
        return this.rightTupleMemory;
    }

    public LeftTupleMemory getLeftTupleMemory() {
        return this.leftTupleMemory;
    }

    public ObjectHashMap getCreatedHandles() {
        if ( this.createdHandles == null ) {
            this.createdHandles = new ObjectHashMap();
        }
        return this.createdHandles;
    }

    /**
     * @return the context
     */
    public ContextEntry[] getContext() {
        return context;
    }

    public Object getBehaviorContext() {
        return behaviorContext;
    }

    public void setBehaviorContext(Object behaviorContext) {
        this.behaviorContext = behaviorContext;
    }

    public boolean isOpen() {
        return open;
    }
    
    public void setOpen(boolean open) {
        this.open = open;
    }
    
    public boolean isLeftUnlinked() {
        return this.isLeftUnlinked;
    }
    
    public boolean isRightUnlinked() {
        return this.isRightUnlinked;
    }

    public void linkLeft() {
        this.isLeftUnlinked = false;
    }

    public void linkRight() {
        this.isRightUnlinked = false;
    }

    public void unlinkLeft() {
        this.isLeftUnlinked = true;
    }

    public void unlinkRight() {
        this.isRightUnlinked = true;
    }    
}
