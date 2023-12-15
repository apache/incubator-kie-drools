/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import org.drools.base.rule.Declaration;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.PropagationContext;
import org.kie.api.runtime.rule.FactHandle;

public abstract class AbstractTuple implements Tuple {
    private short  stagedType;

    private Object contextObject;

    protected InternalFactHandle handle;

    private PropagationContext propagationContext;

    protected Tuple stagedNext;
    protected Tuple stagedPrevious;

    private Tuple   previous;
    private AbstractTuple   next;

    private Sink sink;

    protected Tuple handlePrevious;
    protected Tuple handleNext;

    private boolean expired;

    public Object getObject(Declaration declaration) {
        return getObject(declaration.getTupleIndex());
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

    /**
     * This method is used by the consequence invoker (generated via asm by the ConsequenceGenerator)
     * to always pass to the consequence the original fact handle even in case when it has been
     * cloned and linked by a WindowNode
     */
    public InternalFactHandle getOriginalFactHandle() {
        InternalFactHandle linkedFH = handle.isEvent() ? ((DefaultEventHandle)handle).getLinkedFactHandle() : null;
        return linkedFH != null ? linkedFH : handle;
    }

    public void setFactHandle( FactHandle handle ) {
        this.handle = (InternalFactHandle) handle;
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

    public AbstractTuple getNext() {
        return next;
    }

    public void setNext(AbstractTuple next) {
        this.next = next;
    }

    @Override
    public void clear() {
        this.previous = null;
        this.next = null;
    }

    @Override
    public FactHandle get(Declaration declaration) {
        return get(declaration.getTupleIndex());
    }

    @Override
    public Tuple getTuple(int index) {
        Tuple entry = this;
        while ( entry.getIndex() != index) {
            entry = entry.getParent();
        }
        return entry;
    }

    @Override
    public Tuple getRootTuple() {
        return getTuple(0);
    }

    @Override
    public Tuple skipEmptyHandles() {
        // because getParent now only returns a tuple that as an FH, we only need to cheeck the current tuple,
        // and not the parent chain
        return getFactHandle() == null ? getParent() : this;
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

    public void setExpired() {
        this.expired = true;
    }

    protected Sink getSink() {
        return sink;
    }

    protected void setSink(Sink sink) {
        this.sink = sink;
    }
}
