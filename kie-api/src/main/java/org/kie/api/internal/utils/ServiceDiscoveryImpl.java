/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.api.internal.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDiscoveryImpl {
    private static final Logger log = LoggerFactory.getLogger( ServiceDiscoveryImpl.class );

    private final String fileName = "kie.conf";

    private final String path =  "META-INF/" + fileName;

    private ClassLoader classloader;

    private ServiceDiscoveryImpl() {}

    private static class LazyHolder {
        static final ServiceDiscoveryImpl INSTANCE = new ServiceDiscoveryImpl();
    }

    public static ServiceDiscoveryImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    private Map<String, Object>     services   = new HashMap<>();
    private Map<String, List<?>>    childServices = new HashMap<>();
    private boolean                 sealed     = false;
    private boolean                 kiecConfDiscoveryAllowed = true;
    private Map<String, Object>     cachedServices = new HashMap<String, Object>();

    public synchronized boolean isKiecConfDiscoveryAllowed() {
        return kiecConfDiscoveryAllowed;
    }

    public synchronized void setKiecConfDiscoveryAllowed(boolean kiecConfDiscoveryAllowed) {
        this.kiecConfDiscoveryAllowed = kiecConfDiscoveryAllowed;
    }

    public <T> void addService(Class<T> serviceClass, T service) {
        addService( serviceClass.getCanonicalName(), service );
    }

    public synchronized void addService(String serviceName, Object object) {
        if (!sealed) {
            cachedServices.put(serviceName, object);
        } else {
            throw new IllegalStateException("Unable to add service '" + serviceName + "'. Services cannot be added once the ServiceDiscoverys is sealed");
        }
    }

    public synchronized void reset() {
        cachedServices = new HashMap<String, Object>();
        sealed = false;
    }

    public synchronized Map<String, Object> getServices() {
        if (!sealed) {
            if (kiecConfDiscoveryAllowed) {
                Enumeration<URL> confResources = null;
                try {
                    confResources = getClassLoader().getResources(path);
                } catch (Exception e) {
                    new IllegalStateException("Discovery started, but no kie.conf's found");
                }
                if (confResources != null) {
                    while (confResources.hasMoreElements()) {
                        registerConfs( getClassLoader(), confResources.nextElement() );
                    }
                }
                buildMap();
            }

            cachedServices = Collections.unmodifiableMap( cachedServices );
            sealed = true;
        }
        return cachedServices;
    }

    public void registerConfs( ClassLoader classLoader, URL url ) {
        log.info("Loading kie.conf from  " + url + " in classloader " + classLoader);

        try ( BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream())) ) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                // DROOLS-2122: parsing with Properties.load a Drools version 6 kie.conf, hence skipping this entry
                if (line.contains( "=" ) && !line.contains( "[" )) {
                    String[] entry = line.split( "=" );
                    processKieService( classLoader, entry[0].trim(), entry[1].trim() );
                }
            }
        } catch (Exception exc) {
            throw new RuntimeException( "Unable to build kie service url = " + url.toExternalForm(), exc );
        }
    }

    private void processKieService(ClassLoader classLoader, String key, String values) {
        for (String value : values.split( "," )) {
            boolean optional = key.startsWith( "?" );
            String serviceName = optional ? key.substring( 1 ) : key;
            try {
                if ( value.startsWith( "+" ) ) {
                    childServices.computeIfAbsent( serviceName, k -> new ArrayList<>() )
                            .add( newInstance( classLoader, value.substring( 1 ) ) );
                } else {
                    services.put( serviceName, newInstance( classLoader, value ) );
                }
            } catch (RuntimeException e) {
                if (optional) {
                    log.info("Cannot load service: " + serviceName);
                } else {
                    System.out.println("Loading failed because " + e.getMessage());
                    throw e;
                }
            }
            log.info( "Adding Service {}\n", value );
        }
    }

    @FunctionalInterface
    private interface ServiceProcessor {
        boolean process(ClassLoader classLoader, String key, String value);
    }

    private <T> T newInstance( ClassLoader classLoader, String className ) {
        try {
            return (T) Class.forName( className, true, classLoader ).newInstance();
        } catch (Throwable t) {
            throw new RuntimeException( "Cannot create instance of class: " + className, t );
        }
    }

    private ClassLoader getClassLoader() {
        if (classloader == null) {
            classloader = Thread.currentThread().getContextClassLoader();
            if (classloader == null) {
                classloader = ClassLoader.getSystemClassLoader();
            }
        }
        return classloader;
    }

    private void buildMap() {
        for (Map.Entry<String, Object> serviceEntry : services.entrySet()) {
            cachedServices.put(serviceEntry.getKey(), serviceEntry.getValue());
            List<?> children = childServices.remove( serviceEntry.getKey() );
            if (children != null) {
                for (Object child : children) {
                    ( (Consumer) serviceEntry.getValue() ).accept( child );
                }
            }
        }

        if (!childServices.isEmpty()) {
            throw new RuntimeException("Child services " + childServices.keySet() + " have no parent");
        }
    }
}
