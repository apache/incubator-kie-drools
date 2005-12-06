package org.drools.reteoo;

/*
 * $Id: BetaMemory.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Memory for left and right inputs of a <code>JoinNode</code>.
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 * @see ReteTuple
 */
class BetaMemory
    implements
    Serializable
{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Left-side tuples. */
    /* @todo: make a BetaSet to wrap the Map */
    private final Map leftMemory;

    /** Right-side tuples. */
    private final Set rightMemory;

    // private final BetaNode betaNode;

    // private final BetaNodeBinder binder;

    // private final int column;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param tupleDeclarations
     * @param commonDeclarations
     */
    BetaMemory()
    {
        // this.column = column;
        // this.betaNode = betaNode;
        this.leftMemory = new HashMap( );
        this.rightMemory = new HashSet( );
        // this.binder = joinNodeBinder;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * This method is here to facilitate unit testing.
     */
    TupleMatches getBetaMemory(TupleKey key)
    {
        return (TupleMatches) this.leftMemory.get( key );
    }

    /*
     * BetaNodeBinder getBetaNodeBinder() { return this.binder; }
     * 
     * int getColumn() { return this.column; }
     */

    Map getLeftMemory()
    {
        return this.leftMemory;
    }

    Set getRightMemory()
    {
        return this.rightMemory;
    }

    boolean contains(TupleKey key)
    {
        return this.leftMemory.containsKey( key );
    }

    boolean contains(FactHandleImpl handle)
    {
        return this.rightMemory.contains( handle );
    }

    void put(TupleKey key,
             TupleMatches tupleMatches)
    {
        this.leftMemory.put( key,
                             tupleMatches );
    }
    
    Object remove(TupleKey key)
    {
        return this.leftMemory.remove( key );
    }

    void add(FactHandleImpl handle)
    {
        this.rightMemory.add( handle );
    }
    
    boolean remove(FactHandleImpl handle)
    {
        return this.rightMemory.remove( handle );
    }

    int leftMemorySize()
    {
        return this.leftMemory.size( );
    }

    int rightMemorySize()
    {
        return this.rightMemory.size( );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // java.lang.Object
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Produce debug string.
     * 
     * @return The debug string.
     */
    public String toString()
    {
        return "[JoinMemory \n\tleft=" + this.leftMemory + "\n\tright=" + this.rightMemory + "]";
    }

}
