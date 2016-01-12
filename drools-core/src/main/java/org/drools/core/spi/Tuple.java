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

package org.drools.core.spi;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.NetworkNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.Sink;
import org.drools.core.rule.Declaration;
import org.drools.core.util.Entry;
import org.drools.core.util.index.TupleList;

import java.io.Serializable;

/**
 * Partial matches are propagated through the Rete network as <code>Tuple</code>s. Each <code>Tuple</code>
 * Is able to return the <code>FactHandleImpl</code> members of the partial match for the requested pattern.
 * The pattern refers to the index position of the <code>FactHandleImpl</code> in the underlying implementation.
 * 
 * @see org.drools.core.marshalling.impl.ProtobufMessages.FactHandle
 */
public interface Tuple extends Serializable, Entry<Tuple> {

    short NONE   = 0;
    short INSERT = 1;
    short UPDATE = 2;
    short DELETE = 3;
    short NORMALIZED_DELETE = 4;

    Object getObject(int pattern);

    Object getObject(Declaration declaration);

    Object[] toObjects();

    /**
     * Returns the <code>FactHandle</code> for the given pattern index. If the pattern is empty
     * It returns null.
     *
     * @param pattern
     *      The index of the pattern from which the <code>FactHandleImpl</code> is to be returned
     * @return
     *      The <code>FactHandle</code>
     */
    InternalFactHandle get(int pattern);

    /**
     * Returns the <code>FactHandle</code> for the given <code>Declaration</code>, which in turn
     * specifcy the <code>Pattern</code> that they depend on.
     *
     * @param declaration
     *      The <code>Declaration</code> which specifies the <code>Pattern</code>
     * @return
     *      The <code>FactHandle</code>
     */
    InternalFactHandle get(Declaration declaration);

    /**
     * Returns the fact handles in reverse order
     */
    InternalFactHandle[] toFactHandles();

    /**
     * Returns the size of this tuple in number of elements (patterns)
     * @return
     */
    int size();

    int getIndex();

    Tuple getParent();

    InternalFactHandle getFactHandle();
    void setFactHandle( InternalFactHandle handle );

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

    void increaseActivationCountForEvents();
    void decreaseActivationCountForEvents();

    Tuple getRootTuple();
    Tuple skipEmptyHandles();

    LeftTuple getFirstChild();
    void setFirstChild( LeftTuple firstChild );

    LeftTuple getLastChild();
    void setLastChild( LeftTuple firstChild );

    <T extends Tuple> T getHandlePrevious();
    void setHandlePrevious( Tuple leftParentLeft );

    <T extends Tuple> T getHandleNext();
    void setHandleNext( Tuple leftParentright );

    ObjectTypeNode.Id getInputOtnId();

    <N extends NetworkNode> N getTupleSource();

    void modifyTuple( PropagationContext context, InternalWorkingMemory workingMemory );
    void retractTuple( PropagationContext context, InternalWorkingMemory workingMemory );

    boolean isExpired();
    void setExpired( boolean expired );
}
