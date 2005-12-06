package org.drools.spi;

/*
 * $Id: Tuple.java,v 1.1 2005/07/26 01:06:32 mproctor Exp $
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
import org.drools.WorkingMemory;
import org.drools.rule.Declaration;

/**
 * A mapping of <code>Declaration</code> s to current fact values.
 *
 * @see Declaration
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public interface Tuple
{
    /**
     * Retrieve the value bound to a particular <code>Declaration</code>.
     *
     * @param declaration
     *            The <code>Declaration</code> key.
     *
     * @return The currently bound <code>Object</code> value.
     */
    Object get(int column);
  
    
    Object get(Declaration declaration);
    
    Object get(FactHandle handle); 

    /**
     * Retrieve the <code>FactHandle</code> for a given object.
     *
     * <p>
     * Within a consequence of a rule, if the desire is to retract or modify a
     * root fact this method provides a way to retrieve the
     * <code>FactHandle</code>. Facts that are <b>not </b> root fact objects
     * have no handle.
     * </p>
     *
     * @param object
     *            The object.
     *
     * @return The fact-handle or <code>null</code> if the supplied object is
     *         not a root fact object.
     */
    FactHandle getFactHandleForObject(Object object);

    /**
     * Returns a reference to the <code>WorkingMemory</code> associated with
     * this object.
     *
     * @return WorkingMemory
     */
    WorkingMemory getWorkingMemory();

    long getMostRecentFactTimeStamp();

    long getLeastRecentFactTimeStamp();

    // long getConditionTimeStamp(int i);
}
