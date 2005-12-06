package org.drools;

/*
 * $Id: DroolsTestCase.java,v 1.2 2005/12/01 05:50:47 mproctor Exp $
 *
 * Copyright 2003-2005 (C) The Werken Company. All Rights Reserved.
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

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

public abstract class DroolsTestCase extends TestCase
{
    public DroolsTestCase()
    {
        super( );
    }

    public DroolsTestCase(String name)
    {
        super( name );
    }

    public void assertLength(int len, Object[] array)
    {
        assertEquals( Arrays.asList( array ) + " does not have length of "
                      + len, len, array.length );
    }

    public void assertLength(int len, Collection collection)
    {
        assertEquals( collection + " does not have length of " + len, len,
                      collection.size( ) );
    }

    public void assertContains(Object obj, Object[] array)
    {
        for ( int i = 0; i < array.length; ++i )
        {
            if ( array[i] == obj )
            {
                return;
            }
        }

        fail( Arrays.asList( array ) + " does not contain " + obj );
    }

    public void assertContains(Object obj, Collection collection)
    {
        assertTrue( collection + " does not contain " + obj,
                    collection.contains( obj ) );
    }

}
