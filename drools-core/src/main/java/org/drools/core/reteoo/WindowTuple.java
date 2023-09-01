package org.drools.core.reteoo;

import org.drools.core.common.InternalFactHandle;

/**
 * A specialisation of RightTuples that also belong to a window
 * 
 *
 */
public class WindowTuple extends RightTupleImpl {
    private WindowTupleList       windowList;
    
    private WindowTuple           windowPrevious;
    private WindowTuple           windowNext;

    public WindowTuple() {
    }
    
    public WindowTuple(InternalFactHandle handle) {
        super( handle );
    }

    public WindowTuple(InternalFactHandle handle,
                       RightTupleSink sink,
                       WindowTupleList list ) {
        super( handle, sink );
        this.windowList = list;
        list.addLastWindowTuple( this );
    }

    public void unlinkFromRightParent() {
        super.unlinkFromRightParent();
        this.windowList.removeWindowTuple( this );
    }

    public WindowTuple getWindowPrevious() {
        return windowPrevious;
    }

    public void setWindowPrevious(WindowTuple windowPrevious) {
        this.windowPrevious = windowPrevious;
    }

    public WindowTuple getWindowNext() {
        return windowNext;
    }

    public void setWindowNext(WindowTuple windowNext) {
        this.windowNext = windowNext;
    }

}
