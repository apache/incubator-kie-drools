package org.drools;

/*
 * $Id: NoSuchFactObjectException.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 *
 * Copyright 2001-2004 (C) The Werken Company. All Rights Reserved.
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
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
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

/**
 * Indicates an attempt to retract, modify or retrieve a fact object that is no
 * longer present.
 *
 * @see FactHandle
 * @see WorkingMemory#assertObject
 * @see WorkingMemory#retractObject
 * @see WorkingMemory#getObject
 *
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 *
 * @version $Id: NoSuchFactObjectException.java,v 1.3 2003/11/19 21:31:09 bob
 *          Exp $
 */
public class NoSuchFactObjectException extends RuntimeException
{
    /** Invalid fact handle. */
    private final FactHandle handle;

    /**
     * @see java.lang.Exception#Exception()
     *
     * @param object
     *            The invalid fact object.
     */
    public NoSuchFactObjectException( FactHandle handle )
    {
        super( createMessage( handle ) );
        this.handle = handle;
    }

    /**
     * @see java.lang.Exception#Exception()
     *
     * @param object
     *            The invalid fact object.
     */
    public NoSuchFactObjectException( FactHandle handle, Throwable cause )
    {
        super( createMessage( handle ) );
        this.handle = handle;
    }

    /**
     * Retrieve the invalid <code>FactHandle</code>.
     *
     * @return The invalid fact handle.
     */
    public FactHandle getFactHandle( )
    {
        return this.handle;
    }

    private static String createMessage( FactHandle handle ) {
        return handle == null
                ? "null fact object"
                : "no such fact object for handle:" + handle.toExternalForm( );
    }
}
