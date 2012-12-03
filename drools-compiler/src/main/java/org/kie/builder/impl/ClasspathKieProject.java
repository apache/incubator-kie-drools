package org.kie.builder.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.cdi.KProjectExtension;
import org.drools.core.util.StringUtils;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.KieBaseModelImpl;
import org.drools.kproject.KieProjectModelImpl;
import org.drools.kproject.KieSessionModelImpl;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModule;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Discovers all KieJars on the classpath, via the kproject.xml file. 
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieJar is added to the KieRepository
 *
 */
public class ClasspathKieProject
    implements
    KieProject{

    private static final Logger             log               = LoggerFactory.getLogger( KProjectExtension.class );

    private Map<GAV, InternalKieModule>     kJars             = new HashMap<GAV, InternalKieModule>();

    private Map<String, InternalKieModule>  kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private Map<String, KieBaseModel>       kBaseModels       = new HashMap<String, KieBaseModel>();
    private Map<String, KieSessionModel>    kSessionModels    = new HashMap<String, KieSessionModel>();

    private KieRepository                   kr;

    public ClasspathKieProject() {
        this( KieServices.Factory.get().getKieRepository() );
    }

    public ClasspathKieProject(KieRepository kr) {
        this.kr = kr;
    }
    
    public void verify() {
        discoverKieJars();
        AbstractKieModules.indexParts( kJars, kBaseModels, kSessionModels, kJarFromKBaseName );
    }

    public GAV getGAV() {
        return null;
    }

    public void discoverKieJars() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final Enumeration<URL> e;
        try {
            e = classLoader.getResources( "META-INF/kproject.xml" );
        } catch ( IOException exc ) {
            log.error( "Unable to find and build index of kproject.xml \n" + exc.getMessage() );
            return;
        }

        List<KieProjectModel> kProjects = new ArrayList<KieProjectModel>();

        // Map of kproject urls
        Map<KieProjectModel, String> urls = new IdentityHashMap<KieProjectModel, String>();
        while ( e.hasMoreElements() ) {
            URL url = e.nextElement();
            try {
                KieProjectModel kieProject = KieProjectModelImpl.fromXML( url );
                kProjects.add( kieProject );

                String fixedURL = fixURL( url );
                urls.put( kieProject,
                          fixedURL );

                String pomProperties = getPomProperties( fixedURL );
                GAV gav = GAVImpl.fromPropertiesString( pomProperties );

                String rootPath = fixedURL;
                if ( rootPath.lastIndexOf( ':' ) > 0 ) {
                    rootPath = fixedURL.substring( rootPath.lastIndexOf( ':' ) + 1 );
                }

                InternalKieModule kJar = null;
                File file = new File( rootPath );
                if ( fixedURL.endsWith( ".jar" ) ) {
                    kJar = new ZipKieModule( gav,
                                          kieProject,
                                          file );
                } else if ( file.isDirectory() ) {
                    kJar = new FileKieModule( gav,
                                           kieProject,
                                           file );
                } else {
                    // if it's a file it must be zip and end with .jar, otherwise we log an error
                    log.error( "Unable to build index of kproject.xml url=" + url.toExternalForm() + "\n" );
                    continue;
                }
                kJars.put( gav,
                           kJar );

                kr.addKieJar( kJar );

            } catch ( Exception exc ) {
                log.error( "Unable to build index of kproject.xml url=" + url.toExternalForm() + "\n" + exc.getMessage() );
            }
        }
    }

    public String getPomProperties(String urlPathToAdd) {
        String rootPath = urlPathToAdd;
        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
            rootPath = urlPathToAdd.substring( rootPath.lastIndexOf( ':' ) + 1 );
        }

        if ( urlPathToAdd.endsWith( ".jar" ) ) {
            File actualZipFile = new File( rootPath );
            if ( !actualZipFile.exists() ) {
                log.error( "Unable to load pom.properties from" + urlPathToAdd + " as jarPath cannot be found\n" + rootPath );
            }

            ZipFile zipFile = null;

            try {
                zipFile = new ZipFile( actualZipFile );

                String file = KieBuilderImpl.findPomProperties( zipFile );
                if ( file == null ) {
                    throw new IOException();
                }
                ZipEntry zipEntry = zipFile.getEntry( file );

                return StringUtils.readFileAsString( new InputStreamReader( zipFile.getInputStream( zipEntry ) ) );
            } catch ( Exception e ) {
                log.error( "Unable to load pom.properties from" + urlPathToAdd + "\n" + e.getMessage() );
            } finally {
                try {
                    zipFile.close();
                } catch ( IOException e ) {
                    log.error( "Error when closing InputStream to " + urlPathToAdd + "\n" + e.getMessage() );
                }
            }
        } else {
            FileReader reader = null;
            try {
                File file = KieBuilderImpl.findPomProperties( new File( rootPath ) );
                if ( file == null ) {
                    throw new IOException();
                }
                reader = new FileReader( file );
                return StringUtils.toString( reader );
            } catch ( Exception e ) {
                log.error( "Unable to load pom.properties from" + urlPathToAdd + "\n" + e.getMessage() );
            } finally {
                if ( reader != null ) {
                    try {
                        reader.close();
                    } catch ( IOException e ) {
                        log.error( "Error when closing InputStream to " + urlPathToAdd + "\n" + e.getMessage() );
                    }
                }
            }
        }
        log.error( "Unable to load pom.properties from" + urlPathToAdd );
        return null;
    }

    private String fixURL(URL url) {
        String urlPath = url.toExternalForm();

        // determine resource type (eg: jar, file, bundle)
        String urlType = "file";
        int colonIndex = urlPath.indexOf( ":" );
        if ( colonIndex != -1 ) {
            urlType = urlPath.substring( 0,
                                         colonIndex );
        }

        urlPath = url.getPath();

        if ( "jar".equals( urlType ) ) {
            // switch to using getPath() instead of toExternalForm()
            if ( urlPath.indexOf( '!' ) > 0 ) {
                urlPath = urlPath.substring( 0,
                                             urlPath.indexOf( '!' ) );
            }
        } else {
            urlPath = urlPath.substring( 0,
                                         urlPath.length() - "/META-INF/kproject.xml".length() );
        }

        // remove any remaining protocols, normally only if it was a jar
        colonIndex = urlPath.lastIndexOf( ":" );
        if ( colonIndex >= 0 ) {
            urlPath = urlPath.substring( colonIndex + 1 );
        }

        try {
            urlPath = URLDecoder.decode( urlPath,
                                         "UTF-8" );
        } catch ( UnsupportedEncodingException e ) {
            throw new IllegalArgumentException( "Error decoding URL (" + url + ") using UTF-8",
                                                e );
        }

        log.debug( "KieProject URL Type + URL: " + urlType + ":" + urlPath );

        return urlPath;
    }

    public InternalKieModule getKieModuleForKBase(String kBaseName) {
        return this.kJarFromKBaseName.get( kBaseName );
    }

    public boolean kieBaseExists(String kBaseName) {
        return kBaseModels.containsKey( kBaseName );
    }

    public boolean kieSessionExists(String kSessionName) {
        return kSessionModels.containsKey( kSessionName );
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kBaseModels.get( kBaseName );
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kSessionModels.get( kSessionName );
    }
}
