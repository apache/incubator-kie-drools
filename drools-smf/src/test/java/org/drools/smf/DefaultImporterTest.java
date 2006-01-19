package org.drools.smf;

import java.util.Set;

import org.drools.spi.ImportEntry;
import org.drools.spi.Importer;

import junit.framework.TestCase;

/*
 * $Id: DefaultImporterTest.java,v 1.2 2005/05/04 16:58:40 memelet Exp $
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

public class DefaultImporterTest extends TestCase
{

    public void testStaticImport()
    {
        Importer importer = new DefaultImporter( );
        importer.addImport( new DefaultImportEntry( "java.util.HashMap" ) );

        /* static import, should work */
        ClassLoader cl = getClass( ).getClassLoader( );
        try
        {
            Class clazz = importer.importClass( cl,
                                                "HashMap" );
            assertSame( java.util.HashMap.class,
                        clazz );
        }
        catch ( ClassNotFoundException e )
        {
            fail( e.getMessage( ) );
        }
        catch ( Error e )
        {
            fail( e.getMessage( ) );
        }

        /* static import, should fail */
        try
        {
            Class clazz = importer.importClass( cl,
                                                "ArrayList" );
            fail( "Class ArrayList should not be found" );
        }
        catch ( ClassNotFoundException e )
        {
            // correct exception, so pass
        }
        catch ( Error e )
        {
            fail( "Incorrect Error exception, should have been ClassNotFound" );
        }
    }

    public void testDynamicImport()
    {
        Importer importer = new DefaultImporter( );
        importer.addImport( new DefaultImportEntry( "java.util.*" ) );

        ClassLoader cl = getClass( ).getClassLoader( );

        /* dynamic import, should work */
        try
        {
            Class clazz = importer.importClass( cl,
                                                "HashMap" );
            assertSame( java.util.HashMap.class,
                        clazz );
        }
        catch ( ClassNotFoundException e )
        {
            fail( e.getMessage( ) );
        }
        catch ( Error e )
        {
            fail( e.getMessage( ) );
        }

        /* dynamic import, should throw ClassNotFoundException */
        try
        {
            Class clazz = importer.importClass( cl,
                                                "NoneExistingClass" );
            fail( "Class NoneExistingClass should not be found" );
        }
        catch ( ClassNotFoundException e )
        {
            // correct exception, so pass
        }
        catch ( Error e )
        {
            fail( "Incorrect Error exception, should have been ClassNotFound" );
        }

        /* dynamic import, should fail and throw Error */
        importer.addImport( new DefaultImportEntry( "java.awt.*" ) );
        try
        {
            Class clazz = importer.importClass( cl,
                                                "List" );
            fail( "Should fail as imports are ambiguous for List" );
        }
        catch ( ClassNotFoundException e )
        {
            fail( "Incorrect ClassNotFoundException exception, should have been Error" );
        }
        catch ( Error e )
        {
            // correct exception, so pass
        }

        /* recheck that HashMap still works */
        try
        {
            Class clazz = importer.importClass( cl,
                                                "HashMap" );
            assertSame( java.util.HashMap.class,
                        clazz );
        }
        catch ( ClassNotFoundException e )
        {
            fail( e.getMessage( ) );
        }
        catch ( Error e )
        {
            fail( e.getMessage( ) );
        }

    }

    public void testgetImports()
    {
        Importer importer = new DefaultImporter( );

        // assertSame(importer.)

        importer.addImport( new DefaultImportEntry( "java.awt.*" ) );
        importer.addImport( new DefaultImportEntry( "java.util.Map" ) );

        Set set;
        set = importer.getImports( );
        assertTrue( set.contains( "java.awt.*" ) );
        assertTrue( set.contains( "java.util.Map" ) );
        assertEquals( 2,
                      set.size( ) );
    }

    private class DefaultImportEntry
        implements
        ImportEntry
    {
        private String importEntry;

        public DefaultImportEntry(String importEntry)
        {
            this.importEntry = importEntry;
        }

        public String getImportEntry()
        {
            return this.importEntry;
        }
    }
}
