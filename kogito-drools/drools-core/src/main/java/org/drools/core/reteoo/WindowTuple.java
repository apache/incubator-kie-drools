/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
