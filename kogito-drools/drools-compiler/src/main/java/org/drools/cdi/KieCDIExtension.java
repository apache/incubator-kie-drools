package org.drools.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import org.drools.kproject.models.KieSessionModelImpl;
import org.jboss.weld.literal.AnyLiteral;
import org.kie.KieBase;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieContainer;
import org.kie.builder.KieFactory;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KieSessionModel.KieSessionType;
import org.kie.builder.impl.ClasspathKieProject;
import org.kie.builder.impl.InternalKieModule;
import org.kie.builder.impl.KieContainerImpl;
import org.kie.builder.impl.KieProject;
import org.kie.cdi.KBase;
import org.kie.cdi.KGAV;
import org.kie.cdi.KSession;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKieSession;
import org.kie.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieCDIExtension
    implements
    Extension {

    private static final Logger log = LoggerFactory.getLogger( KieCDIExtension.class );

    private Set<KieCDIEntry>       kBaseNames;
    private Set<KieCDIEntry>       kSessionNames;

    private Map<GAV, KieContainer> gavs;
    
    KieContainerImpl               classpathKContainer;
    
    private final static AnnotationLiteral<Default> defaultAnnLit = new AnnotationLiteral<Default>() {};
    private final static AnnotationLiteral<Any> anyAnnLit = new AnnotationLiteral<Any>() {};

    public KieCDIExtension() {
        
    }

    public void init() {
        gavs = new HashMap<GAV, KieContainer>();
        KieServices ks = KieServices.Factory.get();        
        classpathKContainer = ( KieContainerImpl ) ks.getKieClasspathContainer(); //new KieContainerImpl( kProject, null );
    }

    public <Object> void processInjectionTarget(@Observes ProcessInjectionTarget<Object> pit, BeanManager beanManager) {
        if ( classpathKContainer == null ) {
            init();
        }
        
        KieFactory kf = KieFactory.Factory.get();

        // Find all uses of KieBaseModel and KieSessionModel and add to Set index
        if ( !pit.getInjectionTarget().getInjectionPoints().isEmpty() ) {
            for ( InjectionPoint ip : pit.getInjectionTarget().getInjectionPoints() ) {
                KGAV kGAV = ip.getAnnotated().getAnnotation( KGAV.class );
                GAV gav = null;
                if ( kGAV != null ) {
                    gav = kf.newGav( kGAV.groupId(), kGAV.artifactId(), kGAV.version() );
                    gavs.put( gav, null );
                }
                
                KBase kBase = ip.getAnnotated().getAnnotation( KBase.class );
                if ( kBase != null ) {                    
                    Class< ? extends Annotation> scope = ApplicationScoped.class;                    
                    if ( kBaseNames == null ) {
                        kBaseNames = new HashSet<KieCDIEntry>();
                    }
                    kBaseNames.add( new KieCDIEntry(kBase.value(), scope, gav) );
                    continue;
                }

                KSession kSession = ip.getAnnotated().getAnnotation( KSession.class );
                if ( kSession != null ) {
                    Class< ? extends Annotation> scope = ApplicationScoped.class;
                    
                    if ( kSessionNames == null ) {
                        kSessionNames = new HashSet<KieCDIEntry>();
                    }
                    kSessionNames.add( new KieCDIEntry(kSession.value(), scope, gav) );
                    continue;
                }                
            }
        }
    }    

    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd,
                                   BeanManager bm) {
        if ( classpathKContainer != null ) {
            // if classpathKContainer null, processInjectionTarget was not called, so beans to create
            
            KieServices ks = KieServices.Factory.get();            
            
            // to array, so we don't mutate that which we are iterating over
            if ( !gavs.isEmpty() ) {
                for ( GAV gav : gavs.keySet().toArray( new GAV[gavs.size()] ) ) {
                    KieContainer kContainer = ks.getKieContainer( gav );
                    if ( kContainer == null ) {
                        log.error( "Unable to retrieve KieContainer for GAV {}", gav.toString() );
                    } else {
                        log.debug( "KieContainer retrieved for GAV {}",  gav.toString() );
                    }
                    gavs.put( gav, kContainer );
                }
            }
            
            if ( kBaseNames != null ) {
                for ( KieCDIEntry entry : kBaseNames ) {
                    GAV gav = entry.getkGAV();
                    KieContainerImpl kieContainer = classpathKContainer; // default to classpath, but allow it to be overriden
                    if ( gav != null ) {
                        kieContainer = ( KieContainerImpl ) gavs.get( gav );
                        if ( kieContainer == null ) {
                            log.error( "Unable to create KBase({}), could not retrieve KieContainer for GAV {}", entry.getName(), gav.toString() );
                            continue;
                        } 
                    }
                    KieProject kProject = kieContainer.getKieProject();
                    
                    String kBaseQName = entry.getName();
                    KieBaseModel kBaseModel = kProject.getKieBaseModel( kBaseQName );
                    if ( kBaseModel == null ) {
                        log.error( "Annotation @KBase({}) found, but no KieBaseModel exist.\nEither the required kproject.xml does not exist, was corrupted, or mising the KieBase entry",
                                   kBaseQName );
                        continue;
                    }
                    if ( !kBaseModel.getScope().trim().equals( entry.getScope().getClass().getName()  ) ) {
                        try {
                            if (kBaseModel.getScope().indexOf( '.' ) >= 0 ) {
                                entry.setScope( (Class< ? extends Annotation>) Class.forName( kBaseModel.getScope() ) );
                            } else {
                                entry.setScope( (Class< ? extends Annotation>) Class.forName( "javax.enterprise.context." + kBaseModel.getScope() ) );                                
                            }
                        } catch ( ClassNotFoundException e ) {
                            log.error( "KieBaseModule {} overrides default annotation, but it was not able to find it {}\n{}", new String[] { kBaseQName, kBaseModel.getScope(), e.getMessage() } );
                        }
                    }
                    KBaseBean bean = new KBaseBean( kBaseModel,
                                                    kieContainer,
                                                    entry.getScope() );
                    if ( log.isDebugEnabled() ) {
                        InternalKieModule kModule = (InternalKieModule) kProject.getKieModuleForKBase( kBaseQName );
                        log.debug( "Added Bean for @KBase({})",
                                   kBaseQName,
                                   kModule );
                    }
                    abd.addBean( bean );
                }
            }
            kBaseNames = null;
            
            if ( kSessionNames != null ) {
                for ( KieCDIEntry entry : kSessionNames ) {
                    GAV gav = entry.getkGAV();
                    KieContainerImpl kieContainer = classpathKContainer; // default to classpath, but allow it to be overriden
                    if ( gav != null ) {
                        kieContainer = ( KieContainerImpl ) gavs.get( gav );
                        if ( kieContainer == null ) {
                            log.error( "Unable to create KSession({}), could not retrieve KieContainer for GAV {}", entry.getName(), gav.toString() );
                            continue;
                        } 
                    }
                    KieProject kProject = kieContainer.getKieProject();
                    
                    String kSessionName = entry.getName();
                    KieSessionModel kSessionModel = kProject.getKieSessionModel( kSessionName );
                    if ( kSessionModel == null ) {
                        log.error( "Annotation @KSession({}) found, but no KieSessioneModel exist.\nEither the required kproject.xml does not exist, was corrupted, or mising the KieBase entry",
                                   kSessionName );
                        continue;
                    }

                    if ( !kSessionModel.getScope().trim().equals( entry.getScope().getClass().getName()  ) ) {
                        try {
                            if (kSessionModel.getScope().indexOf( '.' ) >= 0 ) {
                                entry.setScope( (Class< ? extends Annotation>) Class.forName( kSessionModel.getScope() ) );
                            } else {
                                entry.setScope( (Class< ? extends Annotation>) Class.forName( "javax.enterprise.context." + kSessionModel.getScope() ) );                                
                            }
                        } catch ( ClassNotFoundException e ) {
                            log.error( "KieBaseModule {} overrides default annotation, but it was not able to find it {}\n{}", new String[] { kSessionName, kSessionModel.getScope(), e.getMessage() } );
                        }
                    }
                    
                    if ( KieSessionType.STATELESS.equals( kSessionModel.getType() ) ) {
                        if ( log.isDebugEnabled() ) {
                            InternalKieModule kModule = (InternalKieModule) kProject.getKieModuleForKBase( ((KieSessionModelImpl) kSessionModel).getKieBaseModel().getName() );
                            log.debug( "Added Bean for Stateless @KSession({}) from: {}",
                                       kSessionName,
                                       kModule );
                        }
                        abd.addBean( new StatelessKSessionBean( kSessionModel,
                                                                kieContainer,
                                                                entry.getScope() ) );
                    } else {
                        InternalKieModule kModule = (InternalKieModule) kProject.getKieModuleForKBase( ((KieSessionModelImpl) kSessionModel).getKieBaseModel().getName() );
                        log.debug( "Added Bean for Stateful @KSession({})  from: {}",
                                   kSessionName,
                                   kModule );
                        abd.addBean( new StatefulKSessionBean( kSessionModel,
                                                               kieContainer,
                                                               entry.getScope() ) );
                    }
                }
            }
            kSessionNames = null;
        }
    }
    
    public static class KBaseBean
        implements
        Bean<KieBase> {
        static final Set<Type>              types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieBase.class,
                                                                                                                   Object.class ) ) );

        private Set<Annotation>              qualifiers;

        private KieContainer                 kContainer;

        private KieBaseModel                 kBaseModel;

        private Class< ? extends Annotation> scope;

        public KBaseBean(final KieBaseModel kBaseModel,
                         KieContainer kContainer,
                         Class< ? extends Annotation> scope) {
            this.kBaseModel = kBaseModel;
            this.kContainer = kContainer;
            this.scope = scope;
            
            Set<Annotation> set = new HashSet<Annotation>();
            set.add( defaultAnnLit );
            set.add( anyAnnLit );
            set.add( new KBase() {
                public Class< ? extends Annotation> annotationType() {
                    return KBase.class;
                }

                public String value() {
                    return kBaseModel.getName();
                }
            } );
            if ( kContainer.getGAV() != null ) {
                final String groupId = kContainer.getGAV().getGroupId();
                final String artifactId = kContainer.getGAV().getArtifactId();
                final String version = kContainer.getGAV().getVersion();
                set.add( new KGAV() {
                    public Class< ? extends Annotation> annotationType() {
                        return KGAV.class;
                    }

                    @Override
                    public String groupId() {
                        return groupId;
                    }

                    @Override
                    public String artifactId() {
                        return artifactId;
                    }

                    @Override
                    public String version() {
                        return version;
                    }
                    
                    public String toString() {
                        return "KGAV[groupId="+groupId + " artifactId" + artifactId + " version=" + version + "]";
                    }
                } );                
            }
            
            this.qualifiers = Collections.unmodifiableSet( set );
        }

        public KieBase create(CreationalContext ctx) {
            KieBase kieBase = kContainer.getKieBase( kBaseModel.getName() );
            return kieBase;
        }

        public void destroy(KieBase kBase,
                            CreationalContext ctx) {
            this.kContainer = null;
            ctx.release();
        }

        public Class getBeanClass() {
            return KieBase.class;
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return Collections.emptySet();
        }

        public String getName() {
           // return kBaseModel.getName();
            return null;
        }

        public Set<Annotation> getQualifiers() {
            return qualifiers;
        }

        public Class< ? extends Annotation> getScope() {
            return this.scope;
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

        @Override
        public String toString() {
            return "KBaseBean [kBase=" + kBaseModel.getName() + ", qualifiers=" + qualifiers + "]";
        }
        
        
    }

    public static class StatelessKSessionBean
        implements
        Bean<StatelessKieSession> {
        static final Set<Type>  types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( StatelessKieSession.class,
                                                                                                       Object.class ) ) );

        private Set<Annotation> qualifiers;

        private KieSessionModel kSessionModel;

        private KieContainer    kContainer;
        
        private Class< ? extends Annotation> scope;

        public StatelessKSessionBean(final KieSessionModel kieSessionModelModel,
                                     KieContainer kContainer,
                                     Class< ? extends Annotation> scope) {
            this.kSessionModel = kieSessionModelModel;
            this.kContainer = kContainer;
            this.scope = scope;

            Set<Annotation> set = new HashSet<Annotation>();
            set.add( defaultAnnLit );
            set.add( anyAnnLit );
            set.add( new KSession() {
                public Class< ? extends Annotation> annotationType() {
                    return KSession.class;
                }

                public String value() {
                    return kSessionModel.getName();
                }
            } );
            if ( kContainer.getGAV() != null ) {
                final String groupId = kContainer.getGAV().getGroupId();
                final String artifactId = kContainer.getGAV().getArtifactId();
                final String version = kContainer.getGAV().getVersion();
                set.add( new KGAV() {
                    public Class< ? extends Annotation> annotationType() {
                        return KGAV.class;
                    }

                    @Override
                    public String groupId() {
                        return groupId;
                    }

                    @Override
                    public String artifactId() {
                        return artifactId;
                    }

                    @Override
                    public String version() {
                        return version;
                    }
                    
                    public String toString() {
                        return "KGAV[groupId="+groupId + " artifactId" + artifactId + " version=" + version + "]";
                    }
                } );                
            }
            this.qualifiers = Collections.unmodifiableSet( set );
        }

        public StatelessKieSession create(CreationalContext ctx) {
            return kContainer.getKieStatelessSession( kSessionModel.getName() );
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
            //return kSessionModel.getName();
        }

        public Set<Annotation> getQualifiers() {
            return qualifiers;
        }

        public Class< ? extends Annotation> getScope() {
            return scope;
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
        
        private Class< ? extends Annotation> scope;

        public StatefulKSessionBean(final KieSessionModel kieSessionModelModel,
                                    KieContainer kContainer,
                                    Class< ? extends Annotation> scope) {
            this.kSessionModel = kieSessionModelModel;
            this.kContainer = kContainer;
            this.scope = scope;

            Set<Annotation> set = new HashSet<Annotation>();
            set.add( defaultAnnLit );
            set.add( anyAnnLit );
            set.add( new KSession() {
                public Class< ? extends Annotation> annotationType() {
                    return KSession.class;
                }

                public String value() {
                    return kSessionModel.getName();
                }
            } );
            if ( kContainer.getGAV() != null ) {
                final String groupId = kContainer.getGAV().getGroupId();
                final String artifactId = kContainer.getGAV().getArtifactId();
                final String version = kContainer.getGAV().getVersion();
                set.add( new KGAV() {
                    public Class< ? extends Annotation> annotationType() {
                        return KGAV.class;
                    }

                    @Override
                    public String groupId() {
                        return groupId;
                    }

                    @Override
                    public String artifactId() {
                        return artifactId;
                    }

                    @Override
                    public String version() {
                        return version;
                    }
                    
                    public String toString() {
                        return "KGAV[groupId="+groupId + " artifactId" + artifactId + " version=" + version + "]";
                    }
                } );                
            }
            
            this.qualifiers = Collections.unmodifiableSet( set );
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
            return this.scope;
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
    
    public static class KieCDIEntry {
        private String name;
        private Class< ? extends Annotation>  scope;
        private GAV gav;
        
        public KieCDIEntry(String name,
                           Class< ? extends Annotation>  scope, GAV gav) {
            super();
            this.name = name;
            this.scope = scope;
            this.gav = gav;
        }

        public KieCDIEntry(String name) {
            super();
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        public void setScope(Class< ? extends Annotation> scope) {
            this.scope = scope;
        }

        public Class< ? extends Annotation>  getScope() {
            return scope;
        }        
        
        public GAV getkGAV() {
            return gav;
        }

        public void setkGAV(GAV gav) {
            this.gav = gav;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((gav == null) ? 0 : gav.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((scope == null) ? 0 : scope.hashCode());
            return result;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            KieCDIEntry other = (KieCDIEntry) obj;
            if ( gav == null ) {
                if ( other.gav != null ) return false;
            } else if ( !gav.equals( other.gav ) ) return false;
            if ( name == null ) {
                if ( other.name != null ) return false;
            } else if ( !name.equals( other.name ) ) return false;
            if ( scope == null ) {
                if ( other.scope != null ) return false;
            } else if ( !scope.equals( other.scope ) ) return false;
            return true;
        }

        @Override
        public String toString() {
            return "KieCDIEntry [name=" + name + ", scope=" + scope + ", gav=" + gav + "]";
        }   
        
    }    
}
