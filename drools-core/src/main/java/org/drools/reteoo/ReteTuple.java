package org.drools.reteoo;

/*
 * $Id: ReteTuple.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
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

import org.drools.FactHandle;
import org.drools.NoSuchFactHandleException;
import org.drools.NoSuchFactObjectException;
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.spi.Tuple;

/**
 * Base Rete-OO <code>Tuple</code> implementation.
 * 
 * @see Tuple
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
class ReteTuple
    implements
    Tuple,
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private final WorkingMemoryImpl workingMemory;

    private final TupleKey          key;

    private FactHandleImpl          mostRecentFact;

    private FactHandleImpl          leastRecentFact;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    ReteTuple(ReteTuple left,
              ReteTuple right) {
        this.workingMemory = left.workingMemory;
        this.key = new TupleKey( left.key,
                                 right.key );
    }

    ReteTuple(int column,
              FactHandleImpl handle,
              WorkingMemoryImpl workingMemory) {
        this.workingMemory = workingMemory;
        this.key = new TupleKey( column,
                                 handle );
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < this.key.size(); i++) {
            buffer.append( this.key.get( i ) + " : " + get(i) + ", " );            
        }
        return buffer.toString();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the key for this tuple.
     * 
     * @return The key.
     */
    TupleKey getKey() {
        return this.key;
    }
    
    public FactHandle[] getFactHandles() {
        return this.key.getFactHandles();
    }

    /**
     * Determine if this tuple depends upon a specified object.
     * 
     * @param handle
     *            The object handle to test.
     * 
     * @return <code>true</code> if this tuple depends upon the specified
     *         object, otherwise <code>false</code>.
     */
    boolean dependsOn(FactHandle handle) {
        return this.key.containsFactHandle( handle );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * @see Tuple
     */
    public Object get(FactHandle handle) {
        return this.workingMemory.getObject( handle );
    }

    /**
     * @see Tuple
     */
    public Object get(int col) {
        FactHandle handle = this.key.get( col );
        if ( handle == null) {
            return null;
        }
        return get( handle );
    }

    public Object get(Declaration declaration) {
        return declaration.getValue( get( declaration.getColumn() ) );
    }

    /**
     * @see Tuple
     */
    public FactHandle getFactHandleForObject(Object object) {
        try {
            return this.workingMemory.getFactHandle( object );
        } catch ( NoSuchFactHandleException e ) {
            return null;
        }
    }
    
    public FactHandle getFactHandleForDeclaration(Declaration declaration) {
        return this.key.get( declaration.getColumn() );
    }    

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }
}
