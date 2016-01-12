/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.drools.core.reteoo;

import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;

public abstract class BaseTuple implements Tuple {
    private short  stagedType;

    private Object contextObject;

    private InternalFactHandle handle;

    private PropagationContext propagationContext;

    protected Tuple stagedNext;
    protected Tuple stagedPrevious;

    private Tuple   previous;
    private Tuple   next;

    protected Sink sink;

    protected Tuple handlePrevious;
    protected Tuple handleNext;

    private boolean expired;

    public Object getObject(Declaration declaration) {
        return getObject(declaration.getPattern().getOffset());
    }

    public Object getContextObject() {
        return this.contextObject;
    }

    public final void setContextObject( final Object contextObject ) {
        this.contextObject = contextObject;
    }

    public short getStagedType() {
        return stagedType;
    }

    public void setStagedType(short stagedType) {
        this.stagedType = stagedType;
    }

    public void clearStaged() {
        this.stagedType = LeftTuple.NONE;
        this.stagedNext = null;
        this.stagedPrevious = null;
    }

    public InternalFactHandle getFactHandle() {
        return handle;
    }

    public void setFactHandle( InternalFactHandle handle ) {
        this.handle = handle;
    }

    public PropagationContext getPropagationContext() {
        return propagationContext;
    }

    public void setPropagationContext(PropagationContext propagationContext) {
        this.propagationContext = propagationContext;
    }

    public void setStagedNext(Tuple stageNext) {
        this.stagedNext = stageNext;
    }

    public void setStagedPrevious( Tuple stagedPrevious ) {
        this.stagedPrevious = stagedPrevious;
    }

    public Tuple getPrevious() {
        return previous;
    }

    public void setPrevious(Tuple previous) {
        this.previous = previous;
    }

    public Tuple getNext() {
        return next;
    }

    public void setNext(Tuple next) {
        this.next = next;
    }

    @Override
    public void clear() {
        this.previous = null;
        this.next = null;
    }

    @Override
    public InternalFactHandle get( Declaration declaration ) {
        return get(declaration.getPattern().getOffset());
    }

    @Override
    public void increaseActivationCountForEvents() {
        for ( Tuple entry = this; entry != null; entry = entry.getParent() ) {
            if(entry.getFactHandle() != null &&  entry.getFactHandle().isEvent() ) {
                // can be null for eval, not and exists that have no right input
                ((EventFactHandle)entry.getFactHandle()).increaseActivationsCount();
            }
        }
    }

    @Override
    public void decreaseActivationCountForEvents() {
        for ( Tuple entry = this; entry != null; entry = entry.getParent() ) {
            if( entry.getFactHandle() != null &&  entry.getFactHandle().isEvent() ) {
                // can be null for eval, not and exists that have no right input
                ((EventFactHandle)entry.getFactHandle()).decreaseActivationsCount();
            }
        }
    }

    @Override
    public Tuple getRootTuple() {
        if ( getParent() == null ) {
            return this;
        }

        Tuple currentLt = getParent();
        while (currentLt.getParent() != null ) {
            currentLt = currentLt.getParent();
        }
        return currentLt;
    }

    @Override
    public Tuple skipEmptyHandles() {
        Tuple entry = this;
        while ( entry != null && entry.getFactHandle() == null ) {
            entry = entry.getParent();
        }
        return entry;
    }

    @Override
    public Tuple getHandlePrevious() {
        return handlePrevious;
    }

    @Override
    public void setHandlePrevious(Tuple handlePrevious) {
        this.handlePrevious = handlePrevious;
    }

    @Override
    public Tuple getHandleNext() {
        return handleNext;
    }

    @Override
    public void setHandleNext(Tuple handleNext) {
        this.handleNext = handleNext;
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public void setExpired( boolean expired ) {
        this.expired = expired;
    }
}
