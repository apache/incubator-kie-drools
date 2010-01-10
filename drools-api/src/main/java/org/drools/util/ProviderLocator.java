/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.drools.ProviderInitializationException;

/**
 * Provides mechanisms for loading service providers dynamically.
 * <p/>
 * Provider factories may extend this class to provide a consistent
 * lookup mechanism. For example, KnowledgeBaseFactory creates
 * a KonwledgeBase using a service provider that implements the
 * KnowlwdgeProvider interface.
 * <pre>
 * public class KnowledgeBaseFactory extends ProviderLocator {
 *     private static KnowledgeBaseProvider provider;
 * 
 *     public static KnowledgeBase newKnowledgeBase() {
 *         return getKnowledgeBaseProvider().newKnowledgeBase();
 *     }
 * 
 *     private static synchronized KnowledgeBaseProvider getKnowledgeBaseProvider() {
 *         if ( provider == null ) {
 *             provider = newProviderFor( KnowledgeBaseProvider.class );
 *         }
 *         return provider;
 *     }
 * }
 * </pre>
 * <tt>getKnowledgeBaseProvider()</tt> simply calls a method on this class
 * to instantiate a provider for <tt>KnowledgeBaseProvider</tt>.
 * <p/>
 * This is an adaptation of the technique described by Guillaume Nodet
 * in his article<i>
 * <a href='http://gnodet.blogspot.com/2008/05/jee-specs-in-osgi.html'>
 * Java EE specs in OSGi</a></i>, which is an adaptation of the
 * FactoryLocator class in <a href='http://stax.codehaus.org/'>StAX</a>.
 * The main changes were to use the existing Drools's lookup strategy
 * for properties, add comments and error handling, and properly
 * parse a Service Configuration File.
 *
 * @author Guillaume Nodet
 * @author Faron Dutton
 * @see {@linkplain http://gnodet.blogspot.com/2008/05/jee-specs-in-osgi.html}
 * @see {@linkplain http://svn.apache.org/repos/asf/servicemix/smx4/specs/trunk/stax-api-1.0/src/main/java/javax/xml/stream/FactoryLocator.java}
 */
public abstract class ProviderLocator {

    // TODO: May need to change the name of the properties file.
    private static final String CONFIG_FILE = "services.conf";

    /**
     * Locates and instantiates a new provider for the given service.
     * <p/>
     * This is a convenience method equivalent to calling
     * {@link #newProviderFor(Class, ClassLoader) newProviderFor(serviceClass, null)}.
     * 
     * @param <T>
     *          Either an interface or a (usually abstract) class
     *          defining a service as described in the JAR File
     *          Specification.
     * @param serviceClass
     *          The Class for <tt>T</tt>. Used to cast the new object
     *          to <tt>T</tt>. It is important that this Class be
     *          visible from the given ClassLoader (if provided).
     * @return A new instance of the service provider.
     * @throws ProviderInitializationException
     *          If a provider cannot be found, cannot be instantiated,
     *          is not accessible, or does not implement (extend) the
     *          interface (class) <tt>T</tt>.
     * @see {@linkplain http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider}
     */
    protected static final <T> T newProviderFor(final Class<T> serviceClass) throws ProviderInitializationException {
        return newProviderFor( serviceClass,
                               (ClassLoader) null );
    }

    /**
     * Locates and instantiates a new provider for the given service.
     * <p/>
     * This method uses the following strategy to locate a provider:
     * <ol>
     * <li>If running in an OSGi container then use the provider from the client bundle</li>
     * <li>Use the strategy from {@link org.drools.util.ChainedProperties ChainedProperties}</li>
     * <li>Use the Service API from the JAR File Specification</li>
     * </ol>
     * 
     * @param <T>
     *          Either an interface or a (usually abstract) class
     *          defining a service as described in the JAR File
     *          Specification.
     * @param serviceClass
     *          The Class for <tt>T</tt>. Used to cast the new object
     *          to <tt>T</tt>. It is important that this Class be
     *          visible from the given ClassLoader (if provided).
     * @param classLoader
     *          An optional ClassLoader used to load and instantiate
     *          the service provider. May be <tt>null</tt>.
     * @return A new instance of the service provider.
     * @throws ProviderInitializationException
     *          If a provider cannot be found, cannot be instantiated,
     *          is not accessible, or does not implement (extend) the
     *          interface (class) <tt>T</tt>.
     * @see {@linkplain http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider}
     */
    protected static final <T> T newProviderFor(final Class<T> serviceClass,
                                                final ClassLoader classLoader) throws ProviderInitializationException {
        // If we are deployed into an OSGi environment, leverage it
        Class< ? > providerClass = OSGiLocator.locate( serviceClass.getName() );
        if ( providerClass != null ) {
            return newInstance( serviceClass,
                                providerClass );
        }

        // Look for a definition in the Drools config files.
        ChainedProperties props = new ChainedProperties( CONFIG_FILE,
                                                         ClassLoaderUtil.getClassLoader( null, ProviderLocator.class ),
                                                         true );
        String providerName = props.getProperty( serviceClass.getName(),
                                                 null );
        if ( providerName != null ) {
            return newInstance( serviceClass,
                                providerName,
                                classLoader );
        }

        // Look for a service provider specified using the mechanism
        // described in the JAR File Specification.
        String serviceId = "META-INF/services/".concat( serviceClass.getName() );
        InputStream providerConfigFile = (classLoader != null) ? classLoader.getResourceAsStream( serviceId ) : ClassLoader.getSystemResourceAsStream( serviceId );
        if ( providerConfigFile != null ) {
            try {
                List<String> results = readProvidersFrom( providerConfigFile );
                if ( !results.isEmpty() ) {
                    return newInstance( serviceClass,
                                        results.get( 0 ),
                                        classLoader );
                }
            } catch ( IOException e ) {
                final String msg = MessageFormat.format( ERR_FILE_READ,
                                                         serviceClass.getName() );
                throw new ProviderInitializationException( msg,
                                                           e );
            } finally {
                try {
                    providerConfigFile.close();
                } catch ( IOException e ) {
                    // FIXME: This should use the Drools logging mechanism.
                    e.printStackTrace();
                }
            }
        }

        // did not find an implementation anywhere
        final String msg = MessageFormat.format( ERR_NOT_FOUND,
                                                 serviceClass.getName() );
        throw new ProviderInitializationException( msg );
    }   
    
    /**
     * Parses a Provider-Configuration File as described in the JAR
     * File Specification.
     * 
     * @param stream
     *         An open byte stream encoded using UTF-8.
     * @return
     *         A list of all the service providers identified in the
     *         given stream.
     * @throws IOException If an I/O error occurs
     */
    protected static final List<String> readProvidersFrom(final InputStream stream) throws IOException {
        final Set<String> results = new LinkedHashSet<String>();

        if ( stream != null ) {
            BufferedReader reader = new BufferedReader( new InputStreamReader( stream,
                                                                               "UTF-8" ) );
            String line;
            while ( (line = reader.readLine()) != null ) {
                int pos = line.indexOf( '#' );
                if ( pos >= 0 ) {
                    line = line.substring( 0,
                                           pos );
                }
                line = line.trim();
                if ( line.length() != 0 ) {
                    results.add( line );
                }
            }
        }

        return new ArrayList<String>( results );
    }

    /**
     * Instantiates a new object implementing the interface <tt>T</tt>.
     * 
     * @param <T>
     *          Either an interface or a (usually abstract) class
     *          defining a service as described in the JAR File
     *          Specification.
     * @param serviceClass
     *          The Class for <tt>T</tt>. Used to cast the new object
     *          to <tt>T</tt>. It is important that this Class be
     *          visible from the given ClassLoader (if provided).
     * @param providerName
     *          A fully qualified name of a class implementing the
     *          service.
     * @param classLoader
     *          An optional ClassLoader used to load and instantiate
     *          the service provider. May be <tt>null</tt>.
     * @return A new instance of the service provider.
     * @throws ProviderInitializationException
     *          If the class specified by providerName cannot be found,
     *          cannot be instantiated, is not accessible, or does not
     *          implement (extend) the interface (class) <tt>T</tt>.
     * @see {@linkplain http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider}
     */
    private static <T> T newInstance(final Class<T> serviceClass,
                                     final String providerName,
                                     final ClassLoader classLoader) throws ProviderInitializationException {
        try {
            Class< ? > providerClass = classLoader == null ? Class.forName( providerName ) : classLoader.loadClass( providerName );
            return newInstance( serviceClass,
                                providerClass );
        } catch ( ClassNotFoundException e ) {
            final String msg = MessageFormat.format( ERR_NOT_FOUND,
                                                     serviceClass.getName() );
            throw new ProviderInitializationException( msg,
                                                       e );
        }
    }

    /**
     * Instantiates a new object implementing the interface <tt>T</tt>.
     * 
     * @param <T>
     *          Either an interface or a (usually abstract) class
     *          defining a service as described in the JAR File
     *          Specification.
     * @param serviceClass
     *          The Class for <tt>T</tt>. Used to cast the new object
     *          to <tt>T</tt>. It is important that this Class be
     *          visible from the given ClassLoader (if provided).
     * @param providerClass
     *          A class implementing the interface <tt>T</tt>.
     * @return A new instance of the service provider.
     * @throws ProviderInitializationException
     *          If the class cannot be instantiated, is not accessible,
     *          or does not implement (extend) the interface (class)
     *          <tt>T</tt>.
     * @see {@linkplain http://java.sun.com/j2se/1.4.2/docs/guide/jar/jar.html#Service%20Provider}
     */
    private static <T> T newInstance(final Class<T> serviceClass,
                                     final Class< ? > providerClass) throws ProviderInitializationException {
        try {
            Object provider = providerClass.newInstance();
            return serviceClass.cast( provider );
        } catch ( InstantiationException e ) {
            final String msg = MessageFormat.format( ERR_NOT_CONCRETE,
                                                     providerClass.getName() );
            throw new ProviderInitializationException( msg,
                                                       e );
        } catch ( IllegalAccessException e ) {
            final String msg = MessageFormat.format( ERR_NOT_ACCESSIBLE,
                                                     providerClass.getName() );
            throw new ProviderInitializationException( msg,
                                                       e );
        } catch ( ClassCastException e ) {
            final String pattern = serviceClass.isInterface() ? ERR_IMPLEMENTS : ERR_EXTENDS;
            final String msg = MessageFormat.format( pattern,
                                                     providerClass.getName(),
                                                     serviceClass.getName() );
            throw new ProviderInitializationException( msg,
                                                       e );
        }
    }

    // Localized messages -- move to resource bundle.
    private static final String ERR_FILE_READ      = "Unable to read the Provider-Configuration File for {0}.";
    private static final String ERR_EXTENDS        = "Provider {0} does not extend {2}.";
    private static final String ERR_IMPLEMENTS     = "Provider {0} does not implement {2}.";
    private static final String ERR_NOT_ACCESSIBLE = "Provider {0} is not accessible.";
    private static final String ERR_NOT_CONCRETE   = "Provider {0} cannot be instantiated.";
    private static final String ERR_NOT_FOUND      = "Provider for {0} could not be found.";
}
