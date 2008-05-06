/*
 * Copyright 2008 JBoss Inc
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
 *
 * Created on Apr 26, 2008
 */

package org.drools.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Comparator;

import org.drools.common.EventFactHandle;
import org.drools.common.InternalWorkingMemory;

/**
 * @author etirelli
 *
 */
public class SlidingTimeWindow
    implements
    Behavior {

    private long size;

    public SlidingTimeWindow() {
    }
    
    /**
     * @param size
     */
    public SlidingTimeWindow(long size) {
        super();
        this.size = size;
    }

    /**
     * @inheritDoc
     *
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        // TODO Auto-generated method stub

    }

    /**
     * @inheritDoc
     *
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Auto-generated method stub

    }

    public BehaviorType getType() {
        return BehaviorType.TIME_WINDOW;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }

    public Comparator<EventFactHandle> getEventComparator() {
        return new Comparator<EventFactHandle>() {
            public int compare(EventFactHandle e1,
                               EventFactHandle e2) {
                return ( e1.getStartTimestamp() < e2.getStartTimestamp() ) ? -1 : ( e1.getStartTimestamp() == e2.getStartTimestamp() ? 0 : 1 );
            }
        };
    }

    public boolean isExpired(EventFactHandle event,
                             InternalWorkingMemory workingMemory) {
        return false;
    }

}
