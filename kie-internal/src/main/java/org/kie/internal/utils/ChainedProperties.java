/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.utils;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Priority
 * <ul>
 *  <li>System properties</li>
 *  <li>home directory</li>
 *  <li>working directory</li>
 *  <li>META-INF/ of optionally provided classLoader</li>
 *  <li>META-INF/ of Thread.currentThread().getContextClassLoader()</li>
 *  <li>META-INF/ of  ClassLoader.getSystemClassLoader()</li>
 * </ul>
 * <br/>
 * To improve performance in frequent session creation cases, chained properties can be cached by it's conf file name
 * and requesting classloader. To take advantage of the case it must be enabled via system property:<br/>
 * <code>org.kie.property.cache.enabled</code> that needs to be set to <code>true</code>
 * Cache entries are by default limited to 100 to reduce memory consumption but can be fine tuned by system property:<br/>
 * <code>org.kie.property.cache.size</code> that needs to be set to valid integer value
 */
public class ChainedProperties
    implements
    Externalizable {

    protected static transient Logger logger = LoggerFactory.getLogger(ChainedProperties.class);
    private static final int MAX_CACHE_ENTRIES = Integer.parseInt(System.getProperty("org.kie.property.cache.size", "100"));
    private static final boolean CACHE_ENABLED = Boolean.parseBoolean(System.getProperty("org.kie.property.cache.enabled", "false"));

    protected static Map<CacheKey, List<Properties>> propertiesCache =
            Collections.synchronizedMap(
                    new LinkedHashMap<CacheKey, List<Properties>>() {
        private static final long serialVersionUID = -4728876927433598466L;

        protected boolean removeEldestEntry(
                Map.Entry<CacheKey, List<Properties>> eldest) {
            return size() > MAX_CACHE_ENTRIES;
        }
    });

    private List<Properties> props;
    private List<Properties> defaultProps;

    public ChainedProperties() {
    }

    public ChainedProperties(String confFileName, ClassLoader classLoader) {
        this(confFileName, classLoader, true);
    }

    public ChainedProperties(String confFileName, ClassLoader classLoader,
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
        loadProperties( System.getProperty( "user.home" ) + "/drools."
                + confFileName, this.props );

        // Working directory properties file
        loadProperties( "drools." + confFileName, this.props );

        // check META-INF directories for all known ClassLoaders

        // DROOLS-1443 load properties from supplied classLoader
        loadProperties( "META-INF/drools." + confFileName, classLoader,
                this.props );

        // DROOLS-1443 load properties from supplied classLoader
        loadProperties( "/META-INF/drools." + confFileName, classLoader,
                this.props );

        ClassLoader contextClassLoader = Thread.currentThread()
                .getContextClassLoader();
        if ( contextClassLoader != null && contextClassLoader != classLoader ) {
            // DROOLS-1443 load properties from context classLoader
            loadProperties(confFileName, contextClassLoader, this.props);
        }

        ClassLoader systemClassLoader = null;
        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException se) {
            // DROOLS-1125: the system class loader cannot be retrieved in
            // Google App Engine - ignore
        }
        if ( systemClassLoader != null && systemClassLoader != classLoader ) {
            // DROOLS-1443 load properties from system classLoader
            loadProperties(confFileName, systemClassLoader, this.props);
        }

        if ( !populateDefaults ) {
            return;
        }

        // load defaults
        // DROOLS-1443 load default properties from supplied classLoader
        loadProperties( "META-INF/drools.default." + confFileName, classLoader,
                this.defaultProps);
        // DROOLS-1443 load default properties from supplied classLoader
        loadProperties( "/META-INF/drools.default." + confFileName, classLoader,
                this.defaultProps);

        if ( contextClassLoader != null && contextClassLoader != classLoader ) {
            // DROOLS-1443 load default properties from context classLoader
            loadProperties(confFileName, contextClassLoader, this.defaultProps);
        }
        if ( systemClassLoader != null && systemClassLoader != classLoader ) {
            // DROOLS-1443 load default properties from system classLoader
            loadProperties(confFileName, systemClassLoader, this.defaultProps);
        }

        // this happens only in OSGi: for some reason doing
        // ClassLoader.getResources() doesn't work but doing
        // Class.getResourse() does
        if (this.defaultProps.isEmpty()) {
            try {
                Class<?> c = Class.forName(
                        "org.drools.compiler.lang.MVELDumper", false,
                        classLoader);
                URL confURL = c.getResource("/META-INF/drools.default."
                        + confFileName);
                loadProperties(confURL, this.defaultProps);
            } catch (ClassNotFoundException e) { }
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

    /**
     * Specifically added properties take priority, so they go to the front of the list.
     * @param properties
     */
    public void addProperties(Properties properties) {
        this.props.add( 0, properties );
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

    private void loadProperties(String fileName,
                                List<Properties> chain) {
        if (fileName != null) {
            File file = new File(fileName);
            if (file != null && file.exists()) {
                loadProperties(fileName, null, chain);
            }
        }
    }

    private void loadProperties(String fileName,
                                ClassLoader classLoader,
                                List<Properties> chain) {
        if (!CACHE_ENABLED) {
            try {
                chain.addAll(read(fileName,classLoader));
            } catch (IOException e){}
            return;
        }
        CacheKey ck = new CacheKey(fileName, classLoader);
        List<Properties> cached = propertiesCache.get(ck);
        if (cached == null) {
            try {
                cached = read(fileName, classLoader);
                propertiesCache.put(ck, cached);
            } catch (IOException e) {}
        }
        if (cached != null) {
            chain.addAll(cached);
        }
    }

    private List<Properties> read(String fileName, ClassLoader classLoader)
            throws IOException {
        List<Properties> properties = new ArrayList<>();
        Enumeration<URL> resources;
        if (classLoader != null) {
            resources = classLoader.getResources(fileName);
        } else {
            resources = Collections.enumeration(Collections.singletonList(new File(fileName).toURI().toURL()));
        }
        while (resources.hasMoreElements()) {
            Properties p = new Properties();
            URL nextElement = resources.nextElement();
            try (InputStream is = nextElement.openStream()) {
                p.load(is);
                properties.add(p);
            }
        }
        return properties;
    }

    private void loadProperties(URL confURL, List<Properties> chain) {
        if ( confURL == null ) {
            return;
        }
        try ( InputStream is = confURL.openStream() ) {
            Properties properties = new Properties();
            properties.load( is );
            chain.add( properties );
        } catch ( IOException e ) {
            //throw new IllegalArgumentException( "Invalid URL to properties file '" + confURL.toExternalForm() + "'" );
        }
    }


    /*
     * optional cache handling to improve performance to avoid duplicated loads of properties
     */


    private static class CacheKey {
        private String confFileName;
        private ClassLoader classLoader;

        CacheKey(String confFileName, ClassLoader classLoader) {
            this.confFileName = confFileName;
            this.classLoader = classLoader;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((classLoader == null) ? 0 : classLoader.hashCode());
            result = prime * result
                    + ((confFileName == null) ? 0 : confFileName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (classLoader == null) {
                if (other.classLoader != null) {
                    return false;
                }
            } else if (!classLoader.equals(other.classLoader)) {
                return false;
            }
            if (confFileName == null) {
                if (other.confFileName != null) {
                    return false;
                }
            } else if (!confFileName.equals(other.confFileName)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "CacheKey [confFileName=" + confFileName + ", classLoader="
                    + classLoader + "]";
        }

    }
}
