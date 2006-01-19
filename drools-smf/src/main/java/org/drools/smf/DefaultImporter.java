package org.drools.smf;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.spi.ImportEntry;
import org.drools.spi.Importer;

/*
 * $Id: DefaultImporter.java,v 1.3 2005/05/07 04:39:30 dbarnett Exp $
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

public class DefaultImporter
    implements
    Importer
{
    private Set importEntrySet = Collections.EMPTY_SET;

    private Set importSet = Collections.EMPTY_SET;

    private Map cachedImports = Collections.EMPTY_MAP;

    public DefaultImporter()
    {
        super( );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.semantics.base.Importer#getImports()
     */
    public Set getImportEntries()
    {
        return this.importEntrySet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.semantics.base.Importer#getImports( Class clazz )
     */
    public Set getImports()
    {
        if (! importEntrySet.isEmpty( ) )
        {       
            if ( importSet == Collections.EMPTY_SET )
            {
                importSet = new HashSet( );
            }       

            Iterator i = this.importEntrySet.iterator( );
            while ( i.hasNext( ) )
            {
                importSet.add( ( (ImportEntry) i.next( ) ).getImportEntry() );               
            }
        }
        
        return importSet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.semantics.base.Importer#addImports(org.drools.spi.ImportEntry)
     */
    public void addImport(ImportEntry importEntry)
    {
        if ( this.importEntrySet == Collections.EMPTY_SET )
        {
            this.importEntrySet = new HashSet( );
        }
        this.importEntrySet.add( importEntry );
    }

    public Class lookupFromCache(String className)
    {
        if ( cachedImports == Collections.EMPTY_MAP )
        {
            return null;
        }

        return (Class) cachedImports.get( className );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.semantics.base.Importer#importClass(java.lang.ClassLoader,
     *      java.lang.String)
     */
    public Class importClass(ClassLoader cl,
                             String className) throws ClassNotFoundException
    {
        Class clazz = null;

        /* first try loading className */
        try
        {
            clazz = cl.loadClass( className );
        }
        catch ( ClassNotFoundException e )
        {
            clazz = null;
        }

        /* Now try the ruleset object type cache */
        if ( clazz == null )
        {
            clazz = lookupFromCache( className );
        }

        /* Now try the className with each of the given imports */
        if ( clazz == null )
        {
            Set validClazzCandidates = new HashSet( );

            Iterator it = importEntrySet.iterator( );
            while ( it.hasNext( ) )
            {
                clazz = importClass( cl,
                                     ((ImportEntry) it.next( )).getImportEntry( ),
                                     className.trim( ) );
                if ( clazz != null )
                {
                    validClazzCandidates.add( clazz );
                }
            }

            /*
             * If there are more than one possible resolutions, complain about
             * the ambiguity
             */
            if ( validClazzCandidates.size( ) > 1 )
            {
                StringBuffer sb = new StringBuffer( );
                Iterator clazzCandIter = validClazzCandidates.iterator( );
                while ( clazzCandIter.hasNext( ) )
                {
                    if ( 0 != sb.length( ) )
                    {
                        sb.append( ", " );
                    }
                    sb.append( ((Class) clazzCandIter.next( )).getName( ) );
                }
                throw new Error( "Unable to find unambiguously defined class '" + className + "', candidates are: [" + sb.toString( ) + "]" );
            }
            else if ( validClazzCandidates.size( ) == 1 )
            {
                clazz = (Class) validClazzCandidates.toArray( )[0];
            }
            else
            {
                clazz = null;
            }

        }

        /* We still can't find the class so throw an exception */
        if ( clazz == null )
        {
            throw new ClassNotFoundException( "Unable to find class '" + className + "'" );
        }

        return clazz;
    }

    private Class importClass(ClassLoader cl,
                              String importText,
                              String className)
    {
        String qualifiedClass = null;
        Class clazz = null;

        String convertedImportText;
        if ( importText.startsWith( "from " ) )
        {
            convertedImportText = convertFromPythonImport( importText );
        }
        else
        {
            convertedImportText = importText;
        }
        
        // not python
        if ( convertedImportText.endsWith( "*" ) )
        {
            qualifiedClass = convertedImportText.substring( 0,
                                                            convertedImportText.indexOf( '*' ) ) + className;
        }
        else if ( convertedImportText.endsWith( "." + className ) )
        {
            qualifiedClass = convertedImportText;
        }
        else if ( convertedImportText.equals( className ) )
        {
            qualifiedClass = convertedImportText;
        }

        if ( qualifiedClass != null )
        {
            try
            {
                clazz = cl.loadClass( qualifiedClass );
            }
            catch ( ClassNotFoundException e )
            {
                clazz = null;
            }
        }

        if ( clazz != null )
        {
            if ( this.cachedImports == Collections.EMPTY_MAP  )
            {
                this.cachedImports = new HashMap( );
            }

            this.cachedImports.put( className,
                                    clazz );
        }

        return clazz;
    }
    
    private String convertFromPythonImport(String packageText)
    {
        String fromString = "from ";
        String importString = "import ";
        int fromIndex = packageText.indexOf( fromString );
        int importIndex = packageText.indexOf( importString );
        return packageText.substring( fromIndex + fromString.length( ),
                                      importIndex ).trim( ) + "." + packageText.substring( importIndex + importString.length( ) ).trim( );
    }    

    public boolean isEmpty()
    {
        return this.importEntrySet.isEmpty( );
    }
}
