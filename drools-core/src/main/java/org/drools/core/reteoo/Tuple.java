/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

import org.drools.base.reteoo.BaseTuple;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.common.NetworkNode;
import org.drools.core.common.PropagationContext;
import org.drools.core.util.Entry;
import org.drools.core.util.index.TupleList;
import org.kie.api.runtime.rule.FactHandle;

import java.io.Serializable;

/**
 * Partial matches are propagated through the Rete network as <code>Tuple</code>s. Each <code>Tuple</code>
 * Is able to return the <code>FactHandleImpl</code> members of the partial match for the requested pattern.
 * The pattern refers to the index position of the <code>FactHandleImpl</code> in the underlying implementation.
 */
public interface Tuple extends BaseTuple, Serializable, Entry<AbstractTuple> {

    short NONE   = 0;
    short INSERT = 1;
    short UPDATE = 2;
    short DELETE = 3;
    short NORMALIZED_DELETE = 4;

    @Override
    default Object[] toObjects() {
        return toObjects(false);
    }

    void setFactHandle( FactHandle handle);

    InternalFactHandle getOriginalFactHandle();

    /**
     * Returns the ReteTuple that contains the "elements"
     * first elements in this tuple.
     * <p/>
     * Use carefully as no cloning is made during this process.
     * <p/>
     * This method is used by TupleStartEqualsConstraint when
     * joining a subnetwork tuple into the main network tuple;
     *
     * @param elements the number of elements to return, starting from
     *                 the begining of the tuple
     * @return a ReteTuple containing the "elements" first elements
     *         of this tuple or null if "elements" is greater than size;
     */
    Tuple getSubTuple(final int elements);

    Object getContextObject();
    void setContextObject( final Object object );

    short getStagedType();
    void setStagedType(short stagedType);

    default boolean isDeleted() {
        return getStagedType() == DELETE || getStagedType() == NORMALIZED_DELETE;
    }

    Tuple getStagedPrevious();
    void setStagedPrevious( Tuple stagePrevious );

    <T extends Tuple> T getStagedNext();
    void setStagedNext( Tuple stageNext );

    void clear();
    void clearStaged();

    void reAdd();

    void unlinkFromRightParent();

    void unlinkFromLeftParent();

    PropagationContext getPropagationContext();

    void setPropagationContext( PropagationContext propagationContext );

    Tuple getPrevious();

    void setPrevious( Tuple previous );

    <S extends Sink> S getTupleSink();

    TupleList getMemory();

    void setMemory( TupleList memory );

    Tuple getRootTuple();

    Tuple skipEmptyHandles();

    LeftTuple getFirstChild();

    void setFirstChild( LeftTuple firstChild );

    LeftTuple getLastChild();

    void setLastChild( LeftTuple firstChild );

    <T extends Tuple> T getHandlePrevious();

    Tuple getParent();

    void setHandlePrevious( Tuple leftParentLeft );

    <T extends Tuple> T getHandleNext();

    void setHandleNext( Tuple leftParentright );

    ObjectTypeNode.Id getInputOtnId();

    <N extends NetworkNode> N getTupleSource();

    boolean isExpired();

    default PropagationContext findMostRecentPropagationContext() {
        // Find the most recent PropagationContext, as this caused this rule to elegible for firing
        PropagationContext mostRecentContext = getPropagationContext();
        for ( Tuple lt = getParent(); lt != null; lt = lt.getParent() ) {
            PropagationContext currentContext = lt.getPropagationContext();
            if ( currentContext != null && currentContext.getPropagationNumber() > mostRecentContext.getPropagationNumber() ) {
                mostRecentContext = currentContext;
            }
        }
        return mostRecentContext;
    }
}
