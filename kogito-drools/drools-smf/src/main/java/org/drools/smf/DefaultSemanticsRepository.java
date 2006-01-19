package org.drools.smf;

/*
 * $Id: DefaultSemanticsRepository.java,v 1.12 2005/09/25 11:45:38 michaelneale Exp $
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Default <code>SemanticsRepository</code> which uses discovery to autoload
 * semantic modules.
 * 
 * <p>
 * Any <b>semantic module </b> that conforms to the SMF contract will be
 * discovered and autoloaded upon first access of the
 * <code>DefaultSemanticsRepository</code>.
 * </p>
 * 
 * <p>
 * To be discovered, the module should be in a jar with a module descriptor
 * located at <code>/META-INF/drools-semantics.properties</code>.
 * </p>
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: DefaultSemanticsRepository.java,v 1.4 2004/06/22 17:17:27 bob
 *          Exp $
 */
public final class DefaultSemanticsRepository
    implements
    SemanticsRepository
{
    // ----------------------------------------------------------------------
    // Class members
    // ----------------------------------------------------------------------

    /** Singleton instance, lazily initialized. */
    private static SemanticsRepository INSTANCE;


    private ClassLoaderHelper          clHelper;

    // ----------------------------------------------------------------------
    // Class methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     * 
     * @throws Exception
     *             If an error occurs while performing discovery and loading of
     *             the semantic modules.
     */
    public static synchronized SemanticsRepository getInstance() throws IOException,
                                                                SemanticsReaderException
    {
        if ( INSTANCE == null )
        {
            INSTANCE = new DefaultSemanticsRepository( );
        }

        return INSTANCE;
    }

    // ----------------------------------------------------------------------
    // Instance members
    // ----------------------------------------------------------------------

    private Set                       loadedSemantics;

    /** Module repository. */
    private SimpleSemanticsRepository repository;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    /**
     * Construct.
     * 
     * @throws Exception
     *             If an error occurs while performing discovery and loading of
     *             the semantic modules.
     */
    private DefaultSemanticsRepository() throws IOException,
                                        SemanticsReaderException
    {
        this.loadedSemantics = new HashSet( );
        this.repository = new SimpleSemanticsRepository( );
        init( );
    }

    // ----------------------------------------------------------------------
    // Instance methods
    // ----------------------------------------------------------------------

    /**
     * Initialize and perform discovery.
     * 
     * @throws Exception
     *             If an error occurs while performing discovery and loading of
     *             the semantic modules.
     */
    protected void init() throws IOException,
                         SemanticsReaderException
    {


        ClassLoaderHelper clh = new CurrentThreadClassLoaderHelper( );

        if ( clh.getClassLoader( ) == null )
        {
            clh = new CurrentClassLoaderHelper( );
        }

        Enumeration configUrls = clh.getClassLoader( ).getResources( "META-INF/drools.conf" );

        if ( !configUrls.hasMoreElements( ) )
        {
            clh = new CurrentClassLoaderHelper( );
            configUrls = clh.getClassLoader( ).getResources( "META-INF/drools.conf" );
        }

        if ( !configUrls.hasMoreElements( ) )
        {
            clh = new SystemClassLoaderHelper( );
            configUrls = clh.getClassLoader( ).getResources( "META-INF/drools.conf" );
        }

        this.clHelper = clh;
        while ( configUrls.hasMoreElements( ) )
        {
            URL configUrl = (URL) configUrls.nextElement( );
            loadConfig( configUrl );
        }
        
        String droolsConfigProp = System.getProperty( "drools.conf" );

        if ( droolsConfigProp != null )
        {
            loadConfig( droolsConfigProp );
        }        

    }

    protected void loadConfig(String path) throws IOException,
                                          SemanticsReaderException
    {
        URL url = this.clHelper.getClassLoader( ).getResource( path );

        if ( url == null )
        {
            System.err.println( "INVALID PATH: [" + path + "]" );
            return;
        }

        loadConfig( url );
    }

    protected void loadConfig(URL url) throws IOException,
                                      SemanticsReaderException
    {
        InputStream config = url.openStream( );

        BufferedReader in = new BufferedReader( new InputStreamReader( config ) );

        try
        {
            String line;

            while ( (line = in.readLine( )) != null )
            {
                line = line.trim( );

                if ( line.equals( "" ) || line.startsWith( "#" ) )
                {
                    continue;
                }

                loadSemantics( line );
            }
        }
        finally
        {
            in.close( );
        }
    }

    protected void loadSemantics(String semanticsName) throws IOException,
                                                      SemanticsReaderException
    {
        if ( this.loadedSemantics.contains( semanticsName ) )
        {
            return;
        }

        this.loadedSemantics.add( semanticsName );

        String semanticsFile = "META-INF/" + semanticsName + ".conf";

        URL descriptor = this.clHelper.getClassLoader( ).getResource( semanticsFile );

        if ( descriptor == null )
        {
            System.err.println( "cannot load " + semanticsFile );
            return;
        }

        SemanticsReader semanticsReader = new SemanticsReader( );

        SemanticModule module = semanticsReader.read( descriptor,
                                                      this.clHelper.getClassLoader( ) );

        this.repository.registerSemanticModule( module );
    }

    /**
     * @see SemanticsRepository
     */
    public SemanticModule lookupSemanticModule(String uri) throws NoSuchSemanticModuleException
    {
        return this.repository.lookupSemanticModule( uri );
    }

    /**
     * @see SemanticsRepository
     */
    public SemanticModule[] getSemanticModules()
    {
        return this.repository.getSemanticModules( );
    }

    /**
     * Gets the classloader that was chosen when the repository was initialised.
     * Each time this is called, it will get a valid classloader. This does not
     * cache the classloader, so as to support "hot deployment"
     * environments/containers.
     */
    public ClassLoader getSemanticModuleClassLoader()
    {
        return this.clHelper.getClassLoader( );
    }

    
    /**
     * The ClassLoaderHelper is used to preserve the initialisation logic of choosing the class loader.
     * The initialisation can still only happen once, but the classloaders will be fresh.
     */
    static interface ClassLoaderHelper
    {
        ClassLoader getClassLoader();
    }

    static class CurrentThreadClassLoaderHelper
        implements
        ClassLoaderHelper
    {

        public ClassLoader getClassLoader()
        {
            return Thread.currentThread( ).getContextClassLoader( );
        }
    }

    static class CurrentClassLoaderHelper
        implements
        ClassLoaderHelper
    {

        public ClassLoader getClassLoader()
        {
            return getClass( ).getClassLoader( );
        }
    }

    static class SystemClassLoaderHelper 
        implements
        ClassLoaderHelper
    {

        public ClassLoader getClassLoader()
        {
            return ClassLoader.getSystemClassLoader( );
        }
    }



}