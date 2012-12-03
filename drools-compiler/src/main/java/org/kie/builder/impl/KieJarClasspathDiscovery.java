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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.core.util.StringUtils;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieModuleModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieJarClasspathDiscovery {
    private static final Logger        log              = LoggerFactory.getLogger( KieRepositoryImpl.class );    

    private Set<String>                  kBaseNames;
    private Set<String>                  kSessionNames;

    private Map<String, String>          kBaseURLs;
//    private Map<String, KieProject>      kProjects;
    private Map<String, KieBaseModel>    kBases;
    private Map<String, KieSessionModel> kSessions;
    
    public KieJarClasspathDiscovery() {
        
    }
    
    public void buildKProjects() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        final Enumeration<URL> e;
        try {
            e = classLoader.getResources( "META-INF/kproject.xml" );
        } catch ( IOException exc ) {
            log.error( "Unable to find and build index of kproject.xml \n" + exc.getMessage() );
            return;
        }

        List<KieModuleModel> kProjects = new ArrayList<KieModuleModel>();
        
        // Map of kproject urls
        Map<KieModuleModel, String> urls = new IdentityHashMap<KieModuleModel, String>();
        while ( e.hasMoreElements() ) {
            URL url = e.nextElement();;
            try {
                KieModuleModel kieProject = KieModuleModelImpl.fromXML( url );
                kProjects.add( kieProject );
                
                String fixedURL = fixURL( url );
                urls.put( kieProject,
                          fixedURL );
            } catch ( Exception exc ) {
                log.error( "Unable to build and build index of kproject.xml url=" + url.toExternalForm() + "\n" + exc.getMessage() );
            }
        }

        for ( KieModuleModel kieProject : kProjects ) {
            String url = urls.get( kieProject );
            String strGAV = getPomProperties(url);
            if ( StringUtils.isEmpty( strGAV  ) ) {
                continue;
            }
            
            GAV gav = null;
            try {
                gav = GAVImpl.fromPropertiesString( strGAV );
            } catch ( Exception ex ) {
                log.error( "Unable to build GAV for path "  +url  + "\n    {}", ex );
            }
            

            String rootPath = url;
            if ( rootPath.lastIndexOf( ':' ) > 0 ) {
                rootPath = url.substring( rootPath.lastIndexOf( ':' ) + 1 );
            }
            File file = new File( rootPath );            
            FileKieModule kieJar = new FileKieModule( gav, kieProject, file );
            
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                kBases.put( kieBaseModel.getName(),
                            kieBaseModel );
                ((KieBaseModelImpl) kieBaseModel).setKProject( kieProject ); // should already be set, but just in case
                
                kBaseURLs.put( kieBaseModel.getName(),
                               url );
                for ( KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values() ) {
                    ((KieSessionModelImpl) kieSessionModel).setKBase( kieBaseModel ); // should already be set, but just in case
                    kSessions.put( kieSessionModel.getName(),
                                   kieSessionModel );
                }
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
}
