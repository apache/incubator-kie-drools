package org.drools.cdi;

import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.KieBase;
import org.kie.KieServices;
import org.kie.builder.ReleaseId;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KieSessionModel.KieSessionType;
import org.kie.builder.impl.InternalKieModule;
import org.kie.builder.impl.KieContainerImpl;
import org.kie.builder.impl.KieProject;
import org.kie.cdi.KBase;
import org.kie.cdi.KReleaseId;
import org.kie.cdi.KSession;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKieSession;
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
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KieCDIExtension
    implements
    Extension {

    private static final Logger                     log           = LoggerFactory.getLogger( KieCDIExtension.class );

    private Map<KieCDIEntry, KieCDIEntry>                        kBaseNames;
    private Map<KieCDIEntry, KieCDIEntry>                        kSessionNames;

    private Map<ReleaseId, KieContainer>                  gavs;

    private Map<String, KieCDIEntry>                named;

    private KieContainerImpl                        classpathKContainer;

    private final static AnnotationLiteral<Default> defaultAnnLit = new AnnotationLiteral<Default>() {
                                                                  };
    private final static AnnotationLiteral<Any>     anyAnnLit     = new AnnotationLiteral<Any>() {
                                                                  };

    public KieCDIExtension() { }

    public void init() {
        KieServices ks = KieServices.Factory.get();
        gavs = new HashMap<ReleaseId, KieContainer>();
        classpathKContainer = (KieContainerImpl) ks.getKieClasspathContainer(); //new KieContainerImpl( kProject, null );
        named = new HashMap<String, KieCDIExtension.KieCDIEntry>();
    }

    public <Object> void processInjectionTarget(@Observes ProcessInjectionTarget<Object> pit,
                                                BeanManager beanManager) {
        if ( classpathKContainer == null ) {
            init();
        }

        KieServices ks = KieServices.Factory.get();

        // Find all uses of KieBaseModel and KieSessionModel and add to Set index
        if ( !pit.getInjectionTarget().getInjectionPoints().isEmpty() ) {
            for ( InjectionPoint ip : pit.getInjectionTarget().getInjectionPoints() ) {
                KBase kBase = ip.getAnnotated().getAnnotation( KBase.class );
                KSession kSession = ip.getAnnotated().getAnnotation( KSession.class );
                if ( kBase == null && kSession == null ) {
                    continue;
                }

                KReleaseId KReleaseId = ip.getAnnotated().getAnnotation( KReleaseId.class );
                ReleaseId releaseId = null;
                if ( KReleaseId != null ) {
                    releaseId = ks.newReleaseId(KReleaseId.groupId(),
                            KReleaseId.artifactId(),
                            KReleaseId.version());
                    gavs.put(releaseId,
                              null );
                }

                Named namedAnn = ip.getAnnotated().getAnnotation( Named.class );
                String namedStr = null;
                if ( namedAnn != null ) {
                    namedStr = namedAnn.value();
                }

                Class< ? extends Annotation> scope = ApplicationScoped.class;

                if ( kBase != null ) {
                    addKBaseInjectionPoint(ip, kBase, namedStr, scope, releaseId);
                } else if ( kSession != null ) {
                    addKSessionInjectionPoint(ip, kSession, namedStr, scope, releaseId);
                }
            }
        }
    }
    
    public void addKBaseInjectionPoint(InjectionPoint ip, KBase kBase, String namedStr, Class< ? extends Annotation> scope, ReleaseId releaseId) {
        if ( kBaseNames == null ) {
            kBaseNames = new HashMap<KieCDIEntry, KieCDIEntry>();
        }

        KieCDIEntry newEntry = new KieCDIEntry( kBase.value(),
                                                scope,
                releaseId,
                                                namedStr );

        KieCDIEntry existingEntry = kBaseNames.remove( newEntry );
        if ( existingEntry != null ) {
            // it already exists, so just update its Set of InjectionPoints
            // Note any duplicate "named" would be handled via this.
            existingEntry.addInjectionPoint( ip );
            kBaseNames.put( existingEntry, existingEntry );
        }
        
        if ( namedStr != null ) {
            existingEntry = named.get( namedStr );   
            if ( existingEntry == null ) {
                // it is named, but nothing existing for it to clash ambigously with
                named.put( namedStr, newEntry );
                kBaseNames.put(newEntry,newEntry);                            
            } else {
                // this name exists, but we know it's a different KieCDIEntry due to the previous existing check
                log.error( "@Named({}) declaration used ambiguiously existing: {} new: {}",
                           new String[] { namedStr,
                                          existingEntry.toString(),
                                          newEntry.toString() });
            }

        } else {
            // is not named and no existing entry
            kBaseNames.put(newEntry,newEntry);
        }        
    }

    public void addKSessionInjectionPoint(InjectionPoint ip, KSession kSession, String namedStr, Class< ? extends Annotation> scope, ReleaseId releaseId) {
        if ( kSessionNames == null ) {
            kSessionNames = new HashMap<KieCDIEntry, KieCDIEntry>();
        }

        KieCDIEntry newEntry = new KieCDIEntry( kSession.value(),
                                                scope,
                releaseId,
                                                namedStr );

        KieCDIEntry existingEntry = kSessionNames.remove( newEntry );
        if ( existingEntry != null ) {
            // it already exists, so just update its Set of InjectionPoints
            // Note any duplicate "named" would be handled via this.
            existingEntry.addInjectionPoint( ip );
            kSessionNames.put( existingEntry, existingEntry );
        }
        
        if ( namedStr != null ) {
            existingEntry = named.get( namedStr );   
            if ( existingEntry == null ) {
                // it is named, but nothing existing for it to clash ambigously with
                named.put( namedStr, newEntry );
                kSessionNames.put(newEntry,newEntry);                            
            } else {
                // this name exists, but we know it's a different KieCDIEntry due to the previous existing check
                log.error( "@Named({}) declaration used ambiguiously existing: {} new: {}",
                           new String[] { namedStr,
                                          existingEntry.toString(),
                                          newEntry.toString() });
            }

        } else {
            // is not named and no existing entry
            kSessionNames.put(newEntry,newEntry);
        }        
    }
    
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd,
                                   BeanManager bm) {
        //abd.addBean( bean )
        
        if ( classpathKContainer != null ) {
            // if classpathKContainer null, processInjectionTarget was not called, so beans to create
            KieServices ks = KieServices.Factory.get();

            // to array, so we don't mutate that which we are iterating over
            if ( !gavs.isEmpty() ) {
                for ( ReleaseId releaseId : gavs.keySet().toArray( new ReleaseId[gavs.size()] ) ) {
                    KieContainer kContainer = ks.newKieContainer(releaseId);
                    if ( kContainer == null ) {
                        log.error( "Unable to retrieve KieContainer for ReleaseId {}",
                                   releaseId.toString() );
                    } else {
                        log.debug( "KieContainer retrieved for ReleaseId {}",
                                   releaseId.toString() );
                    }
                    gavs.put(releaseId,
                              kContainer );
                }
            }

            if ( kBaseNames != null ) {
                for ( KieCDIEntry entry : kBaseNames.keySet() ) {
                    addKBaseBean( abd,
                                  entry );
                }
            }
            kBaseNames = null;

            if ( kSessionNames != null ) {
                for ( KieCDIEntry entry : kSessionNames.keySet() ) {
                    addKSessionBean( abd,
                                     entry );
                }
            }
            kSessionNames = null;
        }
    }

    public void addKBaseBean(AfterBeanDiscovery abd,
                             KieCDIEntry entry) {
        ReleaseId releaseId = entry.getkGAV();
        KieContainerImpl kieContainer = classpathKContainer; // default to classpath, but allow it to be overriden
        if ( releaseId != null ) {
            kieContainer = (KieContainerImpl) gavs.get(releaseId);
            if ( kieContainer == null ) {
                log.error( "Unable to create KBase({}), could not retrieve KieContainer for ReleaseId {}",
                           entry.getKieTypeName(),
                           releaseId.toString() );
                return;
            }
        }
        KieProject kProject = kieContainer.getKieProject();

        String kBaseQName = entry.getKieTypeName();
        KieBaseModel kBaseModel = kProject.getKieBaseModel( kBaseQName );
        if ( kBaseModel == null ) {
            log.error( "Annotation @KBase({}) found, but no KieBaseModel exist.\nEither the required kproject.xml does not exist, was corrupted, or mising the KieBase entry",
                       kBaseQName );
            return;
        }
        if ( !kBaseModel.getScope().trim().equals( entry.getScope().getClass().getName() ) ) {
            try {
                if ( kBaseModel.getScope().indexOf( '.' ) >= 0 ) {
                    entry.setScope( (Class< ? extends Annotation>) Class.forName( kBaseModel.getScope() ) );
                } else {
                    entry.setScope( (Class< ? extends Annotation>) Class.forName( "javax.enterprise.context." + kBaseModel.getScope() ) );
                }
            } catch ( ClassNotFoundException e ) {
                log.error( "KieBaseModule {} overrides default annotation, but it was not able to find it {}\n{}",
                           new String[]{kBaseQName, kBaseModel.getScope(), e.getMessage()} );
            }
        }
        KBaseBean bean = new KBaseBean( kBaseModel,
                                        kieContainer,
                                        entry.getScope(),
                                        entry.getNamed(),
                                        entry.getInjectionPoints() );
        if ( log.isDebugEnabled() ) {
            InternalKieModule kModule = kProject.getKieModuleForKBase( kBaseQName );
            log.debug( "Added Bean for @KBase({})",
                       kBaseQName,
                       kModule );
        }
        abd.addBean( bean );
    }

    public void addKSessionBean(AfterBeanDiscovery abd,
                                KieCDIEntry entry) {
        ReleaseId releaseId = entry.getkGAV();
        KieContainerImpl kieContainer = classpathKContainer; // default to classpath, but allow it to be overriden
        if ( releaseId != null ) {
            kieContainer = (KieContainerImpl) gavs.get(releaseId);
            if ( kieContainer == null ) {
                log.error( "Unable to create KSession({}), could not retrieve KieContainer for ReleaseId {}",
                           entry.getKieTypeName(),
                           releaseId.toString() );
                return;
            }
        }
        KieProject kProject = kieContainer.getKieProject();

        String kSessionName = entry.getKieTypeName();
        KieSessionModel kSessionModel = kProject.getKieSessionModel( kSessionName );
        if ( kSessionModel == null ) {
            log.error( "Annotation @KSession({}) found, but no KieSessioneModel exist.\nEither the required kproject.xml does not exist, was corrupted, or mising the KieBase entry",
                       kSessionName );
            return;
        }

        if ( !kSessionModel.getScope().trim().equals( entry.getScope().getClass().getName() ) ) {
            try {
                if ( kSessionModel.getScope().indexOf( '.' ) >= 0 ) {
                    entry.setScope( (Class< ? extends Annotation>) Class.forName( kSessionModel.getScope() ) );
                } else {
                    entry.setScope( (Class< ? extends Annotation>) Class.forName( "javax.enterprise.context." + kSessionModel.getScope() ) );
                }
            } catch ( ClassNotFoundException e ) {
                log.error( "KieBaseModule {} overrides default annotation, but it was not able to find it {}\n{}",
                           new String[]{kSessionName, kSessionModel.getScope(), e.getMessage()} );
            }
        }

        if ( KieSessionType.STATELESS.equals( kSessionModel.getType() ) ) {
            if ( log.isDebugEnabled() ) {
                InternalKieModule kModule = kProject.getKieModuleForKBase( ((KieSessionModelImpl) kSessionModel).getKieBaseModel().getName() );
                log.debug( "Added Bean for Stateless @KSession({}) from: {}",
                           kSessionName,
                           kModule );
            }
            abd.addBean( new StatelessKSessionBean( kSessionModel,
                                                    kieContainer,
                                                    entry.getScope(),
                                                    entry.getNamed(),
                                                    entry.getInjectionPoints() ) );
        } else {
            InternalKieModule kModule = kProject.getKieModuleForKBase( ((KieSessionModelImpl) kSessionModel).getKieBaseModel().getName() );
            log.debug( "Added Bean for Stateful @KSession({})  from: {}",
                       kSessionName,
                       kModule );
            abd.addBean( new StatefulKSessionBean( kSessionModel,
                                                   kieContainer,
                                                   entry.getScope(),
                                                   entry.getNamed(),
                                                   entry.getInjectionPoints()  ) );
        }
    }

    public static class KBaseBean
        implements
        Bean<KieBase> {
        static final Set<Type>               types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieBase.class,
                                                                                                                    Object.class ) ) );

        private final Set<Annotation>              qualifiers;

        private KieContainer                       kContainer;

        private final KieBaseModel                 kBaseModel;

        private final Class< ? extends Annotation> scope;

        private final String                       named;
        
        private final Set<InjectionPoint>          injectionPoints;

        public KBaseBean(final KieBaseModel kBaseModel,
                         KieContainer kContainer,
                         Class< ? extends Annotation> scope,
                         final String named,
                         Set<InjectionPoint> injectionPoints) {
            this.kBaseModel = kBaseModel;
            this.kContainer = kContainer;
            this.scope = scope;
            this.named = named;
            this.injectionPoints = injectionPoints;

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
            if ( named != null ) {
                set.add( new Named() {
                    public Class< ? extends Annotation> annotationType() {
                        return Named.class;
                    }

                    public String value() {
                        return named;
                    }

                    public String toString() {
                        return "Named[" + named + "]";
                    }
                } );
            }
            if ( kContainer.getReleaseId() != null ) {
                final String groupId = kContainer.getReleaseId().getGroupId();
                final String artifactId = kContainer.getReleaseId().getArtifactId();
                final String version = kContainer.getReleaseId().getVersion();
                set.add( new KReleaseId() {
                    public Class< ? extends Annotation> annotationType() {
                        return KReleaseId.class;
                    }

                    public String groupId() {
                        return groupId;
                    }

                    public String artifactId() {
                        return artifactId;
                    }

                    public String version() {
                        return version;
                    }

                    public String toString() {
                        return "KReleaseId[groupId=" + groupId + " artifactId" + artifactId + " version=" + version + "]";
                    }
                } );
            }

            this.qualifiers = Collections.unmodifiableSet( set );
        }

        public KieBase create(CreationalContext ctx) {
            return kContainer.getKieBase( kBaseModel.getName() );
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
            return injectionPoints;
        }

        public String getName() {
            return named;
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
        static final Set<Type>               types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( StatelessKieSession.class,
                                                                                                                    Object.class ) ) );

        private final Set<Annotation>              qualifiers;

        private final KieSessionModel              kSessionModel;

        private final KieContainer                 kContainer;

        private final Class< ? extends Annotation> scope;

        private final String                       named;
        
        private final Set<InjectionPoint>          injectionPoints;

        public StatelessKSessionBean(final KieSessionModel kieSessionModelModel,
                                     KieContainer kContainer,
                                     Class< ? extends Annotation> scope,
                                     final String named,
                                     Set<InjectionPoint> injectionPoints) {
            this.kSessionModel = kieSessionModelModel;
            this.kContainer = kContainer;
            this.scope = scope;
            this.named = named;
            this.injectionPoints = injectionPoints;

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
            if ( named != null ) {
                set.add( new Named() {
                    public Class< ? extends Annotation> annotationType() {
                        return Named.class;
                    }

                    public String value() {
                        return named;
                    }

                    public String toString() {
                        return "Named[" + named + "]";
                    }
                } );
            }
            if ( kContainer.getReleaseId() != null ) {
                final String groupId = kContainer.getReleaseId().getGroupId();
                final String artifactId = kContainer.getReleaseId().getArtifactId();
                final String version = kContainer.getReleaseId().getVersion();
                set.add( new KReleaseId() {
                    public Class< ? extends Annotation> annotationType() {
                        return KReleaseId.class;
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
                        return "KReleaseId[groupId=" + groupId + " artifactId" + artifactId + " version=" + version + "]";
                    }
                } );
            }
            this.qualifiers = Collections.unmodifiableSet( set );
        }

        public StatelessKieSession create(CreationalContext ctx) {
            return kContainer.newStatelessKieSession(kSessionModel.getName());
        }

        public void destroy(StatelessKieSession kSession,
                            CreationalContext ctx) {
            ctx.release();
        }

        public Class getBeanClass() {
            return StatelessKnowledgeSession.class;
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return injectionPoints;
        }

        public String getName() {
            return named;
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
        static final Set<Type>               types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieSession.class,
                                                                                                                    Object.class ) ) );

        private final Set<Annotation>              qualifiers;

        private final KieSessionModel              kSessionModel;

        private final KieContainer                 kContainer;

        private final Class< ? extends Annotation> scope;

        private final String                       named;
        
        private final Set<InjectionPoint>          injectionPoints;

        public StatefulKSessionBean(final KieSessionModel kieSessionModelModel,
                                    KieContainer kContainer,
                                    Class< ? extends Annotation> scope,
                                    final String named,
                                    Set<InjectionPoint> injectionPoints) {
            this.kSessionModel = kieSessionModelModel;
            this.kContainer = kContainer;
            this.scope = scope;
            this.named = named;
            this.injectionPoints = injectionPoints;

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
            if ( named != null ) {
                set.add( new Named() {
                    public Class< ? extends Annotation> annotationType() {
                        return Named.class;
                    }

                    public String value() {
                        return named;
                    }

                    public String toString() {
                        return "Named[" + named + "]";
                    }
                } );
            }
            if ( kContainer.getReleaseId() != null ) {
                final String groupId = kContainer.getReleaseId().getGroupId();
                final String artifactId = kContainer.getReleaseId().getArtifactId();
                final String version = kContainer.getReleaseId().getVersion();
                set.add( new KReleaseId() {
                    public Class< ? extends Annotation> annotationType() {
                        return KReleaseId.class;
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
                        return "KReleaseId[groupId=" + groupId + " artifactId" + artifactId + " version=" + version + "]";
                    }
                } );
            }

            this.qualifiers = Collections.unmodifiableSet( set );
        }

        public KieSession create(CreationalContext ctx) {
            return kContainer.newKieSession(kSessionModel.getName());
        }

        public void destroy(KieSession kBase,
                            CreationalContext ctx) {
            ctx.release();
        }

        public Class getBeanClass() {
            return StatefulKnowledgeSession.class;
        }

        public Set<InjectionPoint> getInjectionPoints() {
            return injectionPoints;
        }

        public String getName() {
            return named;
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
        private String                       kieTypeName;
        private Class< ? extends Annotation> scope;
        private ReleaseId kReleaseId;
        private String                       named;        
        private Set<InjectionPoint>          injectionPoints;

        public KieCDIEntry(String kieTypeName,
                           Class< ? extends Annotation> scope,
                           ReleaseId releaseId,
                           String named) {
            super();
            this.kieTypeName = kieTypeName;
            this.scope = scope;
            this.kReleaseId = releaseId;
            this.named = named;
            this.injectionPoints = new HashSet<InjectionPoint>();
        }

        public KieCDIEntry(String kieTypeName) {
            super();
            this.kieTypeName = kieTypeName;
        }

        public String getKieTypeName() {
            return kieTypeName;
        }

        public void setKieTypeName(String kieTypeName) {
            this.kieTypeName = kieTypeName;
        }

        public String getNamed() {
            return named;
        }

        public void setNamed(String named) {
            this.named = named;
        }

        public void setScope(Class< ? extends Annotation> scope) {
            this.scope = scope;
        }

        public Class< ? extends Annotation> getScope() {
            return scope;
        }

        public ReleaseId getkGAV() {
            return kReleaseId;
        }

        public void setkGAV(ReleaseId kReleaseId) {
            this.kReleaseId = kReleaseId;
        }         
        
        /**
         * InjectionPoints is not to be included in the equals/hashcode test
         * @return
         */        
        public void addInjectionPoint(InjectionPoint ip) {
            this.injectionPoints.add( ip );
        }

        /**
         * InjectionPoints is not to be included in the equals/hashcode test
         * @return
         */
        public Set<InjectionPoint> getInjectionPoints() {
            return injectionPoints;
        }

        /**
         * InjectionPoints is not to be included in the equals/hashcode test
         * @return
         */        
        public void setInjectionPoints(Set<InjectionPoint> injectionPoints) {
            this.injectionPoints = injectionPoints;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((kReleaseId == null) ? 0 : kReleaseId.hashCode());
            result = prime * result + ((kieTypeName == null) ? 0 : kieTypeName.hashCode());
            result = prime * result + ((named == null) ? 0 : named.hashCode());
            result = prime * result + ((scope == null) ? 0 : scope.hashCode());
            return result;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            KieCDIEntry other = (KieCDIEntry) obj;
            if ( kReleaseId == null ) {
                if ( other.kReleaseId != null ) return false;
            } else if ( !kReleaseId.equals( other.kReleaseId) ) return false;
            if ( kieTypeName == null ) {
                if ( other.kieTypeName != null ) return false;
            } else if ( !kieTypeName.equals( other.kieTypeName ) ) return false;
            if ( named == null ) {
                if ( other.named != null ) return false;
            } else if ( !named.equals( other.named ) ) return false;
            if ( scope == null ) {
                if ( other.scope != null ) return false;
            } else if ( !scope.equals( other.scope ) ) return false;
            return true;
        }

        @Override
        public String toString() {
            return "KieCDIEntry [kieTypeName=" + kieTypeName + ", scope=" + scope + ", kReleaseId=" + kReleaseId + ", named=" + named + "]";
        }

    }
}
