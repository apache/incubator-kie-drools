package org.drools.cdi;

import org.drools.core.util.StringUtils;
import org.drools.kproject.KieBaseModelImpl;
import org.drools.kproject.KieProjectImpl;
import org.drools.kproject.KieSessionModelImpl;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProject;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.builder.impl.KieBuilderImpl;
import org.kie.cdi.KBase;
import org.kie.cdi.KSession;
import org.kie.io.ResourceFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.kproject.KieBaseModelImpl.getFiles;

public class KProjectExtension
    implements
    Extension {

    private static final Logger          log = LoggerFactory.getLogger( KProjectExtension.class );

    private Set<String>                  kBaseNames;
    private Set<String>                  kSessionNames;

    private Map<String, String>          kBaseURLs;
//    private Map<String, KieProject>      kProjects;
    private Map<String, KieBaseModel>    kBases;
    private Map<String, KieSessionModel> kSessions;

    public KProjectExtension() {
    }

    public void init() {
        kBaseURLs = new HashMap<String, String>();
//        kProjects = new HashMap<String, KieProject>();
        kBases = new HashMap<String, KieBaseModel>();
        kSessions = new HashMap<String, KieSessionModel>();
        buildKProjects();
    }

    <Object> void processInjectionTarget(@Observes ProcessInjectionTarget<Object> pit) {
        if ( kBases == null ) {
            init();
        }

        // Find all uses of KieBaseModel and KieSessionModel and add to Set index
        if ( !pit.getInjectionTarget().getInjectionPoints().isEmpty() ) {
            for ( InjectionPoint ip : pit.getInjectionTarget().getInjectionPoints() ) {
                KBase kBase = ip.getAnnotated().getAnnotation( KBase.class );
                if ( kBase != null ) {
                    if ( kBaseNames == null ) {
                        kBaseNames = new HashSet<String>();
                    }
                    kBaseNames.add( kBase.value() );
                }

                KSession kSession = ip.getAnnotated().getAnnotation( KSession.class );
                if ( kSession != null ) {
                    if ( kSessionNames == null ) {
                        kSessionNames = new HashSet<String>();
                    }
                    kSessionNames.add( kSession.value() );
                }
            }
        }

    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd,
                            BeanManager bm) {
        if ( kBases != null ) {
            // if kProjects null, processInjectionTarget was not called, so beans to create

            Map<String, KBaseBean> kBaseBeans = new HashMap<String, KProjectExtension.KBaseBean>();
            if ( kBaseNames != null ) {
                for ( String kBaseQName : kBaseNames ) {
                    KieBaseModel kieBaseModelModel = kBases.get( kBaseQName );
                    if ( kieBaseModelModel == null ) {
                        log.error( "Annotation @KBase(\"" + kBaseQName + "\") found, but no KieBaseModel exist.\nEither the required kproject.xml does not exist, was corrupted, or mising the KieBase entry" );
                        continue;
                    }
                    KBaseBean bean = new KBaseBean( kieBaseModelModel,
                                                    kBaseURLs.get( kBaseQName ),
                                                    kBaseBeans );
                    kBaseBeans.put( kBaseQName,
                                    bean );
                    abd.addBean( bean );
                }
            }
            kBaseNames = null;

            if ( kSessionNames != null ) {
                for ( String kSessionName : kSessionNames ) {
                    KieSessionModel kieSessionModel = kSessions.get( kSessionName );
                    if ( kieSessionModel == null ) {
                        throw new RuntimeException( "Unknown KnowledgeSession: " + kSessionName );
                    }
                    KBaseBean bean = kBaseBeans.get( ((KieSessionModelImpl) kieSessionModel).getKBase().getName() );
                    if ( "stateless".equals( kieSessionModel.getType() ) ) {
                        abd.addBean( new StatelessKSessionBean( kieSessionModel,
                                                                bean ) );
                    } else {
                        abd.addBean( new StatefulKSessionBean( kieSessionModel,
                                                               bean ) );
                    }
                }
            }
            kSessionNames = null;

            kBaseURLs = null;
            kBases = null;
            kSessions = null;
        }
    }

    public static class KBaseBean
        implements
        Bean<KnowledgeBase> {
        static final Set<Type>         types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KnowledgeBase.class,
                                                                                                              Object.class ) ) );

        private Set<Annotation>        qualifiers;

        private String                 urlPath;
        private KieBaseModel           kieBaseModelModel;

        private KnowledgeBase          kBase;

        private Map<String, KBaseBean> kBaseBeans;

        public KBaseBean(final KieBaseModel kieBaseModelModel,
                         String urlPath,
                         Map<String, KBaseBean> kBaseBeans) {
            this.kieBaseModelModel = kieBaseModelModel;
            this.urlPath = urlPath;
            this.kBaseBeans = kBaseBeans;
            this.qualifiers = Collections.unmodifiableSet( new HashSet<Annotation>( Arrays.asList( new AnnotationLiteral<Default>() {
                                                                                                   },
                                                                                                   new AnnotationLiteral<Any>() {
                                                                                                   },
                                                                                                   new KBase() {
                                                                                                       public Class< ? extends Annotation> annotationType() {
                                                                                                           return KBase.class;
                                                                                                       }

                                                                                                       public String value() {
                                                                                                           return kieBaseModelModel.getName();
                                                                                                       }
                                                                                                   }
                    ) ) );
        }

        public KnowledgeBase create(CreationalContext ctx) {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

            Set<String> includes = kieBaseModelModel.getIncludes();
            if ( includes != null && !includes.isEmpty() ) {
                for ( String include : includes ) {
                    KBaseBean includeBean = kBaseBeans.get( include );
                    addFiles( ckbuilder,
                              includeBean.getKieBaseModelModel(),
                              includeBean.getUrlPath() );
                }
            }
            addFiles( ckbuilder,
                      kieBaseModelModel,
                      urlPath );

            ckbuilder.build();

            this.kBase = KnowledgeBaseFactory.newKnowledgeBase();
            this.kBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

            return this.kBase;
        }

        private void addFiles(CompositeKnowledgeBuilder ckbuilder,
                              KieBaseModel kieBaseModel,
                              String urlPathToAdd) {
            String rootPath = urlPathToAdd;
            if ( rootPath.lastIndexOf( ':' ) > 0 ) {
                rootPath = urlPathToAdd.substring( rootPath.lastIndexOf( ':' ) + 1 );
            }

            if ( urlPathToAdd.endsWith( ".jar" ) ) {
                File actualZipFile = new File( rootPath );
                if ( !actualZipFile.exists() ) {
                    log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + " as jarPath cannot be found\n" + rootPath );
                    // return KnowledgeBaseFactory.newKnowledgeBase();
                }

                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile( actualZipFile );
                } catch ( Exception e ) {
                    log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + " as jar cannot be opened\n" + e.getMessage() );
                    // return KnowledgeBaseFactory.newKnowledgeBase();
                }

                try {
                    for ( String file : getFiles( kieBaseModel.getName(),
                                                  zipFile ) ) {
                        ZipEntry zipEntry = zipFile.getEntry( file );
                        ckbuilder.add( ResourceFactory.newInputStreamResource( zipFile.getInputStream( zipEntry ) ),
                                       ResourceType.DRL );
                    }
                } catch ( Exception e ) {
                    try {
                        zipFile.close();
                    } catch ( IOException e1 ) {

                    }
                    log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + " as jar cannot be read\n" + e.getMessage() );
                    // return KnowledgeBaseFactory.newKnowledgeBase();
                }
            } else {
                try {
                    File  kieBaseRootPath = new File(  rootPath, kieBaseModel.getName().replace( '.', '/' ) );
                    for ( String file : getFiles(kieBaseRootPath) ) {
                        ckbuilder.add( ResourceFactory.newFileResource( new File( kieBaseRootPath,
                                                                                  file ) ),
                                       ResourceType.DRL );
                    }
                } catch ( Exception e ) {
                    log.error( "Unable to build KieBaseModel:" + kieBaseModel.getName() + "\n" + e.getMessage() );
                }
            }
        }

        public KieBaseModel getKieBaseModelModel() {
            return kieBaseModelModel;
        }

        public String getUrlPath() {
            return urlPath;
        }

        public KnowledgeBase getKnowledgeBase() {
            if ( this.kBase == null ) {
                create( null );
            }
            return this.kBase;
        }

        public void destroy(KnowledgeBase kBase,
                            CreationalContext ctx) {
            this.kBase = null;

            ctx.release();
        }

        public Class getBeanClass() {
            return KnowledgeBase.class;
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return Collections.emptySet();
        }

        public String getName() {
            return null;
        }

        public Set<Annotation> getQualifiers() {
            return qualifiers;
        }

        public Class< ? extends Annotation> getScope() {
            return ApplicationScoped.class;
        }

        public Set<Class< ? extends Annotation>> getStereotypes() {
            return Collections.emptySet();
        }

        public Set<Type> getTypes() {
            return types;
        }

        public boolean isAlternative() {
            return false;
        }

        public boolean isNullable() {
            return false;
        }
    }

    public static class StatelessKSessionBean
        implements
        Bean<StatelessKnowledgeSession> {
        static final Set<Type>  types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( StatelessKnowledgeSession.class,
                                                                                                       Object.class ) ) );

        private Set<Annotation> qualifiers;

        private KieBaseModel    kieBaseModelModel;
        private KieSessionModel kieSessionModelModel;

        private KBaseBean       kBaseBean;

        public StatelessKSessionBean(final KieSessionModel kieSessionModelModel,
                                     KBaseBean kBaseBean) {
            this.kieSessionModelModel = kieSessionModelModel;
            this.kBaseBean = kBaseBean;
            this.kieBaseModelModel = ((KieSessionModelImpl) kieSessionModelModel).getKBase();

            this.qualifiers = Collections.unmodifiableSet( new HashSet<Annotation>( Arrays.asList( new AnnotationLiteral<Default>() {
                                                                                                   },
                                                                                                   new AnnotationLiteral<Any>() {
                                                                                                   },
                                                                                                   new KSession() {
                                                                                                       public Class< ? extends Annotation> annotationType() {
                                                                                                           return KSession.class;
                                                                                                       }

                                                                                                       public String value() {
                                                                                                           return kieSessionModelModel.getName();
                                                                                                       }
                                                                                                   }
                    ) ) );
        }

        public StatelessKnowledgeSession create(CreationalContext ctx) {
            KnowledgeBase kBase = kBaseBean.getKnowledgeBase();
            return kBase.newStatelessKnowledgeSession();
        }

        public void destroy(StatelessKnowledgeSession kSession,
                            CreationalContext ctx) {
            ctx.release();
        }

        public Class getBeanClass() {
            return StatelessKnowledgeSession.class;
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return Collections.emptySet();
        }

        public String getName() {
            return null;
        }

        public Set<Annotation> getQualifiers() {
            return qualifiers;
        }

        public Class< ? extends Annotation> getScope() {
            return ApplicationScoped.class;
        }

        public Set<Class< ? extends Annotation>> getStereotypes() {
            return Collections.emptySet();
        }

        public Set<Type> getTypes() {
            return types;
        }

        public boolean isAlternative() {
            return false;
        }

        public boolean isNullable() {
            return false;
        }
    }

    public static class StatefulKSessionBean
        implements
        Bean<StatefulKnowledgeSession> {
        static final Set<Type>  types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( StatefulKnowledgeSession.class,
                                                                                                       Object.class ) ) );

        private Set<Annotation> qualifiers;

        private KieBaseModel    kieBaseModelModel;
        private KieSessionModel kieSessionModelModel;

        private KBaseBean       kBaseBean;

        public StatefulKSessionBean(final KieSessionModel kieSessionModelModel,
                                    KBaseBean kBaseBean) {
            this.kieSessionModelModel = kieSessionModelModel;
            this.kieBaseModelModel = ((KieSessionModelImpl) kieSessionModelModel).getKBase();
            this.kBaseBean = kBaseBean;

            this.qualifiers = Collections.unmodifiableSet( new HashSet<Annotation>( Arrays.asList( new AnnotationLiteral<Default>() {
                                                                                                   },
                                                                                                   new AnnotationLiteral<Any>() {
                                                                                                   },
                                                                                                   new KSession() {
                                                                                                       public Class< ? extends Annotation> annotationType() {
                                                                                                           return KSession.class;
                                                                                                       }

                                                                                                       public String value() {
                                                                                                           return kieSessionModelModel.getName();
                                                                                                       }
                                                                                                   }
                    ) ) );
        }

        public StatefulKnowledgeSession create(CreationalContext ctx) {
            KnowledgeBase kBase = kBaseBean.getKnowledgeBase();
            return kBase.newStatefulKnowledgeSession();
        }

        public void destroy(StatefulKnowledgeSession kBase,
                            CreationalContext ctx) {
            ctx.release();
        }

        public Class getBeanClass() {
            return StatefulKnowledgeSession.class;
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return Collections.emptySet();
        }

        public String getName() {
            return null;
        }

        public Set<Annotation> getQualifiers() {
            return qualifiers;
        }

        public Class< ? extends Annotation> getScope() {
            return ApplicationScoped.class;
        }

        public Set<Class< ? extends Annotation>> getStereotypes() {
            return Collections.emptySet();
        }

        public Set<Type> getTypes() {
            return types;
        }

        public boolean isAlternative() {
            return false;
        }

        public boolean isNullable() {
            return false;
        }
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

        List<KieProject> kProjects = new ArrayList<KieProject>();
        
        // Map of kproject urls
        Map<KieProject, String> urls = new IdentityHashMap<KieProject, String>();
        while ( e.hasMoreElements() ) {
            URL url = e.nextElement();;
            try {
                KieProject kieProject = KieProjectImpl.fromXML( url );
                kProjects.add( kieProject );
                
                String fixedURL = fixURL( url );
                urls.put( kieProject,
                          fixedURL );
            } catch ( Exception exc ) {
                log.error( "Unable to build and build index of kproject.xml url=" + url.toExternalForm() + "\n" + exc.getMessage() );
            }
        }

        for ( KieProject kieProject : kProjects ) {
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                kBases.put( kieBaseModel.getName(),
                            kieBaseModel );
                ((KieBaseModelImpl) kieBaseModel).setKProject( kieProject ); // should already be set, but just in case
                
                kBaseURLs.put( kieBaseModel.getName(),
                               urls.get( kieProject ) );
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
