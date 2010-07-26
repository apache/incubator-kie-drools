/**
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

package org.drools.reteoo;

import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;

public class InitialFactHandle extends DefaultFactHandle {
    /**
     *
     */
    private static final long  serialVersionUID = 400L;

    private InternalFactHandle delegate;

    private Object             object;

    public InitialFactHandle() {

    }

    public InitialFactHandle(final InternalFactHandle delegate) {
        super();
        this.delegate = delegate;
        this.object = InitialFactImpl.getInstance();
    }

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * @see Object
     */
    public boolean equals(final Object object) {
        return this.delegate.equals( object );
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return this.delegate.hashCode();
    }

    /**
     * @see Object
     */
    public String toString() {
        return toExternalForm();
    }

    public long getRecency() {
        return this.delegate.getRecency();
    }

    public void setRecency(final long recency) {
        this.delegate.setRecency( recency );
    }

    public int getId() {
        return this.delegate.getId();
    }

    public void invalidate() {
        this.delegate.invalidate();
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(final Object object) {
        // do nothign
    }

    public String toExternalForm() {
        return "InitialFact";
    }
    
    public InitialFactHandle clone() {
        return this;
    }

}
