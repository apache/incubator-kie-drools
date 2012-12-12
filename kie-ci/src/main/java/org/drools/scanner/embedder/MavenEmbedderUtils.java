package org.drools.scanner.embedder;

import org.apache.tools.ant.AntClassLoader;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;

public class MavenEmbedderUtils {
    
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
        
        ClassRealm classRealm = new ClassRealm( world, "plexus.core", parentClassLoader == null ? antClassLoader : parentClassLoader );

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
        .setRealm( classRealm ).setClassWorld( world );
        
        return buildPlexusContainer(mavenRequest,conf);
    }

    public static PlexusContainer buildPlexusContainer(ClassLoader mavenClassLoader, ClassLoader parent, MavenRequest mavenRequest) throws MavenEmbedderException {
        DefaultContainerConfiguration conf = new DefaultContainerConfiguration();
        
        conf.setAutoWiring( mavenRequest.isContainerAutoWiring() );
        conf.setClassPathScanning( mavenRequest.getContainerClassPathScanning() );
        conf.setComponentVisibility( mavenRequest.getContainerComponentVisibility() );

        conf.setContainerConfigurationURL( mavenRequest.getOverridingComponentsXml() );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm classRealm = new ClassRealm( classWorld, "maven", mavenClassLoader );
        classRealm.setParentRealm( new ClassRealm( classWorld, "maven-parent",
                                                   parent == null ? Thread.currentThread().getContextClassLoader()
                                                                   : parent ) );
        conf.setRealm( classRealm );

        conf.setClassWorld( classWorld );
        
        return buildPlexusContainer(mavenRequest,conf);
    }

    private static PlexusContainer buildPlexusContainer(MavenRequest mavenRequest,ContainerConfiguration containerConfiguration )
        throws MavenEmbedderException {
        try
        {
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
}
