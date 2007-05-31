/**
 * 
 */
package org.drools.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ChainedProperties {
    private final List props;
    private final List defaultProps;
    
    public ChainedProperties(String confFileName) {
        this( Thread.currentThread().getContextClassLoader(),
              confFileName );
    }
    
    public ChainedProperties(ClassLoader classLoader, String confFileName) {
        this.props = new ArrayList();
        this.defaultProps = new ArrayList();
        
        // Properties added in precedence order
        //this.chainedProperties = new ChainedProperties();

        // System defined properties always get precedence
        addProperties( System.getProperties() );

        // System property defined properties file
        loadProperties( System.getProperty( "drools.packagebuilder.conf" ), this.props );

        // User home properties file
        loadProperties( System.getProperty( "user.home" ) + "/drools.packagebuilder.conf", this.props );

        // Working directory properties file
        loadProperties( "drools.packagebuilder.conf", this.props );

        // check META-INF directories for all known ClassLoaders
        ClassLoader confClassLoader = classLoader;
        if ( confClassLoader != null ) {
            loadProperties( confClassLoader.getResource( "META-INF/drools." + confFileName), this.props );
        }

        confClassLoader = getClass().getClassLoader();
        if ( confClassLoader != null && confClassLoader != classLoader ) {
            loadProperties( confClassLoader.getResource( "META-INF/drools." + confFileName ), this.props );
        }

        confClassLoader = Thread.currentThread().getContextClassLoader();
        if ( confClassLoader != null && confClassLoader != classLoader ) {
            loadProperties( confClassLoader.getResource( "META-INF/drools." + confFileName ), this.props );
        }

        confClassLoader = ClassLoader.getSystemClassLoader();
        if ( confClassLoader != null && confClassLoader != classLoader ) {
            loadProperties( confClassLoader.getResource( "META-INF/drools." + confFileName ), this.props );
        }

        // load default, only use the first one as there should only be one
        confClassLoader = classLoader;
        URL defaultURL = null;
        if ( confClassLoader != null ) {
            defaultURL = confClassLoader.getResource( "META-INF/drools.default." + confFileName );
        }

        if ( defaultURL == null ) {
            confClassLoader = getClass().getClassLoader();
            if ( confClassLoader != null && confClassLoader != classLoader ) {
                defaultURL = confClassLoader.getResource( "META-INF/drools.default." + confFileName );
            }
        }

        if ( defaultURL == null ) {
            confClassLoader = Thread.currentThread().getContextClassLoader();
            if ( confClassLoader != null && confClassLoader != classLoader ) {
                defaultURL = confClassLoader.getResource( "META-INF/drools.default." + confFileName );
            }
        }

        if ( defaultURL == null ) {
            confClassLoader = ClassLoader.getSystemClassLoader();
            if ( confClassLoader != null && confClassLoader != classLoader ) {
                defaultURL = confClassLoader.getResource( "META-INF/drools.default.packagebuilder.conf" );
            }
        }

        if ( defaultURL != null ) {
            loadProperties( defaultURL, this.defaultProps );
        }
    }
    
    public void addProperties(Properties properties) {
        this.props.add( properties );
    }  

    public String getProperty(String key,
                              String defaultValue) {
        String value = null;
        for ( Iterator it = this.props.iterator(); it.hasNext(); ) {
            Properties props = (Properties) it.next();
            value = props.getProperty( key );
            if ( value != null ) {
                break;
            }
        }
        if ( value == null ) {
            for ( Iterator it = this.defaultProps.iterator(); it.hasNext(); ) {
                Properties props = (Properties) it.next();
                value = props.getProperty( key );
                if ( value != null ) {
                    break;
                }
            }
        }
        return (value != null) ? value : defaultValue;
    }

    public void mapStartsWith(Map map,
                              String startsWith) {
        for ( Iterator it = this.props.iterator(); it.hasNext(); ) {
            Properties props = (Properties) it.next();
            mapStartsWith( map,
                           props,
                           startsWith );
        }
        
        for ( Iterator it = this.defaultProps.iterator(); it.hasNext(); ) {
            Properties props = (Properties) it.next();
            mapStartsWith( map,
                           props,
                           startsWith );
        }        
    }

    private void mapStartsWith(Map map,
                               Properties properties,
                               String startsWith) {
        Enumeration enumeration = properties.propertyNames();
        while ( enumeration.hasMoreElements() ) {
            String key = (String) enumeration.nextElement();
            if ( key.startsWith( startsWith ) ) {
                if ( !map.containsKey( key ) ) {
                    map.put( key,
                             properties.getProperty( key ) );
                }

            }
        }
    }
        
    private void loadProperties(String fileName, List chain) {
        if ( fileName != null ) {
            File file = new File( fileName );
            if ( file != null && file.exists() ) {
                try {
                    loadProperties( file.toURL(), chain );
                } catch ( MalformedURLException e ) {
                    throw new IllegalArgumentException( "file.toURL failed for drools.packagebuilder.conf properties value '" + file + "'" );
                }
            } else {
                //throw new IllegalArgumentException( "drools.packagebuilder.conf is specified but cannot be found '" + file + "'" );
            }
        }
    }

    private void loadProperties(URL confURL, List chain) {
        if ( confURL != null ) {
            Properties properties = new Properties();
            try {
                properties.load( confURL.openStream() );
                chain.add( properties );
            } catch ( IOException e ) {
                //throw new IllegalArgumentException( "Invalid URL to properties file '" + confURL.toExternalForm() + "'" );
            }
        }
    }    
}