package org.kie.scanner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.internal.utils.ClassLoaderResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;


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
        
        Collection<ReleaseId> jarDependencies = ((InternalKieModule)kmodule).getJarDependencies();
        ArtifactResolver resolver = ArtifactResolver.getResolverFor(kmodule.getReleaseId(),true);
        
        URL[] urls = new URL[jarDependencies.size()];
        int i = 0;
        for (ReleaseId rid : jarDependencies) {
            try {
                Artifact artifact = resolver.resolveArtifact(rid.toString());
                if( artifact != null ) {
                    File jar = artifact.getFile(); 
                    urls[i++] = jar.toURI().toURL();
                } else {
                    logger.warn( "Dependency artifact not found for: "+rid );
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        ClassLoader classLoader = new URLClassLoader(urls, parent);

        return classLoader;
    }

}
