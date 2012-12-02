package org.kie.builder.impl;

import static org.drools.kproject.KieBaseModelImpl.getFiles;

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

import javax.enterprise.context.spi.CreationalContext;

import org.drools.cdi.KProjectExtension;
import org.drools.cdi.KProjectExtension.KBaseBean;
import org.drools.core.util.StringUtils;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.kproject.GAVImpl;
import org.drools.kproject.KieBaseModelImpl;
import org.drools.kproject.KieProjectModelImpl;
import org.drools.kproject.KieSessionModelImpl;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieJar;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathKieContainer
    implements
    InternalKieContainer { 

    private static final Logger          log               = LoggerFactory.getLogger( KProjectExtension.class );

    private Map<GAV, InternalKieJar>     kieJars           = new HashMap<GAV, InternalKieJar>();

    private Map<String, InternalKieJar>  kJarFromKBaseName = new HashMap<String, InternalKieJar>();

    private Map<String, KieBaseModel>    kBaseModels       = new HashMap<String, KieBaseModel>();
    private Map<String, KieSessionModel> kSessionModels    = new HashMap<String, KieSessionModel>();

    private Map<String, KieBase>         kBases            = new HashMap<String, KieBase>();

    KieRepositoryImpl                    kr;

    public ClasspathKieContainer() {
        this(null);
    }
    
    public ClasspathKieContainer(KieRepositoryImpl kr) {
        this.kr = kr;
        discoverKieJars();
        indexParts();
    }

    public GAV getGAV() {
        return null;
    }

    public void updateToVersion(GAV version) {
        //throw new UnsupportedOperationException( "The " + getClass().getSimpleName() + " cannot be updated" );
    }

    public void updateKieJar(KieJar kieJar) {
        //throw new UnsupportedOperationException( "The " + getClass().getSimpleName() + " cannot be updated" );
    }

    public KieBase getKieBase() {
        return getKieBase( KieBaseModelImpl.DEFAULT_KIEBASE_NAME );
    }

    public KieBase getKieBase(String kBaseName) {
        KieBase kBase = kBases.get( kBaseName );
        if ( kBase == null ) {
            kBase = createKieBase( kBaseModels.get( kBaseName ) );
        }
        return kBase;
    }

    public KieSession getKieSession() {
        return null;
    }

    public KieSession getKieSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = ( KieSessionModelImpl ) kSessionModels.get( kSessionName );
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        return kBase.newKieSession();
    }

    public StatelessKieSession getKieStatelessSession(String kSessionName) {
        KieSessionModelImpl kSessionModel = ( KieSessionModelImpl ) kSessionModels.get( kSessionName );
        KieBase kBase = getKieBase( kSessionModel.getKieBaseModel().getName() );
        return kBase.newStatelessKieSession();
    }

    @Override
    public StatelessKieSession getKieStatelessSession() {
        // TODO Auto-generated method stub
        return null;
    }

    public void dispose() {
        // TODO: should it store all the KieSession created from this container and then dispose them?
        //kieJar = null;
    }

    //    private InternalKieJar loadKieJar() {
    //        if ( kieJar == null ) {
    //            kieJar = (InternalKieJar) KieServices.Factory.get().getKieRepository().getKieJar(gav);
    //            if ( kieJar == null ) {
    //                throw new RuntimeException("It doesn't exist any KieJar with gav: " + gav);
    //            }
    //        }
    //        return kieJar;
    //    }

    //    private KieBaseModel getKieBaseForSession(String kSessionName) {
    //        return loadKieJar().getKieBaseForSession(kSessionName);
    //    }
    //
    //    private KnowledgeSessionConfiguration getKnowledgeSessionConfiguration(KieBaseModel kieBaseModel,
    //                                                                           String ksessionName) {
    //        KieSessionModel kieSessionModel = kieBaseModel.getKieSessionModels().get( ksessionName );
    //        KnowledgeSessionConfiguration ksConf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
    //        ksConf.setOption( kieSessionModel.getClockType() );
    //        return ksConf;
    //    }

    public void indexParts() {
        for ( InternalKieJar kieJar : kieJars.values() ) {
            KieProjectModel kieProject = kieJar.getKieProjectModel();
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                kBaseModels.put( kieBaseModel.getName(),
                                 kieBaseModel );
                ((KieBaseModelImpl) kieBaseModel).setKProject( kieProject ); // should already be set, but just in case

                kJarFromKBaseName.put( kieBaseModel.getName(),
                                       kieJar );
                for ( KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values() ) {
                    ((KieSessionModelImpl) kieSessionModel).setKBase( kieBaseModel ); // should already be set, but just in case
                    kSessionModels.put( kieSessionModel.getName(),
                                        kieSessionModel );
                }
            }
        }
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

                InternalKieJar kieJar = null;
                File file = new File( rootPath );
                if ( fixedURL.endsWith( ".jar" ) ) {
                    kieJar = new ZipKieJar( gav,
                                            kieProject,
                                            file );
                } else if ( file.isDirectory() ) {
                    kieJar = new FileKieJar( gav,
                                             kieProject,
                                             file );
                } else {
                    // if it's a file it must be zip and end with .jar, otherwise we log an error
                    log.error( "Unable to build index of kproject.xml url=" + url.toExternalForm() + "\n" );
                    continue;
                }
                kieJars.put( gav,
                             kieJar );

            } catch ( Exception exc ) {
                log.error( "Unable to build index of kproject.xml url=" + url.toExternalForm() + "\n" + exc.getMessage() );
            }
        }
    }

    public KieBase createKieBase(KieBaseModel kBaseModel) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        Set<String> includes = kBaseModel.getIncludes();
        if ( includes != null && !includes.isEmpty() ) {
            for ( String include : includes ) {
                InternalKieJar includeJar = kJarFromKBaseName.get( include );
                if ( includeJar == null ) {
                    log.error( "Unable to build KieBase, could not find include: " + include );
                    return null;
                }
                addFiles( ckbuilder,
                          kBaseModels.get( include ),
                          includeJar );
            }
        }
        
        InternalKieJar includeJar = kJarFromKBaseName.get( kBaseModel.getName() );
        addFiles( ckbuilder,
                  kBaseModel,
                  includeJar );

        ckbuilder.build();

        if ( kbuilder.hasErrors() ) {
            log.error( "Unable to build KieBaseModel:" + kBaseModel.getName() + "\n" + kbuilder.getErrors().toString() );
        }

        InternalKnowledgeBase kBase = ( InternalKnowledgeBase ) KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return kBase;
    }

    private void addFiles(CompositeKnowledgeBuilder ckbuilder,
                          KieBaseModel kieBaseModel,
                          InternalKieJar kieJar) {
        int fileCount = 0;
        String prefixPath = kieBaseModel.getName().replace( '.', '/' );
        for ( String fileName : kieJar.getFileNames() ) {
            if ( fileName.startsWith( prefixPath ) ) {
                String upperCharName = fileName.toUpperCase();
                
                if ( upperCharName.endsWith( "DRL" ) ) {
                    ckbuilder.add( ResourceFactory.newByteArrayResource( kieJar.getBytes( fileName ) ),
                                   ResourceType.DRL );
                    fileCount++;
                } else if ( upperCharName.endsWith( "BPMN2" ) ) {
                    ckbuilder.add( ResourceFactory.newByteArrayResource( kieJar.getBytes( fileName ) ),
                                   ResourceType.DRL );
                    fileCount++;
                }
            }
        }
        if ( fileCount == 0 ) {
            log.warn( "No files found for KieBase " + kieBaseModel.getName() + ", searching folder " + kieJar.getFile() );
        }        

    }

//    private void addFiles2(CompositeKnowledgeBuilder ckbuilder,
//                           KieBaseModel kieBaseModel,
//                           String urlPathToAdd) {
//        String rootPath = urlPathToAdd;
//        if ( rootPath.lastIndexOf( ':' ) > 0 ) {
//            rootPath = urlPathToAdd.substring( rootPath.lastIndexOf( ':' ) + 1 );
//        }
//
//        if ( urlPathToAdd.endsWith( ".jar" ) ) {
//            File actualZipFile = new File( rootPath );
//            if ( !actualZipFile.exists() ) {
//                log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + " as jarPath cannot be found\n" + rootPath );
//                // return KnowledgeBaseFactory.newKnowledgeBase();
//            }
//
//            ZipFile zipFile = null;
//            try {
//                zipFile = new ZipFile( actualZipFile );
//            } catch ( Exception e ) {
//                log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + " as jar cannot be opened\n" + e.getMessage() );
//                // return KnowledgeBaseFactory.newKnowledgeBase();
//            }
//
//            try {
//                for ( String file : getFiles( kieBaseModel.getName(),
//                                              zipFile ) ) {
//                    ZipEntry zipEntry = zipFile.getEntry( file );
//                    ckbuilder.add( ResourceFactory.newInputStreamResource( zipFile.getInputStream( zipEntry ) ),
//                                   ResourceType.DRL );
//                }
//            } catch ( Exception e ) {
//                try {
//                    zipFile.close();
//                } catch ( IOException e1 ) {
//
//                }
//                log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + " as jar cannot be read\n" + e.getMessage() );
//                // return KnowledgeBaseFactory.newKnowledgeBase();
//            }
//        } else {
//            try {
//                File kieBaseRootPath = new File( rootPath,
//                                                 kieBaseModel.getName().replace( '.',
//                                                                                 '/' ) );
//                int fileCount = 0;
//                for ( String file : getFiles( kieBaseRootPath ) ) {
//                    ckbuilder.add( ResourceFactory.newFileResource( new File( kieBaseRootPath,
//                                                                              file ) ),
//                                   ResourceType.DRL );
//                    fileCount++;
//                }
//                if ( fileCount == 0 ) {
//                    log.warn( "No files found for KieBase " + kieBaseModel.getName() + ", searching folder " + kieBaseRootPath );
//                }
//            } catch ( Exception e ) {
//                log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + "\n" + e.getMessage() );
//            }
//        }
//    }

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
    
    public boolean kieBaseExists(String kieBaseName) {
        return kBaseModels.containsKey( kieBaseName );
    }

    public boolean kieSessionExists(String kieSessionName) {
        return kSessionModels.containsKey( kieSessionName );
    }

    public KieBaseModel getKieBaseModel(String kieBaseName) {
        return kBaseModels.get( kieBaseName );
    }
    
    public KieSessionModel getKieSessionModel(String kSessionName) {
        return kSessionModels.get( kSessionName );
    }
}
