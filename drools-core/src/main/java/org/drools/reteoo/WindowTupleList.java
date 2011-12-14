/*
 * Copyright 2011 JBoss Inc
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

import org.drools.common.EventFactHandle;

/**
 * A class to hold a list of the right tuples that belong to a 
 * window (sliding, tumbling, etc)
 * 
 * @author etirelli
 */
public class WindowTupleList {
    protected EventFactHandle    handle;

    private WindowTupleList      listPrevious;
    private WindowTupleList      listNext;

    public WindowTuple           firstWindowTuple;
    public WindowTuple           lastWindowTuple;

    private WindowNode node;

    public WindowTupleList() {
    }
    
    public WindowTupleList( EventFactHandle handle,
                            WindowNode node) {
        this.handle = handle;
        this.node = node;

        //this.handle.addLastWindowTuple( this );
    }

    public WindowNode getWindowNode() {
        return this.node;
    }
    
    public void unlinkFromHandle() {
        //this.handle.removeWindowTuple( this );
        this.handle = null;
        this.listPrevious = null;
        this.listNext = null;
    }

    public EventFactHandle getHandle() {
        return this.handle;
    }

    public WindowTupleList getListPrevious() {
        return listPrevious;
    }

    public void setListPrevious(WindowTupleList listPrevious) {
        this.listPrevious = listPrevious;
    }

    public WindowTupleList getListNext() {
        return listNext;
    }

    public void setListNext(WindowTupleList listNext) {
        this.listNext = listNext;
    }

    public void addFirstWindowTuple( WindowTuple windowTuple ) {
        WindowTuple previousFirst = firstWindowTuple;
        firstWindowTuple = windowTuple;
        if ( previousFirst == null ) {
            windowTuple.setWindowPrevious( null );
            windowTuple.setWindowNext( null );
            lastWindowTuple = windowTuple;
        } else {
            windowTuple.setWindowPrevious( null );
            windowTuple.setWindowNext( previousFirst );
            previousFirst.setWindowPrevious( windowTuple );
        }
    }

    public void addLastWindowTuple( WindowTuple windowTuple ) {
        WindowTuple previousLast = lastWindowTuple;
        lastWindowTuple = windowTuple;
        if( previousLast == null ){
            windowTuple.setWindowPrevious( null );
            windowTuple.setWindowNext( null );
            firstWindowTuple = windowTuple;
        } else {
            windowTuple.setWindowPrevious( previousLast );
            windowTuple.setWindowNext( null );
            previousLast.setWindowNext( windowTuple );
        }
    }
    
    public void removeWindowTuple( WindowTuple windowTuple ) {
        WindowTuple previous = windowTuple.getWindowPrevious();
        WindowTuple next = windowTuple.getWindowNext();

        if ( previous != null && next != null ) {
            // remove  from middle
            previous.setWindowNext( next );
            next.setWindowPrevious( previous );
        } else if ( next != null ) {
            // remove from first
            next.setWindowPrevious( null );
            firstWindowTuple = next;
        } else if ( previous != null ) {
            // remove from end
            previous.setWindowNext( null );
            lastWindowTuple = previous;
        } else {
            // single remaining item, no previous or next
            firstWindowTuple = null;
            lastWindowTuple = null;
        }
        windowTuple.setWindowPrevious( null );
        windowTuple.setWindowNext( null );
    }
    
    public WindowTuple getFirstWindowTuple() {
        return firstWindowTuple;
    }
    
    public WindowTuple getLastWindowTuple() {
        return lastWindowTuple;
    }

    public int hashCode() {
        return this.handle.hashCode();
    }

    public String toString() {
        return this.handle.toString() + "\n";
    }

    public boolean equals(WindowTupleList other) {
        // we know the object is never null and always of the  type ReteTuple
        if ( other == this ) {
            return true;
        }

        // A ReteTuple is  only the same if it has the same hashCode, factId and parent
        if ( (other == null) || (hashCode() != other.hashCode()) ) {
            return false;
        }

        return this.handle == other.handle;
    }

    public boolean equals(Object object) {
        return equals( (WindowTupleList) object );
    }
}
