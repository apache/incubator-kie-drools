/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.cdi;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.drools.compiler.kproject.models.KieSessionModelImpl;
import org.drools.core.util.StringUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.KieSessionModel.KieSessionType;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;
import javax.inject.Scope;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class KieCDIExtension
    implements
    Extension {

    private static final Logger                     log           = LoggerFactory.getLogger( KieCDIExtension.class );

    private Map<KieCDIEntry, KieCDIEntry>           kContainerNames;
    private Map<KieCDIEntry, KieCDIEntry>           kBaseNames;
    private Map<KieCDIEntry, KieCDIEntry>           kSessionNames;

    private Map<ReleaseId, KieContainer>            gavs;

    private Map<String, KieCDIEntry>                named;

    private KieContainerImpl                        classpathKContainer;

    private final static AnnotationLiteral<Default> defaultAnnLit = new AnnotationLiteral<Default>() {
                                                                  };
    private final static AnnotationLiteral<Any>     anyAnnLit     = new AnnotationLiteral<Any>() {
                                                                  };
    public KieCDIExtension() { }

    public void init() {
        final KieServices ks = KieServices.Factory.get();
        gavs = new HashMap<ReleaseId, KieContainer>();
        classpathKContainer = (KieContainerImpl) ks.newKieClasspathContainer();
        named = new HashMap<String, KieCDIExtension.KieCDIEntry>();
    }

    public <T> void processInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
        if ( classpathKContainer == null ) {
            init();
        }

        KieServices ks = KieServices.Factory.get();

        // Find all uses of KieBaseModel and KieSessionModel and add to Set index
        if ( !pit.getInjectionTarget().getInjectionPoints().isEmpty() ) {
            for ( InjectionPoint ip : pit.getInjectionTarget().getInjectionPoints() ) {                
                boolean kBaseExists = false;
                boolean kSessionExists = false;
                boolean kContainerExists = false;
                Class clazz = getClassType(ip);

                if (clazz != null) {
                    KBase kBase = null;
                    KSession kSession = null;
                    if ( ( KieSession.class.isAssignableFrom(  clazz ) || StatelessKieSession.class.isAssignableFrom( clazz ) )  ) {
                        kSession = ip.getAnnotated().getAnnotation( KSession.class );
                        kSessionExists = true;
                    } else if (  KieBase.class.isAssignableFrom( clazz ) ) {
                        kBaseExists = true;
                        kBase = ip.getAnnotated().getAnnotation( KBase.class );
                    } else if (  KieContainer.class.isAssignableFrom( clazz ) ) {
                        kContainerExists = true;
                    }

                    if ( !kSessionExists && !kBaseExists && !kContainerExists) {
                        continue;
                    }



                    KReleaseId kReleaseId = ip.getAnnotated().getAnnotation( KReleaseId.class );
                    ReleaseId releaseId = null;
                    if ( kReleaseId != null ) {
                        releaseId = ks.newReleaseId(kReleaseId.groupId(),
                                                    kReleaseId.artifactId(),
                                                    kReleaseId.version());
                        gavs.put(releaseId,
                                  null );
                    }

                    Class<? extends Annotation> scope = getScope(pit);

                    if ( kBaseExists ) {
                        addKBaseInjectionPoint(ip, kBase, scope, releaseId, kReleaseId);
                    } else if ( kSessionExists ) {
                        addKSessionInjectionPoint(ip, kSession, scope, releaseId, kReleaseId);
                    } else if ( kContainerExists ) {
                        addKContainerInjectionPoint(ip, null, scope, releaseId, kReleaseId);
                    }
                }
            }
        }
    }

    private <T> Class<? extends Annotation> getScope(ProcessInjectionTarget<T> pit) {
        Set<Annotation> annotations = pit.getAnnotatedType().getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> scope = annotation.annotationType();
            if (scope.getAnnotation( NormalScope.class ) != null || scope.getAnnotation( Scope.class ) != null) {
                return scope;
            }
        }
        return ApplicationScoped.class;
    }

    public void addKBaseInjectionPoint(InjectionPoint ip, KBase kBase, Class< ? extends Annotation> scope, ReleaseId releaseId, KReleaseId kReleaseId) {
        if ( kBaseNames == null ) {
            kBaseNames = new HashMap<>();
        }
        
        String namedStr = ( kBase == null ) ? null : kBase.name();

        KieCDIEntry newEntry = new KieCDIEntry( (kBase == null) ? null : kBase.value(),
                                                KieBase.class,
                                                scope,
                                                releaseId,
                                                kReleaseId,
                                                namedStr );

        KieCDIEntry existingEntry = kBaseNames.remove( newEntry );
        if ( existingEntry != null ) {
            // it already exists, so just update its Set of InjectionPoints
            // Note any duplicate "named" would be handled via this.
            existingEntry.addInjectionPoint( ip );
            kBaseNames.put( existingEntry, existingEntry );
        }
        
        if ( !StringUtils.isEmpty( namedStr ) ) {
            existingEntry = named.get( namedStr );   
            if ( existingEntry == null ) {
                // it is named, but nothing existing for it to clash ambigously with
                named.put( namedStr, newEntry );
                kBaseNames.put(newEntry,newEntry);                            
            } else {
                // this name exists, but we know it's a different KieCDIEntry due to the previous existing check
                log.error( "name={} declaration used ambiguiously existing: {} new: {}",
                           new String[] { namedStr,
                                          existingEntry.toString(),
                                          newEntry.toString() });
            }

        } else {
            // is not named and no existing entry
            kBaseNames.put(newEntry,newEntry);
        }        
    }

    public void addKSessionInjectionPoint(InjectionPoint ip, KSession kSession, Class< ? extends Annotation> scope, ReleaseId releaseId, KReleaseId kReleaseId) {
        if ( kSessionNames == null ) {
            kSessionNames = new HashMap<KieCDIEntry, KieCDIEntry>();
        }
        
        String namedStr = ( kSession == null ) ? null : kSession.name();
        
        KieCDIEntry newEntry = new KieCDIEntry( (kSession == null) ? null : kSession.value(),
                                                getClassType(ip),
                                                scope,
                                                releaseId,
                                                kReleaseId,
                                                namedStr );

        KieCDIEntry existingEntry = kSessionNames.remove( newEntry );
        if ( existingEntry != null ) {
            // it already exists, so just update its Set of InjectionPoints
            // Note any duplicate "named" would be handled via this.
            existingEntry.addInjectionPoint( ip );
            kSessionNames.put( existingEntry, existingEntry );
        }
        
        if ( !StringUtils.isEmpty( namedStr ) ) {
            existingEntry = named.get( namedStr );   
            if ( existingEntry == null ) {
                // it is named, but nothing existing for it to clash ambiguously with
                named.put( namedStr, newEntry );
                kSessionNames.put(newEntry,newEntry);                            
            } else {
                // this name exists, but we know it's a different KieCDIEntry due to the previous existing check
                log.error( "name={} declaration used ambiguiously existing: {} new: {}",
                           new String[] { namedStr,
                                          existingEntry.toString(),
                                          newEntry.toString() });
            }

        } else {
            // is not named and no existing entry
            kSessionNames.put(newEntry,newEntry);
        }        
    }
    
    public void addKContainerInjectionPoint(InjectionPoint ip, String namedStr, Class< ? extends Annotation> scope, ReleaseId releaseId, KReleaseId kReleaseId) {
        if ( kContainerNames == null ) {
            kContainerNames = new HashMap<KieCDIEntry, KieCDIEntry>();
        }
        
        KieCDIEntry newEntry = new KieCDIEntry( null,
                                                KieContainer.class,
                                                scope,
                                                releaseId,
                                                kReleaseId,
                                                namedStr );

        KieCDIEntry existingEntry = kContainerNames.remove( newEntry );
        if ( existingEntry != null ) {
            // it already exists, so just update its Set of InjectionPoints
            // Note any duplicate "named" would be handled via this.
            existingEntry.addInjectionPoint( ip );
            kContainerNames.put( existingEntry, existingEntry );
        }
        
        if ( !StringUtils.isEmpty( namedStr ) ) {
            existingEntry = named.get( namedStr );   
            if ( existingEntry == null ) {
                // it is named, but nothing existing for it to clash ambigously with
                named.put( namedStr, newEntry );
                kContainerNames.put(newEntry,newEntry);                            
            } else {
                // this name exists, but we know it's a different KieCDIEntry due to the previous existing check
                log.error( "name={} declaration used ambiguiously existing: {} new: {}",
                           new String[] { namedStr,
                                          existingEntry.toString(),
                                          newEntry.toString() });
            }

        } else {
            // is not named and no existing entry
            kContainerNames.put(newEntry,newEntry);
        }        
    }    
        
    
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd) {
        if ( classpathKContainer != null ) {
            // if classpathKContainer null, processInjectionTarget was not called, so beans to create
            KieServices ks = KieServices.Factory.get();

            // to array, so we don't mutate that which we are iterating over
            if ( !gavs.isEmpty() ) {
                for ( ReleaseId releaseId : gavs.keySet().toArray( new ReleaseId[gavs.size()] ) ) {
                    KieContainer kContainer = ks.newKieContainer(releaseId);
                    if ( kContainer == null ) {
                        log.error( "Unable to retrieve KieContainer for @ReleaseId({})",
                                   releaseId.toString() );
                    } else {
                        log.debug( "KieContainer retrieved for @ReleaseId({})",
                                   releaseId.toString() );
                    }
                    gavs.put(releaseId,
                             kContainer );
                }
            }
            
            if ( kContainerNames != null ) {
                for ( KieCDIEntry entry : kContainerNames.keySet() ) {
                    addKContainerBean( abd,
                                       entry );
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

    public void addKContainerBean(AfterBeanDiscovery abd,
                                  KieCDIEntry entry) {        
        ReleaseId releaseId = entry.getReleaseId();
        KieContainerImpl kieContainer = classpathKContainer; // default to classpath, but allow it to be overriden
        if ( releaseId != null ) {
            kieContainer = (KieContainerImpl) gavs.get(releaseId);
            if ( kieContainer == null ) {
                log.error( "Could not retrieve KieContainer for ReleaseId {}",
                           entry.getValue(),
                           releaseId.toString() );
                return;
            }
        }

        KContainerBean bean = new KContainerBean(kieContainer, 
                                                 entry.getKReleaseId(),
                                                 entry.getName(), 
                                                 entry.getInjectionPoints() );
        
        if ( log.isDebugEnabled() ) {
            log.debug( "Added Bean for @KContainer({})",
                       releaseId );
        }
        abd.addBean( bean );
    }    
    
    
    public void addKBaseBean(AfterBeanDiscovery abd,
                             KieCDIEntry entry) {
        ReleaseId releaseId = entry.getReleaseId();
        KieContainerImpl kieContainer = classpathKContainer; // default to classpath, but allow it to be overridden
        if ( releaseId != null ) {
            kieContainer = (KieContainerImpl) gavs.get(releaseId);
            if ( kieContainer == null ) {
                log.error( "Unable to create @KBase({}), could not retrieve KieContainer for ReleaseId {}",
                           entry.getValue(),
                           releaseId.toString() );
                return;
            }
        }
        KieProject kProject = kieContainer.getKieProject();

        KieBaseModel kBaseModel = null;
        String kBaseQName = entry.getValue();
        if ( StringUtils.isEmpty( kBaseQName  )) {
            kBaseModel = kProject.getDefaultKieBaseModel();
        } else {
            kBaseModel = kProject.getKieBaseModel( kBaseQName );   
        }        
        if ( kBaseModel == null ) {
            log.error( "Annotation @KBase({}) found, but no KieBaseModel exists.\nEither the required kmodule.xml does not exist, is corrupted, or is missing the KieBase entry",
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
        KBaseBean bean = new KBaseBean( kBaseQName,
                                        kBaseModel,
                                        kieContainer,
                                        entry.getKReleaseId(),
                                        entry.getScope(),
                                        entry.getName(),
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
        ReleaseId releaseId = entry.getReleaseId();
        KieContainerImpl kieContainer = classpathKContainer; // default to classpath, but allow it to be overriden
        if ( releaseId != null ) {
            kieContainer = (KieContainerImpl) gavs.get(releaseId);
            if ( kieContainer == null ) {
                log.error( "Unable to create KSession({}), could not retrieve KieContainer for ReleaseId {}",
                           entry.getValue(),
                           releaseId.toString() );
                return;
            }
        }
        KieProject kProject = kieContainer.getKieProject();

        String kSessionName = entry.getValue();
        KieSessionModel kSessionModel = null;
        if ( StringUtils.isEmpty( kSessionName  )) {
            kSessionModel = ( entry.getType() == KieSession.class ) ? kProject.getDefaultKieSession() : kProject.getDefaultStatelessKieSession();
        } else {
            kSessionModel =  kProject.getKieSessionModel(kSessionName);
        }         
        if ( kSessionModel == null ) {
            log.error( "Annotation @KSession({}) found, but no KieSessionModel exists.\nEither the required kmodule.xml does not exist, is corrupted, or is missing the KieBase entry",
                       kSessionName );
            return;
        }

        if ( kSessionModel.getScope() != null && !kSessionModel.getScope().trim().equals( entry.getScope().getClass().getName() ) ) {
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
                                                    entry.getKReleaseId(),
                                                    entry.getScope(),
                                                    entry.getName(),
                                                    entry.getInjectionPoints() ) );
        } else {
            InternalKieModule kModule = kProject.getKieModuleForKBase( ((KieSessionModelImpl) kSessionModel).getKieBaseModel().getName() );
            log.debug( "Added Bean for Stateful @KSession({})  from: {}",
                       kSessionName,
                       kModule );
            abd.addBean( new StatefulKSessionBean( kSessionName,
                                                   kSessionModel,
                                                   kieContainer,
                                                   entry.getKReleaseId(),
                                                   entry.getScope(),
                                                   entry.getName(),
                                                   entry.getInjectionPoints()  ) );
        }
    }
    
    public static class KContainerBean implements Bean<KieContainer>, PassivationCapable {
        static final Set<Type>                     types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieContainer.class,
                                                                                                                          Object.class ) ) );

        private final Set<Annotation>              qualifiers;

        private KieContainer                       kContainer;

        private final String                       named;

        private final Set<InjectionPoint>          injectionPoints;

        private final String id = "KContainerBean-" + UUID.randomUUID().toString();

        public KContainerBean(final KieContainer kContainer,
                              final KReleaseId kReleaseId,
                              final String named,
                              final Set<InjectionPoint> injectionPoints) {
            this.kContainer = kContainer;
            this.named = named;
            this.injectionPoints = injectionPoints;

            Set<Annotation> annotations = new HashSet<Annotation>();
            if ( kReleaseId == null ) {
                annotations.add( defaultAnnLit );
            }
            annotations.add( anyAnnLit );

            if ( named != null ) {
                annotations.add( new Named() {
                    public Class< ? extends Annotation> annotationType() {
                        return Named.class;
                    }

                    public String value() {
                        return named == null ? "" : named;
                    }

                    public String toString() {
                        return "Named[" + named + "]";
                    }
                } );
            }
            
            if ( kReleaseId != null ) {
                annotations.add( kReleaseId );
            } 

            this.qualifiers = Collections.unmodifiableSet( annotations );
        }
        
        public KieContainer create(CreationalContext ctx) {
            return kContainer;
        }

        public void destroy(KieContainer kContainer,
                            CreationalContext ctx) {
            this.kContainer = null;
            ctx.release();
        }

        public Class getBeanClass() {
            return KieContainer.class;
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
            return Dependent.class;
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
            return "KieContainer [qualifiers=" + qualifiers + "]";
        }

        @Override
        public String getId() {
            return id;
        }
    }

    public static class KBaseBean implements Bean<KieBase>, PassivationCapable {
        static final Set<Type>               types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieBase.class,
                                                                                                                    Object.class ) ) );

        private final Set<Annotation>              qualifiers;

        private KieContainer                       kContainer;

        private final KieBaseModel                 kBaseModel;

        private final Class< ? extends Annotation> scope;

        private final String                       named;
        
        private final Set<InjectionPoint>          injectionPoints;

        private String id = "KieBase-" + UUID.randomUUID().toString();

        public KBaseBean(final String kBaseQName,
                         final KieBaseModel kBaseModel,
                         final KieContainer kContainer,
                         final KReleaseId kReleaseId,
                         final Class< ? extends Annotation> scope,
                         final String named,
                         final Set<InjectionPoint> injectionPoints) {
            this.kBaseModel = kBaseModel;
            this.kContainer = kContainer;
            this.scope = scope;
            this.named = StringUtils.isEmpty( named ) ? null : named;
            this.injectionPoints = injectionPoints;

            Set<Annotation> annotations = new HashSet<>();
            if ( kBaseModel.isDefault() && kReleaseId == null ) {
                annotations.add( defaultAnnLit );
            }
            annotations.add( anyAnnLit );
            annotations.add( new KBase() {
                public Class< ? extends Annotation> annotationType() {
                    return KBase.class;
                }

                public String value() {
                    return kBaseQName == null ? "" : kBaseQName;
                }
                
                public String name() {
                    return named == null ? "" : named;
                }
            } );

            if ( kReleaseId != null ) {
                annotations.add( kReleaseId );
            }

            this.qualifiers = Collections.unmodifiableSet( annotations );
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

        @Override
        public String getId() {
            return id;
        }

    }

    public static class StatelessKSessionBean implements Bean<StatelessKieSession>, PassivationCapable {
        static final Set<Type>               types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( StatelessKieSession.class,
                                                                                                                    Object.class ) ) );

        private final Set<Annotation>              qualifiers;

        private final KieSessionModel              kSessionModel;

        private final KieContainer                 kContainer;

        private final Class< ? extends Annotation> scope;

        private final String                       named;
        
        private final Set<InjectionPoint>          injectionPoints;

        private final String id = "StatelessKSessionBean-" + UUID.randomUUID().toString();

        public StatelessKSessionBean(final KieSessionModel kieSessionModelModel,
                                     KieContainer kContainer,
                                     KReleaseId kReleaseId, 
                                     Class< ? extends Annotation> scope,
                                     final String named,
                                     Set<InjectionPoint> injectionPoints) {
            this.kSessionModel = kieSessionModelModel;
            this.kContainer = kContainer;
            this.scope = scope;
            this.named = StringUtils.isEmpty( named ) ? null : named;
            this.injectionPoints = injectionPoints;

            Set<Annotation> annotations = new HashSet<Annotation>();
            if ( kieSessionModelModel.isDefault() && kReleaseId == null ) {
                annotations.add( defaultAnnLit );
            }
            annotations.add( anyAnnLit );
            annotations.add( new KSession() {
                public Class< ? extends Annotation> annotationType() {
                    return KSession.class;
                }

                public String value() {
                    return kSessionModel.getName();
                }
                
                public String name() {
                    return named == null ? "" : named;
                }                
            } );
            if ( kReleaseId != null ) {
                annotations.add( kReleaseId );
            }
            this.qualifiers = Collections.unmodifiableSet( annotations );
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

        @Override
        public String getId() {
            return id;
        }
    }

    public static class StatefulKSessionBean implements Bean<KieSession>, PassivationCapable {
        static final Set<Type>               types = Collections.unmodifiableSet( new HashSet<Type>( Arrays.asList( KieSession.class,
                                                                                                                    Object.class ) ) );

        private final Set<Annotation>              qualifiers;

        private final KieSessionModel              kSessionModel;

        private final KieContainer                 kContainer;

        private final Class< ? extends Annotation> scope;

        private final String                       named;
        
        private final Set<InjectionPoint>          injectionPoints;

        private final String id = "StatefulKSessionBean-" + UUID.randomUUID().toString();

        public StatefulKSessionBean(final String kSessionName,
                                    final KieSessionModel kieSessionModel,
                                    final KieContainer kContainer,
                                    final KReleaseId kReleaseId,
                                    final Class< ? extends Annotation> scope,
                                    final String named,
                                    final Set<InjectionPoint> injectionPoints) {
            this.kSessionModel = kieSessionModel;
            this.kContainer = kContainer;
            this.scope = scope;
            this.named = StringUtils.isEmpty( named ) ? null : named;
            this.injectionPoints = injectionPoints;

            Set<Annotation> annotations = new HashSet<>();
            if ( kieSessionModel.isDefault() && kReleaseId == null ) {
                annotations.add( defaultAnnLit );
            }
            annotations.add( anyAnnLit );
            annotations.add( new KSession() {
                public Class< ? extends Annotation> annotationType() {
                    return KSession.class;
                }

                public String value() {
                    return kSessionName == null ? "" : kSessionName;
                }
                
                public String name() {
                    return named == null ? "" : named;
                }                
            } );

            if ( kReleaseId != null ) {
                annotations.add( kReleaseId );
            }

            this.qualifiers = Collections.unmodifiableSet( annotations );
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

        @Override
        public String getId() {
            return id;
        }
    }

    public static class KieCDIEntry {
        private String                       value;
        private Class                        type;
        private Class< ? extends Annotation> scope;
        private ReleaseId                    releaseId;
        private KReleaseId                   kReleaseId;
        private String                       name;        
        private Set<InjectionPoint>          injectionPoints;

        public KieCDIEntry(String value,
                           Class type,
                           Class< ? extends Annotation> scope,
                           ReleaseId releaseId,
                           KReleaseId kReleaseId,
                           String named) {
            super();
            this.value = value;
            this.type = type;
            this.scope = scope;
            this.releaseId = releaseId;
            this.kReleaseId = kReleaseId;
            this.name = named;
            this.injectionPoints = new HashSet<InjectionPoint>();
        }

        public KieCDIEntry(String value) {
            super();
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }               

        public Class getType() {
            return type;
        }

        public void setType(Class type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String named) {
            this.name = named;
        }

        public void setScope(Class< ? extends Annotation> scope) {
            this.scope = scope;
        }

        public Class< ? extends Annotation> getScope() {
            return scope;
        }

        public ReleaseId getReleaseId() {
            return releaseId;
        }

        public void setReleaseId(ReleaseId releaseId) {
            this.releaseId = releaseId;
        } 

        public KReleaseId getKReleaseId() {
            return kReleaseId;
        }

        public void setKReleaseId(KReleaseId kReleaseId) {
            this.kReleaseId = kReleaseId;
        }

        /**
         * InjectionPoints is not to be included in the equals/hashcode test
         */
        public void addInjectionPoint(InjectionPoint ip) {
            this.injectionPoints.add( ip );
        }

        /**
         * InjectionPoints is not to be included in the equals/hashcode test
         */
        public Set<InjectionPoint> getInjectionPoints() {
            return injectionPoints;
        }

        /**
         * InjectionPoints is not to be included in the equals/hashcode test
         */
        public void setInjectionPoints(Set<InjectionPoint> injectionPoints) {
            this.injectionPoints = injectionPoints;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((releaseId == null) ? 0 : releaseId.hashCode());
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + ((scope == null) ? 0 : scope.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            KieCDIEntry other = (KieCDIEntry) obj;
            if ( releaseId == null ) {
                if ( other.releaseId != null ) return false;
            } else if ( !releaseId.equals( other.releaseId ) ) return false;
            if ( name == null ) {
                if ( other.name != null ) return false;
            } else if ( !name.equals( other.name ) ) return false;
            if ( scope == null ) {
                if ( other.scope != null ) return false;
            } else if ( !scope.equals( other.scope ) ) return false;
            if ( type == null ) {
                if ( other.type != null ) return false;
            } else if ( !type.equals( other.type ) ) return false;
            if ( value == null ) {
                if ( other.value != null ) return false;
            } else if ( !value.equals( other.value ) ) return false;
            return true;
        }


    }

    private Class getClassType(InjectionPoint ip) {
        if (ip.getType() instanceof Class) {

            return (Class )ip.getType();
        } else if (ip.getType() instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) ip.getType()).getRawType();
            Type[] types = ((ParameterizedType) ip.getType()).getActualTypeArguments();
            if (rawType instanceof Class && ((Class) rawType).isAssignableFrom(Instance.class) &&
                types.length == 1 && types[0] instanceof Class) {
                return (Class) types[0];
            }
        }

        return null;
    }
}
