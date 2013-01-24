package org.kie.builder.impl;

import org.drools.core.util.StringUtils;
import org.drools.kproject.ReleaseIdImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.kproject.xml.MinimalPomParser;
import org.drools.kproject.xml.PomModel;
import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.builder.model.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.internal.utils.ClassLoaderUtil;
import org.kie.internal.utils.CompositeClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

/**
 * Discovers all KieModules on the classpath, via the kmodule.xml file.
 * KieBaseModels and KieSessionModels are then indexed, with helper lookups
 * Each resulting KieModule is added to the KieRepository
 *
 */
public class ClasspathKieProject extends AbstractKieProject {

    private static final Logger             log               = LoggerFactory.getLogger( ClasspathKieProject.class );

    private Map<ReleaseId, InternalKieModule>     kieModules        = new HashMap<ReleaseId, InternalKieModule>();

    private Map<String, InternalKieModule>  kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private KieRepository                   kr;
    
    private CompositeClassLoader            cl;

    public ClasspathKieProject() {
        this( KieServices.Factory.get().getRepository() );
    }

    public ClasspathKieProject(KieRepository kr) {
        this.kr = kr;
    }
    
    public void init() {
        this.cl = ClassLoaderUtil.getClassLoader( null, null, true );
        discoverKieModules();
        indexParts(kieModules, kJarFromKBaseName);
    }

    public ReleaseId getGAV() {
        return null;
    }

    public void discoverKieModules() {
        final Enumeration<URL> e;
        try {
            e = cl.getResources( KieModuleModelImpl.KMODULE_JAR_PATH );
        } catch ( IOException exc ) {
            log.error( "Unable to find and build index of kmodule.xml \n" + exc.getMessage() );
            return;
        }

        // Map of kmodule urls
        while ( e.hasMoreElements() ) {
            URL url = e.nextElement();
            System.out.println( "kmodules: " + url);
            try {
                String fixedURL = fixURLFromKProjectPath( url ); 
                InternalKieModule kModule = fetchKModule(url, fixedURL);

                ReleaseId releaseId = kModule.getReleaseId();
                kieModules.put(releaseId, kModule);

                log.debug( "Discovered classpath module " + releaseId.toExternalForm() );
                
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

        setDefaultsforEmptyKieModule(kieProject);

        String pomProperties = getPomProperties( fixedURL );
        
        ReleaseId releaseId = ReleaseIdImpl.fromPropertiesString(pomProperties);

        String rootPath = fixedURL;
        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
            rootPath = fixedURL.substring( rootPath.lastIndexOf( ':' ) + 1 );
        }

        InternalKieModule kJar;
        File file = new File( rootPath );
        if ( fixedURL.endsWith( ".jar" ) ) {
            kJar = new ZipKieModule(releaseId,
                                  kieProject,
                                  file );
        } else if ( file.isDirectory() ) {
            kJar = new FileKieModule(releaseId,
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
                    
                    ReleaseIdImpl gav = (ReleaseIdImpl) pomModel.getReleaseId();
                    
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

    @Override
    public CompositeClassLoader getClassLoader() {
        return this.cl;
    }
}
