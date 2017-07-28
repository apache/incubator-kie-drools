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

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.assembler.KieAssemblersImpl;
import org.kie.api.internal.runtime.KieRuntimeService;
import org.kie.api.internal.runtime.KieRuntimes;
import org.kie.api.internal.runtime.KieRuntimesImpl;
import org.kie.api.internal.runtime.beliefs.KieBeliefService;
import org.kie.api.internal.runtime.beliefs.KieBeliefs;
import org.kie.api.internal.runtime.beliefs.KieBeliefsImpl;
import org.kie.api.internal.weaver.KieWeaverService;
import org.kie.api.internal.weaver.KieWeavers;
import org.kie.api.internal.weaver.KieWeaversImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDiscoveryImpl {
    private static final Logger log = LoggerFactory.getLogger( ServiceDiscoveryImpl.class );

    private final String fileName = "kie.conf";

    private final String path =  "META-INF/" + fileName;

    private ClassLoader classloader;

    private final List<ServiceProcessor> processors = Arrays.asList( this::processKieService,
                                                                     this::processKieBeliefs,
                                                                     this::processKieAssemblers,
                                                                     this::processKieWeavers,
                                                                     this::processKieRuntimes );

    private ServiceDiscoveryImpl() {}

    private static class LazyHolder {
        static final ServiceDiscoveryImpl INSTANCE = new ServiceDiscoveryImpl();
    }

    public static ServiceDiscoveryImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    private Map<String, Object>     services   = new HashMap<String, Object>();
    private KieAssemblers           assemblers = new KieAssemblersImpl();
    private KieWeavers              weavers    = new KieWeaversImpl();
    private KieRuntimes             runtimes   = new KieRuntimesImpl();
    private KieBeliefs              beliefs    = new KieBeliefsImpl();
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
        log.info("Loading kie.conf from  ", classLoader);
        Properties props = loadConfs( url );
        processKieConf( classLoader, props );
    }

    private Properties loadConfs(URL url) {
        // iterate urls, then for each url split the service key and attempt to register each service
        Properties props = new Properties();
        try (InputStream is = url.openStream()) {
            props.load( is );
            log.info("Discovered kie.conf url={} ", url);
        } catch (Exception exc) {
            throw new RuntimeException("Unable to build kie service url = " + url.toExternalForm(), exc);
        }
        return props;
    }

    private void processKieConf(ClassLoader classLoader, Properties props) {
        props.forEach( (k, v) -> {
            String key = k.toString();
            boolean optional = key.startsWith( "?" );
            try {
                processors.stream()
                          .filter( p -> p.process( classLoader, optional ? key.substring( 1 ) : key, v.toString() ) )
                          .findFirst()
                          .orElseThrow( () -> new RuntimeException( "Cannot process pair: ( " + k + ", " + v + " )" ) );
            } catch (RuntimeException e) {
                if (optional) {
                    log.info("Cannot load service: " + key.substring( 1 ));
                } else {
                    throw e;
                }
            }
        });
    }

    @FunctionalInterface
    private interface ServiceProcessor {
        boolean process(ClassLoader classLoader, String key, String value);
    }

    private boolean processKieRuntimes(ClassLoader classLoader, String key, String value) {
        if (key.startsWith( "kie.runtimes." )) {
            KieRuntimeService runtime = newInstance( classLoader, value );
            log.info("Adding Runtime {}\n", runtime.getServiceInterface().getName());
            runtimes.getRuntimes().put(runtime.getServiceInterface().getName(), runtime);
            return true;
        }
        return false;
    }

    private boolean processKieAssemblers(ClassLoader classLoader, String key, String value) {
        if (key.startsWith( "kie.assemblers." )) {
            KieAssemblerService assemblerFactory = newInstance( classLoader, value );
            log.info( "Adding Assembler {}\n", assemblerFactory.getClass().getName() );
            assemblers.getAssemblers().put(assemblerFactory.getResourceType(), assemblerFactory);
            return true;
        }
        return false;
    }

    private boolean processKieWeavers(ClassLoader classLoader, String key, String value) {
        if (key.startsWith( "kie.weavers." )) {
            KieWeaverService weaver = newInstance( classLoader, value );
            log.info("Adding Weaver {}\n", weavers.getClass().getName());
            weavers.getWeavers().put( weaver.getResourceType(), weaver );
            return true;
        }
        return false;
    }

    private boolean processKieBeliefs(ClassLoader classLoader, String key, String value) {
        if (key.startsWith( "kie.beliefs." )) {
            KieBeliefService belief = newInstance( classLoader, value );
            log.info("Adding Belief {}\n", belief.getClass().getName());
            beliefs.getBeliefs().put(belief.getBeliefType(), belief);
            return true;
        }
        return false;
    }

    private boolean processKieService(ClassLoader classLoader, String key, String value) {
        if (key.startsWith( "kie.services." )) {
            String serviceName = key.substring( "kie.services.".length() );
            services.put(serviceName, newInstance( classLoader, value ));
            log.info( "Adding Service {}\n", value );
            return true;
        }
        return false;
    }

    private <T> T newInstance( ClassLoader classLoader, String className ) {
        try {
            return (T) Class.forName( className, true, classLoader ).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException( "Cannot create instance of class: " + className, e );
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
        for (String serviceName : services.keySet()) {
            cachedServices.put(serviceName, services.get(serviceName));
        }

        cachedServices.put( KieAssemblers.class.getName(), assemblers );
        cachedServices.put( KieWeavers.class.getName(), weavers );
        cachedServices.put( KieRuntimes.class.getName(), runtimes);
        cachedServices.put( KieBeliefs.class.getName(), beliefs );
    }
}
