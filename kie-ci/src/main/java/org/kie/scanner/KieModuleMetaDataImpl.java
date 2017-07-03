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
*/

package org.kie.scanner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.appformer.maven.integration.ArtifactResolver;
import org.appformer.maven.integration.DependencyDescriptor;
import org.appformer.maven.support.AFReleaseId;
import org.appformer.maven.support.DependencyFilter;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeMetaInfo;
import org.eclipse.aether.artifact.Artifact;

import static org.appformer.maven.integration.ArtifactResolver.getResolverFor;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;
import static org.drools.core.util.IoUtils.UTF8_CHARSET;
import static org.drools.core.util.IoUtils.readBytesFromZipEntry;

public class KieModuleMetaDataImpl implements KieModuleMetaData {

    private final Map<String, Collection<String>> classes = new HashMap<String, Collection<String>>();

    private final Map<String, String> processes = new HashMap<String, String>();
    
    private final Map<URI, File> jars = new HashMap<URI, File>();

    private final Map<String, TypeMetaInfo> typeMetaInfos = new HashMap<String, TypeMetaInfo>();
    private final Map<String, Set<String>> rulesByPackage = new HashMap<String, Set<String>>();
    private final Set<String> packages = new HashSet<String>();

    private final DependencyFilter dependencyFilter;

    private ProjectClassLoader classLoader;

    private AFReleaseId releaseId;

    private InternalKieModule kieModule;

    public KieModuleMetaDataImpl(AFReleaseId releaseId, DependencyFilter dependencyFilter) {
        this.releaseId = releaseId;
        this.dependencyFilter = dependencyFilter;
        init(getResolverFor(releaseId, false));
    }

    public KieModuleMetaDataImpl(File pomFile, DependencyFilter dependencyFilter) {
        this.dependencyFilter = dependencyFilter;
        init(getResolverFor(pomFile));
    }

    public KieModuleMetaDataImpl(InternalKieModule kieModule, DependencyFilter dependencyFilter) {
        this.kieModule = kieModule;
        this.dependencyFilter = dependencyFilter;
        indexKieModule( kieModule );
        init(getResolverFor( kieModule.getPomModel() ));
    }

    public KieModuleMetaDataImpl( InternalKieModule kieModule, List<URI> dependencies ) {
        this.kieModule = kieModule;
        this.dependencyFilter = DependencyFilter.TAKE_ALL_FILTER;
        indexKieModule( kieModule );
        init(dependencies);
    }

    private void indexKieModule( InternalKieModule kieModule ) {
        for (String file : kieModule.getFileNames()) {
            if (!indexClass(file)) {
                if (file.endsWith( KieModuleModelImpl.KMODULE_INFO_JAR_PATH )) {
                    indexMetaInfo(kieModule.getBytes(file));
                }
            }
        }
    }

    public Collection<String> getPackages() {
        return packages;
    }

    public Collection<String> getClasses(String packageName) {
        Collection<String> classesInPkg = classes.get(packageName);
        return classesInPkg != null ? classesInPkg : Collections.<String>emptyList();
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
		return rulesPerPackage != null ? rulesPerPackage : Collections.<String>emptyList();
    }

    public ClassLoader getClassLoader() {
        if (classLoader == null) {
            URL[] urls = new URL[jars.size()];
            int i = 0;
            for (File jar : jars.values()) {
                try {
                    urls[i++] = jar.toURI().toURL();
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }

            classLoader = ProjectClassLoader.createProjectClassLoader(new URLClassLoader(urls, getClass().getClassLoader()));

            if (kieModule != null) {
                Map<String, byte[]> classes = kieModule.getClassesMap(true);
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

        if (releaseId != null) {
            addArtifact(artifactResolver.resolveArtifact(releaseId));
        }
        if ( kieModule != null ) {
            for ( AFReleaseId releaseId : kieModule.getPomModel().getDependencies(dependencyFilter) ) {
                addArtifact( artifactResolver.resolveArtifact( releaseId ) );
            }
        } else {
            for ( DependencyDescriptor dep : artifactResolver.getAllDependecies( dependencyFilter ) ) {
                addArtifact( artifactResolver.resolveArtifact( dep.getReleaseId() ) );
            }
        }

        packages.addAll(classes.keySet());
        packages.addAll(rulesByPackage.keySet());
    }

    private void init(List<URI> dependencies) {
        for (URI uri : dependencies) {
            addJar( new File(uri), uri );
        }
        packages.addAll(classes.keySet());
        packages.addAll(rulesByPackage.keySet());
    }

    private void addArtifact(Artifact artifact) {
        if (artifact != null && artifact.getExtension() != null && artifact.getExtension().equals("jar")) {
            File jarFile = artifact.getFile();
            addJar( jarFile, jarFile.toURI() );
        }
    }

    private void addJar( File jarFile, URI uri ) {
        if (!jars.containsKey(uri)) {
            jars.put(uri, jarFile);
            scanJar(jarFile);
        }
    }

    private void scanJar(File jarFile) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jarFile );
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                String pathName = entry.getName();
                if(pathName.endsWith("bpmn") || pathName.endsWith("bpmn2")){
                  processes.put(pathName, new String(readBytesFromZipEntry(jarFile, entry), UTF8_CHARSET));
                }
                if (!indexClass(pathName)) {
                    if (pathName.endsWith(KieModuleModelImpl.KMODULE_INFO_JAR_PATH)) {
                        indexMetaInfo(readBytesFromZipEntry(jarFile, entry));
                    }
                }
            }
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    private boolean indexClass(String pathName) {
        if (!pathName.endsWith(".class")) {
            return false;
        }

        int separator = pathName.lastIndexOf( '/' );
        String packageName = separator > 0 ? pathName.substring( 0, separator ).replace('/', '.') : "";
        String className = pathName.substring( separator + 1, pathName.length() - ".class".length() );

        Collection<String> pkg = classes.get(packageName);
        if (pkg == null) {
            pkg = new HashSet<String>();
            classes.put(packageName, pkg);
        }
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

}
