package org.drools.reteoo;

/*
 * $Id: FactHandleImpl.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
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

import org.drools.FactHandle;

/**
 * Implementation of <code>FactHandle</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public class FactHandleImpl
    implements
    FactHandle {
    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /** Handle id. */
    private long       id;
    private long recency;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    protected FactHandleImpl(long id) {
        this.id = id;
        this.recency = id;
    }

    /**
     * Construct.
     * 
     * @param id
     *            Handle id.
     */
    protected FactHandleImpl(long id,
                             long recency) {
        this.id = id;
        this.recency = recency;
    }

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    /**
     * @see Object
     */
    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof FactHandle) ) {
            return false;
        }

        return this.id == ((FactHandleImpl) object).id;
    }

    /**
     * @see Object
     */
    public int hashCode() {
        return (int) this.id;
    }

    /**
     * @see FactHandle
     */
    public String toExternalForm() {
        return "[fid:" + this.id + ":" + this.recency + "]";
    }

    /**
     * @see Object
     */
    public String toString() {
        return toExternalForm();
    }

    public long getRecency() {
        return this.recency;
    }
    
    public void setRecency(long recency) {
        this.recency = recency;
    }    

    public long getId() {
        return this.id;
    }

    void invalidate() {
        this.id = -1;
    }

}
