package org.drools.reteoo;

/*
 * $Id: TestNode.java,v 1.3 2005/08/14 22:44:12 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.AssertionException;
import org.drools.FactException;
import org.drools.RetractionException;
import org.drools.spi.Condition;
import org.drools.spi.PropagationContext;

/**
 * Node which filters <code>ReteTuple</code>s.
 * 
 * <p>
 * Using a semantic <code>Test</code>, this node may allow or disallow
 * <code>Tuples</code> to proceed further through the Rete-OO network.
 * </p>
 * 
 * @see TestNode
 * @see Condition
 * @see ReteTuple
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
class TestNode extends TupleSource
    implements
    TupleSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The semantic <code>Test</code>. */
    private final Condition   condition;

    /** The source of incoming <code>Tuples</code>. */
    private final TupleSource tupleSource;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param rule
     *            The rule
     * @param tupleSource
     *            The source of incoming <code>Tuples</code>.
     * @param condition
     */
    TestNode(int id,
             TupleSource tupleSource,
             Condition condition,
             boolean hasMemory) {
        super( id );
        this.condition = condition;
        this.tupleSource = tupleSource;
        this.hasMemory = hasMemory;
    }

    /**
     * Attaches this node into the network.
     */
    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the <code>Test</code> associated with this node.
     * 
     * @return The <code>Test</code>.
     */
    public Condition getCondition() {
        return this.condition;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.reteoo.impl.TupleSink
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Assert a new <code>Tuple</code>.
     * 
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     * 
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) throws FactException {

        if ( hasMemory() ) {
            Map memory = (Map) workingMemory.getNodeMemory( this );
            if ( !memory.containsKey( tuple.getKey() ) ) {
                boolean allowed = this.condition.isAllowed( tuple );

                workingMemory.getReteooNodeEventSupport().propagateReteooNode( this,
                                                                               tuple,
                                                                               allowed );

                if ( allowed ) {
                    memory.put( tuple.getKey(),
                                tuple );
                    propagateAssertTuple( tuple,
                                          context,
                                          workingMemory );
                }
            }
        } else {
            boolean allowed = this.condition.isAllowed( tuple );

            workingMemory.getReteooNodeEventSupport().propagateReteooNode( this,
                                                                           tuple,
                                                                           allowed );

            if ( allowed ) {
                propagateAssertTuple( tuple,
                                      context,
                                      workingMemory );
            }
        }
    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) throws FactException {
        this.attachingNewNode = true;
        if ( hasMemory() ) {
            Map memory = (Map) workingMemory.getNodeMemory( this );
            for ( Iterator it = memory.values().iterator(); it.hasNext(); ) {
                propagateAssertTuple( (ReteTuple) it.next(),
                                      context,
                                      workingMemory );
            }
        } else {
            // We need to detach and re-attach to make sure the node is at the
            // top
            // for the propagation
            this.tupleSource.removeTupleSink( this );
            this.tupleSource.addTupleSink( this );
            this.tupleSource.updateNewNode( workingMemory,
                                            context );
        }

        this.attachingNewNode = false;
    }

    /**
     * Retract tuples.
     * 
     * @param key
     *            The tuple key.
     * @param workingMemory
     *            The working memory seesion.
     * 
     * @throws RetractionException
     *             If an error occurs while retracting.
     */
    public void retractTuples(TupleKey key,
                              PropagationContext context,
                              WorkingMemoryImpl workingMemory) throws FactException {
        if ( hasMemory() ) {
            Map memory = (Map) workingMemory.getNodeMemory( this );
            if ( memory.remove( key ) != null ) {

                propagateRetractTuples( key,
                                        context,
                                        workingMemory );
            }
        } else {
            propagateRetractTuples( key,
                                    context,
                                    workingMemory );
        }

    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // java.lang.Object
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Produce a debug string.
     * 
     * @return The debug string.
     */
    public String toString() {
        return "[ConditionNode: cond=" + this.condition + "]";
    }

    public int hashCode() {
        return this.tupleSource.hashCode() ^ this.condition.hashCode();
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || getClass() != object.getClass() ) {
            return false;
        }

        TestNode other = (TestNode) object;

        return this.tupleSource.equals( other.tupleSource ) && this.condition.equals( other.condition );
    }

    public void remove() {
        // TODO Auto-generated method stub

    }

    public Object createMemory() {
        return new HashMap();
    }
}
