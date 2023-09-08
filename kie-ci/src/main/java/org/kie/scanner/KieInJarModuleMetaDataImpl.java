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
package org.kie.scanner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.base.rule.KieModuleMetaInfo;
import org.drools.base.rule.TypeMetaInfo;
import org.drools.util.ClassUtils;
import org.drools.util.IoUtils;
import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.kie.api.builder.ReleaseId;
import org.kie.maven.integration.ArtifactResolver;
import org.kie.util.maven.support.DependencyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.enumeration;
import static java.util.stream.Collectors.toList;
import static org.drools.util.ClassUtils.convertResourceToClassName;
import static org.drools.util.IoUtils.UTF8_CHARSET;
import static org.kie.maven.integration.ArtifactResolver.getResolverFor;

public class KieInJarModuleMetaDataImpl implements KieModuleMetaData {

    private static final Logger log = LoggerFactory.getLogger(KieInJarModuleMetaDataImpl.class);

    private final Map<String, Collection<String>> classes = new HashMap<>();

    private final Map<String, String> processes = new HashMap<>();

    private final Map<String, String> forms = new HashMap<>();

    private final Set<URL> jars = new HashSet<>();

    private final Map<String, TypeMetaInfo> typeMetaInfos = new HashMap<>();
    private final Map<String, Set<String>> rulesByPackage = new HashMap<>();
    private final Set<String> packages = new HashSet<>();

    private final DependencyFilter dependencyFilter;

    private ProjectClassLoader classLoader;

    private ReleaseId releaseId;

    private InternalKieModule kieModule;

    public KieInJarModuleMetaDataImpl(ReleaseId releaseId, DependencyFilter dependencyFilter) {
        this.releaseId = releaseId;
        this.dependencyFilter = dependencyFilter;
        init(getResolverFor(getClass().getClassLoader(), releaseId, false));
    }

    public Collection<String> getPackages() {
        return packages;
    }

    public Collection<String> getClasses(String packageName) {
        Collection<String> classesInPkg = classes.get(packageName);
        return classesInPkg != null ? classesInPkg : Collections.emptyList();
    }

    public Class<?> getClass(String pkgName, String className) {
        try {
            return Class.forName((pkgName == null || pkgName.trim().length() == 0) ? className : pkgName + "." + className, false, getClassLoader());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public TypeMetaInfo getTypeMetaInfo(Class<?> clazz) {
        TypeMetaInfo typeMetaInfo = typeMetaInfos.get(clazz.getName());
        return typeMetaInfo != null ? typeMetaInfo : new TypeMetaInfo(clazz);
    }

    @Override
    public Collection<String> getRuleNamesInPackage(String packageName) {
        Set<String> rulesPerPackage = rulesByPackage.get(packageName);
        return rulesPerPackage != null ? rulesPerPackage : Collections.emptyList();
    }

    private static class InJarClassLoader extends ClassLoader {

        private URL[] urls;

        public InJarClassLoader(URL[] urls, ClassLoader parent) {
            super(parent);
            this.urls = urls;
        }

        private Class<?> getClass(String name) throws ClassNotFoundException {
            try {
                String file = ClassUtils.convertClassToResourcePath(name);
                Class<?> clazz = null;
                byte[] binaryClassDefinition = loadClassData(file);
                if (binaryClassDefinition != null) {
                    clazz = defineClass(name, binaryClassDefinition, 0, binaryClassDefinition.length);
                    resolveClass(clazz);
                }
                return clazz;
            } catch (IOException e) {
                return null;
            }
        }

        private byte[] loadClassData(String name) throws IOException {
            for (URL url : urls) {
                if (url == null) {
                    continue;
                }
                String prefix = "file".equals(url.getProtocol()) ? "jar:" : "";
                URL tryUrl = new URL(prefix + url.toString() + "!/" + name);
                try (InputStream stream = tryUrl.openStream()) {
                    if (stream == null) {
                        continue;
                    }
                    return IoUtils.readBytesFromInputStream(stream, false);
                } catch (IOException e) {
                    // do nothing
                }
            }
            return null;

        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            Class<?> clazz = getClass(name);
            return clazz != null ? clazz : super.loadClass(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            return enumeration(getJarResources(name));
        }

        @Override
        public URL getResource(String name) {
            List<URL> resources = getJarResources(name);
            return !resources.isEmpty() ? resources.get(0) : super.getResource(name);
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            List<URL> resources = getJarResources(name);
            try {
                return !resources.isEmpty() ? resources.get(0).openStream() : super.getResourceAsStream(name);
            } catch (IOException e) {
                return super.getResourceAsStream(name);
            }
        }

        private List<URL> getJarResources(String name) {
            List<URL> result = new ArrayList<>();
            for (URL url : urls) {
                try {
                    String urlToString = url.toString();
                    if (!urlToString.endsWith(".jar")) {
                        continue;
                    }
                    URL tmp;
                    if (!urlToString.startsWith("jar:")) {
                        tmp = new URL("jar:" + urlToString + "!/" + name);
                    } else {
                        tmp = new URL(urlToString + "!/" + name);
                    }
                    tmp.getContent();
                    log.debug("found {} in {}", tmp, urls);
                    result.add(tmp);
                } catch (IOException e) {
                    log.trace("Failed to load resource {} in {}", name, url);
                }
            }
            return result;
        }

    }

    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            URL[] urls = jars.stream().toArray(URL[]::new);

            String msg = jars.stream().map(e -> e.getFile()).collect(Collectors.joining("\n"));
            log.debug("SpringBoot In Jar ClassLoader:\n{}", msg);
            classLoader = ProjectClassLoader.createProjectClassLoader(new InJarClassLoader(urls, getClass().getClassLoader()));

            if (kieModule != null) {
                Map<String, byte[]> classes = kieModule.getClassesMap();
                for (Map.Entry<String, byte[]> entry : classes.entrySet()) {
                    classLoader.storeClass(convertResourceToClassName(entry.getKey()), entry.getKey(), entry.getValue());
                }
            }
        }
        return classLoader;
    }

    private void init(ArtifactResolver artifactResolver) {
        if (artifactResolver == null) {
            return;
        }

        List<ReleaseId> releasesId = new ArrayList<>();
        if (releaseId != null) {
            releasesId.add(releaseId);
        }
        if (kieModule != null && kieModule.getPomModel() != null) {
            releasesId.addAll(kieModule.getPomModel().getDependencies(dependencyFilter));

        } else {
            List<ReleaseId> dependencies = artifactResolver.getAllDependecies(dependencyFilter).stream().map(e -> e.getReleaseId()).collect(toList());
            releasesId.addAll(dependencies);

        }

        for (ReleaseId rId : releasesId) {
            ArtifactResolver.ArtifactLocation artifactLocation = artifactResolver.resolveArtifactLocation(rId);
            addArtifact(artifactLocation);
        }


        packages.addAll(classes.keySet());
        packages.addAll(rulesByPackage.keySet());
    }

    private void addArtifact(ArtifactResolver.ArtifactLocation artifactLocation) {
        if (artifactLocation != null && artifactLocation.getArtifact().getExtension() != null && artifactLocation.getArtifact().getExtension().equals("jar")) {
            addJar(artifactLocation.toURL());
        }
    }

    private void addJar(URL url) {
        if (!jars.contains(url)) {
            jars.add(url);
            scanJar(url);
        }
    }

    private void scanJar(URL jarFile) {
        try (ZipInputStream zipFile = new ZipInputStream(jarFile.openStream())) {

            ZipEntry entry;
            while ((entry = zipFile.getNextEntry()) != null) {
                int available = zipFile.available();
                if (available <= 0) {
                    continue;
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];

                int read;
                while ((read = zipFile.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }

                byte[] blob = out.toByteArray();
                if (blob.length == 0) {
                    continue;
                }
                String pathName = entry.getName();
                if (isProcessFile(pathName)) {
                    processes.put(pathName, new String(blob, UTF8_CHARSET));
                } else if (isFormFile(pathName)) {
                    forms.put(pathName, new String(blob, UTF8_CHARSET));
                }

                if (!indexClass(pathName) && pathName.endsWith(KieModuleModelImpl.KMODULE_INFO_JAR_PATH.asString())) {
                    indexMetaInfo(blob);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean indexClass(String pathName) {
        if (!pathName.endsWith(".class")) {
            return false;
        }

        int separator = pathName.lastIndexOf('/');
        String packageName = separator > 0 ? pathName.substring(0, separator).replace('/', '.') : "";
        String className = pathName.substring(separator + 1, pathName.length() - ".class".length());

        Collection<String> pkg = classes.computeIfAbsent(packageName, k -> new HashSet<>());
        pkg.add(className);
        return true;
    }

    private void indexMetaInfo(byte[] bytes) {
        KieModuleMetaInfo info = KieModuleMetaInfo.unmarshallMetaInfos(new String(bytes, UTF8_CHARSET));
        typeMetaInfos.putAll(info.getTypeMetaInfos());
        rulesByPackage.putAll(info.getRulesByPackage());
    }

    @Override
    public Map<String, String> getProcesses() {
        return processes;
    }

    @Override
    public Map<String, String> getForms() {
        return forms;
    }

    static boolean isFormFile(final String pathName) {
        return pathName.endsWith("frm");
    }

    static boolean isProcessFile(final String pathName) {
        return pathName.endsWith("bpmn") || pathName.endsWith("bpmn2") || pathName.endsWith("bpmn-cm");
    }
}
