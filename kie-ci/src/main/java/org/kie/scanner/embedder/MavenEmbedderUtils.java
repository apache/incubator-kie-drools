package org.kie.scanner.embedder;

import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.tools.ant.AntClassLoader;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.plexus.components.cipher.PlexusCipher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class MavenEmbedderUtils {

    private static boolean isIBM_JVM = System.getProperty("java.vendor").toLowerCase().contains("ibm");
    
    private MavenEmbedderUtils() { }
    
    public static ClassRealm buildClassRealm(File mavenHome, ClassWorld world, ClassLoader parentClassLoader )
        throws MavenEmbedderException {
        
        if ( mavenHome == null ) {
            throw new IllegalArgumentException( "mavenHome cannot be null" );
        }
        if ( !mavenHome.exists() ) {
            throw new IllegalArgumentException( "mavenHome '" + mavenHome.getPath() + "' doesn't seem to exist on this node (or you don't have sufficient rights to access it)" );
        }

        File libDirectory = new File( mavenHome, "lib" );
        if ( !libDirectory.exists() ) {
            throw new IllegalArgumentException( mavenHome.getPath() + " doesn't have a 'lib' subdirectory - thus cannot be a valid maven installation!" );
        }

        File[] jarFiles = libDirectory.listFiles( new FilenameFilter()
        {
            public boolean accept( File dir, String name ) {
                return name.endsWith( ".jar" );
            }
        } );

        AntClassLoader antClassLoader = new AntClassLoader( Thread.currentThread().getContextClassLoader(), false );

        for ( File jarFile : jarFiles ) {
            antClassLoader.addPathComponent( jarFile );
        }
        
        if (world == null) {
            world = new ClassWorld();
        }
        
        ClassRealm classRealm = createClassRealm( world, "plexus.core", parentClassLoader == null ? antClassLoader : parentClassLoader );

        for ( File jarFile : jarFiles ) {
            try {
                classRealm.addURL( jarFile.toURI().toURL() );
            } catch ( MalformedURLException e ) {
                throw new MavenEmbedderException( e.getMessage(), e );
            }
        }
        return classRealm;
    }
    
    public static PlexusContainer buildPlexusContainer(File mavenHome, MavenRequest mavenRequest) throws MavenEmbedderException {
        ClassWorld world = new ClassWorld("plexus.core", Thread.currentThread().getContextClassLoader());

        ClassRealm classRealm = MavenEmbedderUtils.buildClassRealm( mavenHome, world, Thread.currentThread().getContextClassLoader() );

        DefaultContainerConfiguration conf = new DefaultContainerConfiguration();

        conf.setContainerConfigurationURL( mavenRequest.getOverridingComponentsXml() )
            .setRealm(classRealm)
            .setClassWorld(world);
        
        return buildPlexusContainer(mavenRequest,conf);
    }

    public static PlexusContainer buildPlexusContainer(ClassLoader mavenClassLoader, ClassLoader parent, MavenRequest mavenRequest) throws MavenEmbedderException {
        DefaultContainerConfiguration conf = new DefaultContainerConfiguration();
        
        conf.setAutoWiring( mavenRequest.isContainerAutoWiring() );
        conf.setClassPathScanning( mavenRequest.getContainerClassPathScanning() );
        conf.setComponentVisibility( mavenRequest.getContainerComponentVisibility() );

        conf.setContainerConfigurationURL( mavenRequest.getOverridingComponentsXml() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm parentRealm = createParentRealm(classWorld, parent,
                                                   MavenExecutionRequestPopulator.class,
                                                   RepositorySystem.class,
                                                   PlexusCipher.class);

        ClassRealm classRealm = createClassRealm( classWorld, "maven", mavenClassLoader );
        classRealm.setParentRealm( parentRealm );
        conf.setRealm( classRealm );

        conf.setClassWorld( classWorld );
        
        return buildPlexusContainer(mavenRequest,conf);
    }

    private static ClassRealm createParentRealm(ClassWorld classWorld, ClassLoader parent, Class... requiredClasses) {
        ClassLoader parentCL = parent == null ? Thread.currentThread().getContextClassLoader() : parent;
        Set<ClassLoader> usedCLs = new HashSet<ClassLoader>();
        usedCLs.add(parentCL);

        ClassRealm parentRealm = createClassRealm( classWorld, "maven-parent", parentCL);

        int i = 1;
        ClassRealm lastParent = parentRealm;
        for ( Class c : requiredClasses ) {
            if ( usedCLs.add(c.getClassLoader()) ) {
                ClassRealm newParent = createClassRealm( classWorld, "maven-parent" + i++, c.getClassLoader() );
                lastParent.setParentRealm( newParent );
                lastParent = newParent;
            }
        }

        return parentRealm;
    }

    private static PlexusContainer buildPlexusContainer(MavenRequest mavenRequest,ContainerConfiguration containerConfiguration )
        throws MavenEmbedderException {
        try {
            DefaultPlexusContainer plexusContainer = new DefaultPlexusContainer( containerConfiguration );
            if (mavenRequest.getMavenLoggerManager() != null) {
                plexusContainer.setLoggerManager( mavenRequest.getMavenLoggerManager() );
            }
            if (mavenRequest.getLoggingLevel() > 0) {
                plexusContainer.getLoggerManager().setThreshold( mavenRequest.getLoggingLevel() );
            }
            return plexusContainer;
        } catch ( PlexusContainerException e ) {
            throw new MavenEmbedderException( e.getMessage(), e );
        }
    }

    private static ClassRealm createClassRealm(ClassWorld world, String id, ClassLoader baseClassLoader) {
        return isIBM_JVM ? new IBMClassRealm(world, id, baseClassLoader) : new ClassRealm(world, id, baseClassLoader);
    }

    public static class IBMClassRealm extends ClassRealm {
        public IBMClassRealm(ClassWorld world, String id, ClassLoader baseClassLoader) {
            super(world, id, baseClassLoader);
        }

        @Override
        public Enumeration findResources(String name) throws IOException {
            return getParent().getResources(name);
        }
    }
}
