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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceDiscoveryImpl {

    // keep this in alphabetical order
    private static final String[] KIE_MODULES = new String[] {
            "", // This is to reserve the path META-INF/kie/kie.conf for user specific customizations
            "drools-alphanetwork-compiler", "drools-beliefs", "drools-compiler", "drools-core", "drools-decisiontables",
            "drools-kiesession", "drools-metric", "drools-model-compiler", "drools-mvel", "drools-persistence-jpa",
            "drools-scorecards", "drools-serialization-protobuf", "drools-tms", "drools-traits", "drools-wiring-dynamic", "drools-workbench-models-guided-dtable",
            "drools-workbench-models-guided-scorecard", "drools-workbench-models-guided-template", "drools-xml-support",
            "jbpm-bpmn2", "jbpm-case-mgmt-cmmn", "jbpm-flow", "jbpm-flow-builder", "jbpm-human-task-jpa",
            "kie-ci", "kie-dmn-core", "kie-dmn-jpmml", "kie-internal", "kie-pmml", "kie-pmml-evaluator-assembler",
            "kie-pmml-evaluator-core", "kie-server-services-jbpm-cluster"
    };

    private static final Logger log = LoggerFactory.getLogger(ServiceDiscoveryImpl.class);

    private static final String CONF_FILE_FOLDER = "META-INF/kie";
    private static final String CONF_FILE_NAME = "kie.conf";

    public static final String LEGACY_CONF_FILE = "META-INF/kie.conf";

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
            throw new IllegalStateException("Unable to add service '" + serviceName + "'. Services cannot be added once the ServiceDiscovery is sealed");
        }
    }

    public synchronized void reset() {
        cachedServices = null;
        sealed = false;
        services.reset();
    }

    public synchronized Map<String, List<Object>> getServices() {
        if (!sealed) {
            getKieConfs().ifPresent( kieConfs -> {
                for (URL kieConfUrl : kieConfs.resources) {
                    registerConfs( kieConfs.classLoader, kieConfUrl );
                }
            } );

            cachedServices = Collections.unmodifiableMap( buildMap() );
            sealed = true;
        }
        return cachedServices;
    }

    public void registerConfs( ClassLoader classLoader, URL url ) {
        log.debug("Loading kie.conf from {} in classloader {}", url, classLoader);

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
                    log.debug("Added child Service {}", value );
                } else {
                    String[] splitValues = value.split( ";" );
                    if (splitValues.length > 2) {
                        throw new RuntimeException( "Invalid kie.conf entry: " + value );
                    }
                    int priority = splitValues.length == 2 ? Integer.parseInt( splitValues[1].trim() ) : 0;
                    services.put( priority, serviceName, newInstance( classLoader, splitValues[0].trim() ) );
                    log.debug( "Added Service {} with priority {}", value, priority );
                }
            } catch (RuntimeException e) {
                if (optional) {
                    log.info("Cannot load service: {}",serviceName);
                } else {
                    log.error("Loading failed because {}", e.getMessage());
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
            log.debug( "Service {} is implemented by {}",  serviceEntry.getKey(), serviceEntry.getValue().get(0) );
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
                    log.trace( "Service {} is implemented by {}",  serviceEntry.getKey(), serviceEntry.getValue().get(0) );
                } else {
                    log.trace( "Service {} is implemented (in order of priority) by {}", serviceEntry.getKey(), serviceEntry.getValue() );
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
            Collection<URL> resources = findKieConfUrls( cl );
            return resources.isEmpty() ? null : new KieConfs( cl, resources );
        } catch (IOException e) {
            return null;
        }
    }

    private static class KieConfs {
        private final ClassLoader classLoader;
        private final Collection<URL> resources;

        private KieConfs( ClassLoader classLoader, Collection<URL> confResources ) {
            this.classLoader = classLoader;
            this.resources = confResources;
        }
    }

    private static class PriorityMap<K,V> {
        private final Map<K, TreeMap<Integer, V>> priorityMap = new HashMap<>();


        public void reset() {
            priorityMap.clear();
        }

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

    private static Collection<URL> findKieConfUrls(ClassLoader cl) throws IOException {
        List<URL> kieConfsUrls = new ArrayList<>();

        Enumeration<URL> metaInfs = cl.getResources(CONF_FILE_FOLDER);
        while (metaInfs.hasMoreElements()) {
            URL metaInf = metaInfs.nextElement();
            if (metaInf.getProtocol().startsWith("vfs")) {
                // the kie.conf discovery mechanism doesn't work under JBoss vfs
                kieConfsUrls.clear();
                break;
            }

            URLConnection con = metaInf.openConnection();
            if (con instanceof JarURLConnection) {
                collectKieConfsInJar(kieConfsUrls, metaInf, (JarURLConnection) con);
            } else {
                collectKieConfsInFile(kieConfsUrls, new File(metaInf.getFile()));
            }
        }

        if (kieConfsUrls.isEmpty()) {
            // no kie-conf found so fallback to the hardcoded lookup
            kieConfsUrls = getKieConfsFromKnownModules(cl).collect(Collectors.toList());
        } else {
            // check if all discovered kie.conf file are in known modules
            List<String> notRegisteredModules = kieConfsUrls.stream().map(ServiceDiscoveryImpl::getModuleName)
                    .filter(module -> Arrays.binarySearch(KIE_MODULES, module) < 0)
                    .collect(Collectors.toList());
            if (!notRegisteredModules.isEmpty()) {
                throw new IllegalStateException("kie.conf file discovered for modules " + notRegisteredModules +
                        " but not listed among the known modules. This will not work under OSGi or JBoss vfs.");
            }
        }

        // also check the legacy META-INF/kie.conf for backward compatibility
        Enumeration<URL> kieConfEnum = cl.getResources(LEGACY_CONF_FILE);
        while (kieConfEnum.hasMoreElements()) {
            kieConfsUrls.add(kieConfEnum.nextElement());
        }

        if (log.isDebugEnabled()) {
            log.debug("Discovered kie.conf files: " + kieConfsUrls);
        }

        return kieConfsUrls;
    }

    public static Stream<URL> getKieConfsFromKnownModules(ClassLoader cl) {
        return Stream.of(KIE_MODULES)
                .map(module -> cl.getResource(CONF_FILE_FOLDER + "/" + module + (module.length() > 0 ? "/" : "") + CONF_FILE_NAME))
                .filter(Objects::nonNull);
    }

    private static void collectKieConfsInJar(List<URL> kieConfsUrls, URL metaInf, JarURLConnection con) throws IOException {
        JarURLConnection jarCon = con;
        JarFile jarFile = jarCon.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().endsWith(CONF_FILE_NAME)) {
                String metaInfString = metaInf.toString();
                // Adding +1 to length if the metaInf ends with / , need to count with it to properly calculate kie.conf URL
                int confFileFolderLength = metaInfString.endsWith("/") ? CONF_FILE_FOLDER.length() + 1 : CONF_FILE_FOLDER.length();
                kieConfsUrls.add(new URL(metaInfString.substring(0, metaInfString.length() - confFileFolderLength) + entry.getName()));
            }
        }
    }

    private static void collectKieConfsInFile(List<URL> kieConfsUrls, File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                collectKieConfsInFile(kieConfsUrls, child);
            }
        } else {
            if (file.toString().endsWith(CONF_FILE_NAME)) {
                kieConfsUrls.add(file.toURI().toURL());
            }
        }
    }

    private static String getModuleName(URL url) {
        String s = url.toString();
        int moduleStart = s.indexOf(CONF_FILE_FOLDER) + CONF_FILE_FOLDER.length() + 1;
        return s.substring(moduleStart, s.length() - (CONF_FILE_NAME.length()+1));
    }
}