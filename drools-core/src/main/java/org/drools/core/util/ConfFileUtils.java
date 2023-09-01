/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.drools.util.IoUtils;
import org.drools.util.PortablePath;

public class ConfFileUtils {
   
    /**
     * Return the URL for a given conf file
     * @param confName
     * @param classLoader
     * @return
     */
    public static URL getURL(String confName, ClassLoader classLoader, Class cls) {
        URL url;
        
        // User home 
        String userHome = PortablePath.of( System.getProperty( "user.home" ) ).asString();
        if ( userHome.endsWith( "/" ) ) {
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
            URL urlResource = cls.getResource( confName );
            if (urlResource != null) {
                url = urlResource;
            }
        }

        // check META-INF directories for all known ClassLoaders
        if ( url == null && classLoader != null ) {
            url = classLoader.getResource( "META-INF/" + confName );
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
            if ( file.exists() ) {
                try {
                    url = file.toURI().toURL();
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
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader( url.openStream(), IoUtils.UTF8_CHARSET))) {
            String line;
            while ( ( line = reader.readLine() ) != null ) { // while loop begins here
                builder.append( line );
                builder.append( "\n" );
            }
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
        try (final InputStream inputStream = url.openStream()) {
            properties.load( inputStream );
        } catch ( IOException e ) {
            //swallow, as we'll return null
            return null;
        }
        
        return properties;
    }
                   
}
