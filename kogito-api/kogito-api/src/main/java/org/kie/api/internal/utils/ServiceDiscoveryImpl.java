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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDiscoveryImpl {
    private static final Logger log = LoggerFactory.getLogger( ServiceDiscoveryImpl.class );

    private static final String CONF_FILE_NAME = "kie.conf";

    private static final String CONF_FILE_PATH =  "META-INF/" + CONF_FILE_NAME;

    ServiceDiscoveryImpl() {}

    private static class LazyHolder {
        static final ServiceDiscoveryImpl INSTANCE = new ServiceDiscoveryImpl();
    }

    public static ServiceDiscoveryImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    private final PriorityMap<String, Object> services = new PriorityMap<>();
    private final Map<String, List<?>> childServices = new HashMap<>();

    private Map<String, List<Object>> cachedServices;
    private boolean sealed = false;

    public <T> void addService(Class<T> serviceClass, T service) {
        addService( serviceClass.getCanonicalName(), service );
    }

    public synchronized void addService(String serviceName, Object object) {
        if (!sealed) {
            cachedServices.computeIfAbsent(serviceName, n -> new ArrayList<>()).add(object);
        } else {
            throw new IllegalStateException("Unable to add service '" + serviceName + "'. Services cannot be added once the ServiceDiscoverys is sealed");
        }
    }

    public synchronized void reset() {
        cachedServices = null;
        sealed = false;
    }

    public synchronized Map<String, List<Object>> getServices() {
        if (!sealed) {
            getKieConfs().ifPresent( kieConfs -> {
                while (kieConfs.resources.hasMoreElements()) {
                    registerConfs( kieConfs.classLoader, kieConfs.resources.nextElement() );
                }
            } );

            cachedServices = Collections.unmodifiableMap( buildMap() );
            sealed = true;
        }
        return cachedServices;
    }

    public void registerConfs( ClassLoader classLoader, URL url ) {
        log.debug("Loading kie.conf from  " + url + " in classloader " + classLoader);

        try ( BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream())) ) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                // DROOLS-2122: parsing with Properties.load a Drools version 6 kie.conf, hence skipping this entry
                if (line.contains( "=" ) && !line.contains( "[" )) {
                    String[] entry = line.split( "=" );
                    processKieService( classLoader, entry[0].trim(), entry[1].trim() );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException( "Unable to build kie service url = " + url.toExternalForm(), e );
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
                    log.debug( "Added child Service " + value );
                } else {
                    String[] splitValues = value.split( ";" );
                    if (splitValues.length > 2) {
                        throw new RuntimeException( "Invalid kie.conf entry: " + value );
                    }
                    int priority = splitValues.length == 2 ? Integer.parseInt( splitValues[1].trim() ) : 0;
                    services.put( priority, serviceName, newInstance( classLoader, splitValues[0].trim() ) );
                    log.debug( "Added Service " + value + " with priority " + priority );
                }
            } catch (RuntimeException e) {
                if (optional) {
                    log.info("Cannot load service: " + serviceName);
                } else {
                    log.error("Loading failed because " + e.getMessage());
                    throw e;
                }
            }
        }
    }

    private <T> T newInstance( ClassLoader classLoader, String className ) {
        try {
            return (T) Class.forName( className, true, classLoader ).getConstructor().newInstance();
        } catch (Throwable t) {
            throw new RuntimeException( "Cannot create instance of class: " + className, t );
        }
    }

    private Map<String, List<Object>> buildMap() {
        Map<String, List<Object>> servicesMap = new HashMap<>();
        for (Map.Entry<String, List<Object>> serviceEntry : services.entrySet()) {
            log.debug( "Service " + serviceEntry.getKey() + " is implemented by " + serviceEntry.getValue().get(0) );
            servicesMap.put(serviceEntry.getKey(), serviceEntry.getValue());
            List<?> children = childServices.remove( serviceEntry.getKey() );
            if (children != null) {
                for (Object child : children) {
                    for (Object service : serviceEntry.getValue()) {
                        (( Consumer ) service).accept( child );
                    }
                }
            }
        }

        if (!childServices.isEmpty()) {
            throw new RuntimeException("Child services " + childServices.keySet() + " have no parent");
        }

        if (log.isTraceEnabled()) {
            for (Map.Entry<String, List<Object>> serviceEntry : servicesMap.entrySet()) {
                if (serviceEntry.getValue().size() == 1) {
                    log.trace( "Service " + serviceEntry.getKey() + " is implemented by " + serviceEntry.getValue().get(0) );
                } else {
                    log.trace( "Service " + serviceEntry.getKey() + " is implemented (in order of priority) by " + serviceEntry.getValue() );
                }
            }
        }

        return servicesMap;
    }

    private Optional<KieConfs> getKieConfs() {
        return Stream.of(this.getClass().getClassLoader(), Thread.currentThread().getContextClassLoader(), ClassLoader.getSystemClassLoader())
                .map(this::loadKieConfs)
                .filter( Objects::nonNull )
                .findFirst();
    }

    private KieConfs loadKieConfs(ClassLoader cl) {
        if (cl == null) {
            return null;
        }
        try {
            Enumeration<URL> resources = cl.getResources( CONF_FILE_PATH );
            return resources.hasMoreElements() ? new KieConfs( cl, resources ) : null;
        } catch (IOException e) {
            return null;
        }
    }

    private static class KieConfs {
        private final ClassLoader classLoader;
        private final Enumeration<URL> resources;

        private KieConfs( ClassLoader classLoader, Enumeration<URL> confResources ) {
            this.classLoader = classLoader;
            this.resources = confResources;
        }
    }

    private static class PriorityMap<K,V> {
        private final Map<K, TreeMap<Integer, V>> priorityMap = new HashMap<>();

        public void put(int priority, K key, V value) {
            TreeMap<Integer, V> treeMap = priorityMap.get(key);
            if ( treeMap == null ) {
                treeMap = new TreeMap<>();
                priorityMap.put( key, treeMap );
            } else {
                if ( treeMap.get( priority ) != null ) {
                    throw new RuntimeException("There already exists an implementation for service " + key + " with same priority " + priority);
                }
            }
            treeMap.put( priority, value );
        }

        public Iterable<? extends Map.Entry<K, List<V>>> entrySet() {
            Map<K, List<V>> map = new HashMap<>();
            for (Map.Entry<K, TreeMap<Integer, V>> entry : priorityMap.entrySet()) {
                List<V> list = new ArrayList<>();
                for (V value : entry.getValue().values()) {
                    list.add(0, value);
                }
                map.put( entry.getKey(), list );
            }
            return map.entrySet();
        }
    }
}
