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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.aether.artifact.Artifact;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.utils.ClassLoaderResolver;
import org.kie.maven.integration.ArtifactResolver;
import org.kie.util.maven.support.DependencyFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MavenClassLoaderResolver implements ClassLoaderResolver {
    private static final ProtectionDomain  PROTECTION_DOMAIN;
    
    private static final Logger logger = LoggerFactory.getLogger(MavenClassLoaderResolver.class);

    static {
        PROTECTION_DOMAIN = (ProtectionDomain) AccessController.doPrivileged( new PrivilegedAction() {

            public Object run() {
                return MavenClassLoaderResolver.class.getProtectionDomain();
            }
        } );
    }
    

    @Override
    public ClassLoader getClassLoader(KieModule kmodule) {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = ClassLoader.getSystemClassLoader();
        }
        if (parent == null) {
            parent = MavenClassLoaderResolver.class.getClassLoader();
        }

        InternalKieModule internalKModule = (InternalKieModule)kmodule;
        Collection<ReleaseId> jarDependencies;

        ArtifactResolver resolver = null;
        if (internalKModule.getPomModel() == null) {
            // if the kmodule doesn't have a pom try to determine its dependencies through maven
            resolver = ArtifactResolver.getResolverFor( internalKModule.getPomModel() );
            jarDependencies = getJarDependencies(resolver, kmodule.getReleaseId(), DependencyFilter.COMPILE_FILTER);
        } else {
            jarDependencies = internalKModule.getJarDependencies( DependencyFilter.COMPILE_FILTER );
        }

        if (jarDependencies.isEmpty()) {
            return parent;
        }

        if (resolver == null) {
            resolver = ArtifactResolver.getResolverFor( internalKModule.getPomModel() );
        }

        List<URL> urls = new ArrayList<>();
        List<ReleaseId> unresolvedDeps = new ArrayList<>();

        for (ReleaseId rid : jarDependencies) {
            try {
                Artifact artifact = resolver.resolveArtifact(rid);
                if( artifact != null ) {
                    File jar = artifact.getFile(); 
                    urls.add( jar.toURI().toURL() );
                } else {
                    logger.error( "Dependency artifact not found for: " + rid );
                    unresolvedDeps.add(rid);
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        internalKModule.setUnresolvedDependencies(unresolvedDeps);
        return new KieURLClassLoader(urls.toArray(new URL[urls.size()]), parent);
    }

    private Collection<ReleaseId> getJarDependencies(ArtifactResolver resolver, ReleaseId releaseId, DependencyFilter filter) {
        return resolver.getArtifactDependecies(releaseId.toString()).stream()
                .filter( dep -> filter.accept( dep.getReleaseId(), dep.getScope() ) )
                .map( dep -> dep.getReleaseId() )
                .collect( Collectors.toList() );
    }
}
