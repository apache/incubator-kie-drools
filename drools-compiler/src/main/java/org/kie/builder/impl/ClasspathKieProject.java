package org.kie.builder.impl;

import org.drools.cdi.KProjectExtension;
import org.drools.core.util.StringUtils;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.xml.MinimalPomParser;
import org.drools.xml.PomModel;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieFactory;
import org.kie.builder.KieModule;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.util.ClassLoaderUtil;
import org.kie.util.CompositeClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Discovers all KieModules on the classpath, via the kmodule.xml file.
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieModule is added to the KieRepository
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
    
    private CompositeClassLoader            cl;

    public ClasspathKieProject() {
        this( KieServices.Factory.get().getKieRepository() );
    }

    public ClasspathKieProject(KieRepository kr) {
        this.kr = kr;
    }
    
    public void init() {
        this.cl = ClassLoaderUtil.getClassLoader( null, null, true );
        discoverKieModules();
        AbstractKieModule.indexParts( kJars, kBaseModels, kSessionModels, kJarFromKBaseName );
    }

    public GAV getGAV() {
        return null;
    }

    public void discoverKieModules() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final Enumeration<URL> e;
        try {
            e = classLoader.getResources( KieModuleModelImpl.KMODULE_JAR_PATH );
        } catch ( IOException exc ) {
            log.error( "Unable to find and build index of kmodule.xml \n" + exc.getMessage() );
            return;
        }

        List<KieModuleModel> kModules = new ArrayList<KieModuleModel>();

        // Map of kmodule urls
        Map<KieModuleModel, String> urls = new IdentityHashMap<KieModuleModel, String>();
        while ( e.hasMoreElements() ) {
            URL url = e.nextElement();
            try {
                String fixedURL = fixURLFromKProjectPath( url ); 
                InternalKieModule kModule = fetchKModule( url, fixedURL );
                KieModuleModel kModuleModel = kModule.getKieModuleModel();
                
                kModules.add( kModuleModel );
                urls.put( kModuleModel,
                          fixedURL );

                GAV gav = kModule.getGAV();
                kJars.put( gav,
                           kModule );

                log.debug( "Discovered classpath module " + gav.toExternalForm() );
                
                kr.addKieModule(kModule);

            } catch ( Exception exc ) {
                log.error( "Unable to build index of kmodule.xml url=" + url.toExternalForm() + "\n" + exc.getMessage() );
            }
        }
    }
    public static InternalKieModule fetchKModule(URL url) {
        return fetchKModule(url, fixURLFromKProjectPath(url));
    }
    
    public static InternalKieModule fetchKModule(URL url, String fixedURL) {
        KieModuleModel kieProject = KieModuleModelImpl.fromXML( url );

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
            log.error( "Unable to build index of kmodule.xml url=" + url.toExternalForm() + "\n" );
            kJar = null;
        }        
        return kJar;
    }    

    public static String getPomProperties(String urlPathToAdd) {
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

                String pomProps = StringUtils.readFileAsString( new InputStreamReader( zipFile.getInputStream( zipEntry ) ) ); 
                log.debug( "Found and used pom.properties " + file);
                return pomProps;
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
                log.debug( "Found and used pom.properties " + file);
                return StringUtils.toString( reader );
            } catch ( Exception e ) {
                log.warn( "Unable to load pom.properties tried recursing down from" + urlPathToAdd + "\n" + e.getMessage() );
            } finally {
                if ( reader != null ) {
                    try {
                        reader.close();
                    } catch ( IOException e ) {
                        log.error( "Error when closing InputStream to " + urlPathToAdd + "\n" + e.getMessage() );
                    }
                }
            }
            
            
            
            // recurse until we reach root or find a pom.xml
            File file = null;
            for ( File folder = new File( rootPath ); folder != null; folder = new File( folder.getParent() ) ) {
                file = new File( folder, "pom.xml" );
                if ( file.exists() ) {
                    break;
                }
                file = null;
            }
            
            if ( file != null ) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream( file ) ;
                    PomModel pomModel = MinimalPomParser.parse( rootPath + "/pom.xml",
                                                                    fis);
                    
                    KieBuilderImpl.validatePomModel( pomModel ); // throws an exception if invalid
                    
                    GAVImpl gav = ( GAVImpl ) KieFactory.Factory.get().newGav( pomModel.getGroupId(),
                                                                               pomModel.getArtifactId(),
                                                                               pomModel.getVersion() );
                    
                    String str =  KieBuilderImpl.generatePomProperties( gav );
                    log.info( "Recursed up folders,  found and used pom.xml " + file );
                    
                    return str;
                    
                } catch ( Exception e ) {
                    log.error( "As folder project tried to fall back to pom.xml " + file + "\nbut failed with exception:\n" + e.getMessage() );
                } finally {
                    if ( fis != null ) {
                        try {
                            fis.close();
                        } catch ( IOException e ) {
                            log.error( "Error when closing InputStream to " + file + "\n" + e.getMessage() );
                        }
                    }
                }
            } else {
                log.error( "As folder project tried to fall back to pom.xml, but could not find one for " + file );
            }
        }
        log.error( "Unable to load pom.properties from" + urlPathToAdd );
        return null;
    }
    
    public static String fixURLFromKProjectPath(URL url) {
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
                                         urlPath.length() - ("/" + KieModuleModelImpl.KMODULE_JAR_PATH).length() );
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

        log.debug( "KieModule URL type=" + urlType + " url=" + urlPath );

        return urlPath;
    }

    public InternalKieModule getKieModuleForKBase(String kBaseName) {
        return this.kJarFromKBaseName.get( kBaseName );
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kBaseModels.get( kBaseName );
    }

    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kSessionModels.get( kSessionName );
    }

    @Override
    public CompositeClassLoader getClassLoader() {
        return this.cl;
    }
}
