/*
 * Copyright 2010 JBoss Inc
 *
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

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Priority
 * 
 * System properties, home directory, working directory, META-INF/ of optionally provided classLoader
 * META-INF/ of Thread.currentThread().getContextClassLoader() and META-INF/ of  ClassLoader.getSystemClassLoader()
 */
public class ChainedProperties
    implements
    Externalizable {

    private List<Properties> props;
    private List<Properties> defaultProps;

    public ChainedProperties() {
    }
    
    public ChainedProperties(String confFileName, ClassLoader classLoader) {
        this( confFileName,
              classLoader,
              true );
    }
    
    public ChainedProperties(String confFileName,
                             ClassLoader classLoader,
                             boolean populateDefaults) {

        this.props = new ArrayList<Properties>();
        this.defaultProps = new ArrayList<Properties>();

        // Properties added in precedence order

        // System defined properties always get precedence
        addProperties( System.getProperties() );

        // System property defined properties file
        loadProperties( System.getProperty( "drools." + confFileName ),
                        this.props );

        // User home properties file
        loadProperties( System.getProperty( "user.home" ) + "/drools." + confFileName,
                        this.props );

        // Working directory properties file
        loadProperties( "drools." + confFileName,
                        this.props );
        
//        if ( classLoader == null ) {
//            classLoader = Thread.currentThread().getContextClassLoader();
//            if ( classLoader == null ) {
//                classLoader = cls.getClassLoader();
//            }
//        }

        // check META-INF directories for all known ClassLoaders
        ClassLoader confClassLoader = classLoader;
        loadProperties( getResources( "META-INF/drools." + confFileName,
                                      confClassLoader ),
                        this.props );
        loadProperties( getResources( "/META-INF/drools." + confFileName,
                                      confClassLoader ),
                        this.props );

        confClassLoader = ClassLoader.getSystemClassLoader();
        if ( confClassLoader != null && confClassLoader != classLoader ) {
            loadProperties( getResources( "META-INF/drools." + confFileName,
                                          confClassLoader ),
                            this.props );
            loadProperties( getResources( "/META-INF/drools." + confFileName,
                                          confClassLoader ),
                            this.props );
        }

        if ( !populateDefaults ) {
            return;
        }

        // load defaults
        confClassLoader = classLoader;
        loadProperties( getResources( "META-INF/drools.default." + confFileName,
                                      confClassLoader ),
                        this.defaultProps );
        loadProperties( getResources( "/META-INF/drools.default." + confFileName,
                                      confClassLoader ),
                        this.defaultProps );

        confClassLoader = ClassLoader.getSystemClassLoader();
        if ( confClassLoader != null && confClassLoader != classLoader ) {
            loadProperties( getResources( "META-INF/drools.default." + confFileName,
                                          confClassLoader ),
                            this.defaultProps );
            loadProperties( getResources( "/META-INF/drools.default." + confFileName,
                                          confClassLoader ),
                            this.defaultProps );
        }
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        props = (List<Properties>) in.readObject();
        defaultProps = (List<Properties>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( props );
        out.writeObject( defaultProps );
    }

    private Enumeration<URL> getResources(String name,
                                          ClassLoader classLoader) {
        Enumeration<URL> enumeration = null;
        try {
            enumeration = classLoader.getResources( name );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return enumeration;
    }

    public void addProperties(Properties properties) {
        this.props.add( properties );
    }

    public String getProperty(String key,
                              String defaultValue) {
        String value = null;
        for ( Properties props : this.props ) {
            value = props.getProperty( key );
            if ( value != null ) {
                break;
            }
        }
        if ( value == null ) {
            for ( Properties props : this.defaultProps ) {
                value = props.getProperty( key );
                if ( value != null ) {
                    break;
                }
            }
        }
        return (value != null) ? value : defaultValue;
    }

    public void mapStartsWith(Map<String, String> map,
                              String startsWith,
                              boolean includeSubProperties) {
        for ( Properties props : this.props ) {
            mapStartsWith( map,
                           props,
                           startsWith,
                           includeSubProperties );
        }

        for ( Properties props : this.defaultProps ) {
            mapStartsWith( map,
                           props,
                           startsWith,
                           includeSubProperties );
        }
    }

    private void mapStartsWith(Map<String, String> map,
                               Properties properties,
                               String startsWith,
                               boolean includeSubProperties) {
        Enumeration< ? > enumeration = properties.propertyNames();
        while ( enumeration.hasMoreElements() ) {
            String key = (String) enumeration.nextElement();
            if ( key.startsWith( startsWith ) ) {
                if ( !includeSubProperties && key.substring( startsWith.length() + 1 ).indexOf( '.' ) > 0 ) {
                    // +1 to the length, as we do allow the direct property, just not ones below it
                    // This key has sub properties beyond the given startsWith, so skip
                    continue;
                }
                if ( !map.containsKey( key ) ) {
                    map.put( key,
                             properties.getProperty( key ) );
                }

            }
        }
    }

    private void loadProperties(Enumeration<URL> enumeration,
                                List<Properties> chain) {
        if ( enumeration == null ) {
            return;
        }

        while ( enumeration.hasMoreElements() ) {
            URL url = (URL) enumeration.nextElement();
            loadProperties( url,
                            chain );
        }
    }

    private void loadProperties(String fileName,
                                List<Properties> chain) {
        if ( fileName != null ) {
            File file = new File( fileName );
            if ( file != null && file.exists() ) {
                try {
                    loadProperties( file.toURL(),
                                    chain );
                } catch ( MalformedURLException e ) {
                    throw new IllegalArgumentException( "file.toURL() failed for " + fileName + " properties value '" + file + "'" );
                }
            } else {
                //throw new IllegalArgumentException( fileName + " is specified but cannot be found '" + file + "'" );
            }
        }
    }

    private void loadProperties(URL confURL,
                                List<Properties> chain) {
        if ( confURL == null ) {
            return;
        }
        Properties properties = new Properties();
        try {
            java.io.InputStream is = confURL.openStream();
            properties.load( is );
            is.close();
            chain.add( properties );
        } catch ( IOException e ) {
            //throw new IllegalArgumentException( "Invalid URL to properties file '" + confURL.toExternalForm() + "'" );
        }
    }
}
