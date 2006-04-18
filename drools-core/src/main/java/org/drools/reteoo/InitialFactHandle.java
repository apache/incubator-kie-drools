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



public class InitialFactHandle extends FactHandleImpl {
    private final FactHandleImpl delegate;

    private Object               object;

    public InitialFactHandle(FactHandleImpl delegate) {
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
    public boolean equals(Object object) {
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

    public void setRecency(long recency) {
        this.delegate.setRecency( recency );
    }

    public long getId() {
        return this.delegate.getId();
    }

    void invalidate() {
        this.delegate.invalidate();
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        // do nothign
    }

    public String toExternalForm() {
        return "InitialFact";
    }

}