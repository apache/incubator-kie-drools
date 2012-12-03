package org.drools.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

import org.drools.kproject.KieSessionModelImpl;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieContainer;
import org.kie.builder.KieSessionModel;
import org.kie.builder.impl.ClasspathKieProject;
import org.kie.builder.impl.InternalKieModule;
import org.kie.builder.impl.KieContainerImpl;
import org.kie.cdi.KBase;
import org.kie.cdi.KSession;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKieSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KProjectExtension
    implements
    Extension {

    private static final Logger log = LoggerFactory.getLogger( KProjectExtension.class );

    private Set<String>         kBaseNames;
    private Set<String>         kSessionNames;

    ClasspathKieProject         kProject;

    public KProjectExtension() {
    }

    public void init() {
        kProject = new ClasspathKieProject();
    }

    <Object> void processInjectionTarget(@Observes ProcessInjectionTarget<Object> pit) {
        if ( kProject == null ) {
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
        if ( kProject != null ) {
            // if kProjects null, processInjectionTarget was not called, so beans to create
            
            KieContainerImpl kContainer = new KieContainerImpl( kProject, null );
            
            if ( kBaseNames != null ) {
                for ( String kBaseQName : kBaseNames ) {
                    KieBaseModel kBaseModel = kProject.getKieBaseModel( kBaseQName );
                    if ( kBaseModel == null ) {
                        log.error( "Annotation @KBase({}) found, but no KieBaseModel exist.\nEither the required kproject.xml does not exist, was corrupted, or mising the KieBase entry",
                                   kBaseQName );
                        continue;
                    }
                    KBaseBean bean = new KBaseBean( kBaseModel,
                                                    kContainer );
                    if ( log.isDebugEnabled() ) {
                        InternalKieModule kModule = (InternalKieModule) kProject.getKieModuleForKBase( kBaseQName );
                        log.debug( "Added Bean for @KBase({})",
                                   kBaseQName,
                                   kModule.getFile() );
                    }
                    abd.addBean( bean );
                }
            }
            kBaseNames = null;
            
            if ( kSessionNames != null ) {
                for ( String kSessionName : kSessionNames ) {

                    KieSessionModel kSessionModel = kProject.getKieSessionModel( kSessionName );
                    if ( kSessionModel == null ) {
                        log.error( "Annotation @KSession({}) found, but no KieSessioneModel exist.\nEither the required kproject.xml does not exist, was corrupted, or mising the KieBase entry",
                                   kSessionName );
                        continue;
                    }

                    if ( "stateless".equals( kSessionModel.getType() ) ) {
                        if ( log.isDebugEnabled() ) {
                            InternalKieModule kModule = (InternalKieModule) kProject.getKieModuleForKBase( ((KieSessionModelImpl) kSessionModel).getKieBaseModel().getName() );
                            log.debug( "Added Bean for Stateless @Session({}) from: {}",
                                       kSessionName,
                                       kModule.getFile() );
                        }
                        abd.addBean( new StatelessKSessionBean( kSessionModel,
                                                                kContainer ) );
                    } else {
                        InternalKieModule kModule = (InternalKieModule) kProject.getKieModuleForKBase( ((KieSessionModelImpl) kSessionModel).getKieBaseModel().getName() );
                        log.debug( "Added Bean for Stateful @Session({})  from: {}",
                                   kSessionName,
                                   kModule.getFile() );
                        abd.addBean( new StatefulKSessionBean( kSessionModel,
                                                               kContainer ) );
                    }
                }
            }
            kSessionNames = null;
        }
    }

    public static class KBaseBean
        implements
        Bean<KieBase> {
        static final Set<Type>        types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieBase.class,
                                                                                                             Object.class ) ) );

        private Set<Annotation>       qualifiers;

        private KieContainer          kieContainer;

        private KieBaseModel          kBaseModel;

        public KBaseBean(final KieBaseModel kBaseModel,
                         KieContainer kieContainer) {
            this.kBaseModel = kBaseModel;
            this.kieContainer = kieContainer;

            this.qualifiers = Collections.unmodifiableSet( new HashSet<Annotation>( Arrays.asList( new AnnotationLiteral<Default>() {
                                                                                                   },
                                                                                                   new AnnotationLiteral<Any>() {
                                                                                                   },
                                                                                                   new KBase() {
                                                                                                       public Class< ? extends Annotation> annotationType() {
                                                                                                           return KBase.class;
                                                                                                       }

                                                                                                       public String value() {
                                                                                                           return kBaseModel.getName();
                                                                                                       }
                                                                                                   }
                    ) ) );
        }

        public KieBase create(CreationalContext ctx) {
            KieBase kieBase = kieContainer.getKieBase( kBaseModel.getName() );
            return kieBase;
        }

        public void destroy(KieBase kBase,
                            CreationalContext ctx) {
            this.kieContainer = null;
            ctx.release();
        }

        public Class getBeanClass() {
            return KieBase.class;
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
        Bean<StatelessKieSession> {
        static final Set<Type>  types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( StatelessKieSession.class,
                                                                                                       Object.class ) ) );

        private Set<Annotation> qualifiers;

        private KieSessionModel kSessionModel;

        private KieContainer    kieContainer;

        public StatelessKSessionBean(final KieSessionModel kieSessionModelModel,
                                     KieContainer kieContainer) {
            this.kSessionModel = kieSessionModelModel;
            this.kieContainer = kieContainer;

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

        public StatelessKieSession create(CreationalContext ctx) {
            return kieContainer.getKieStatelessSession( kSessionModel.getName() );
        }

        public void destroy(StatelessKieSession kSession,
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
        Bean<KieSession> {
        static final Set<Type>        types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieSession.class,
                                                                                                             Object.class ) ) );

        private Set<Annotation>       qualifiers;

        private KieSessionModel       kSessionModel;

        private KieContainer          kContainer;

        public StatefulKSessionBean(final KieSessionModel kieSessionModelModel,
                                    KieContainer kContainer) {
            this.kSessionModel = kieSessionModelModel;
            this.kContainer = kContainer;

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

        public KieSession create(CreationalContext ctx) {
            return kContainer.getKieSession( kSessionModel.getName() );
        }

        public void destroy(KieSession kBase,
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
}
