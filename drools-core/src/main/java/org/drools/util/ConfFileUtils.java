/**
 * 
 */
package org.drools.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class ConfFileUtils {
   
    /**
     * Return the URL for a given conf file
     * @param confName
     * @param classLoader
     * @return
     */
    public static URL getURL(String confName, ClassLoader classLoader, Class cls) {
        URL url = null;
        
        // User home 
        String userHome = System.getProperty( "user.home" );
        if ( userHome.endsWith( "\\" ) || userHome.endsWith( "/" ) ) {
            url = getURLForFile( userHome + confName );
        } else {
            url = getURLForFile( userHome + "/" + confName );
        }

        // Working directory             
        if ( url == null ) {
            url = getURLForFile( confName );
        }
        
        // check Class folder
        if ( cls != null ) {
            url = cls.getResource( confName );
        }

        // check META-INF directories for all known ClassLoaders
        if ( url == null && classLoader != null ) {
            ClassLoader confClassLoader = classLoader;
            if ( confClassLoader != null ) {
                url = confClassLoader.getResource( "META-INF/" + confName );
            }
        }

        if ( url == null ) {
            ClassLoader confClassLoader = ConfFileUtils.class.getClassLoader();
            if ( confClassLoader != null && confClassLoader != classLoader ) {
                url = confClassLoader.getResource( "META-INF/" + confName );
            }
        }
        
        if ( url == null && cls != null ) {
            ClassLoader confClassLoader = cls.getClassLoader();
            if ( confClassLoader != null && confClassLoader != classLoader ) {
                url = confClassLoader.getResource( "META-INF/" + confName );
            }
        }        

        if ( url == null ) {
            ClassLoader confClassLoader = Thread.currentThread().getContextClassLoader();
            if ( confClassLoader != null && confClassLoader != classLoader ) {
                url = confClassLoader.getResource( "META-INF/" + confName );
            }
        }

        if ( url == null ) {
            ClassLoader confClassLoader = ClassLoader.getSystemClassLoader();
            if ( confClassLoader != null && confClassLoader != classLoader ) {
                url = confClassLoader.getResource( "META-INF/" + confName );
            }
        }

        return url;          
    }
    
    /**
     * Return URL for given filename
     * 
     * @param fileName
     * @return
     *  URL
     */
    public static URL getURLForFile(String fileName) {
        URL url = null;
        if ( fileName != null ) {
            File file = new File( fileName );
            if ( file != null && file.exists() ) {
                try {
                    url = file.toURL();
                } catch ( MalformedURLException e ) {
                    throw new IllegalArgumentException( "file.toURL() failed for '" + file + "'" );
                }
            } 
        }
        return url;
    }     
    
    public static String URLContentsToString(URL url) {
        StringBuilder builder = new StringBuilder();
        if ( url == null ) {
            return null;
        }
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader( url.openStream() ));
            String line = null;
        
            while ( ( line = reader.readLine() ) != null ) { // while loop begins here
                builder.append( line );
                builder.append( "\n" );
            }
            
            reader.close();
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to read " + url.toExternalForm() );
        }        
        return builder.toString();
    }
    
    /**
     * Load a Properties file from a URL, return null if it fails
     * 
     * @param url
     * @return
     *  URL
     */
    public static Properties getProperties(URL url) {
        if ( url == null ) {
            return null;
        }
        
        Properties properties = new Properties();
        try {
            properties.load( url.openStream() );
        } catch ( IOException e ) {
            //swallow, as we'll return null
            return null;
        }       
        
        return properties;
    }
                   
}