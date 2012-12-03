package org.kie.builder.impl;

import org.drools.impl.InternalKnowledgeBase;
import org.drools.kproject.KieBaseModelImpl;
import org.drools.kproject.KieSessionModelImpl;
import org.drools.rule.Collect;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModule;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractKieModules
    implements
    InternalKieModule {

    private static final Logger                             log               = LoggerFactory.getLogger( AbstractKieModules.class );

    private ClassLoader                                     classLoader;

    private final Map<String, Collection<KnowledgePackage>> packageCache      = new HashMap<String, Collection<KnowledgePackage>>();

    protected final GAV                                     gav;

    private Map<GAV, InternalKieModule>                     dependencies;

    private Map<String, InternalKieModule>                  kJarFromKBaseName = new HashMap<String, InternalKieModule>();

    private Map<String, KieBaseModel>                       kBaseModels       = new HashMap<String, KieBaseModel>();
    private Map<String, KieSessionModel>                    kSessionModels    = new HashMap<String, KieSessionModel>();

    public AbstractKieModules(GAV gav) {
        this.gav = gav;
    }    
    
    public void verify() {
        Map<GAV, InternalKieModule> kieModules = new HashMap<GAV, InternalKieModule>();
        kieModules.putAll( dependencies );
        kieModules.put( gav, this );
        indexParts( kieModules, kBaseModels, kSessionModels, kJarFromKBaseName );        
    }

    public Map<GAV, InternalKieModule> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<GAV, InternalKieModule> dependencies) {
        this.dependencies = dependencies;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public GAV getGAV() {
        return gav;
    }

    public Map<String, Collection<KnowledgePackage>> getKnowledgePackageCache() {
        return packageCache;
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

    public static void indexParts(Map<GAV, InternalKieModule> kJars,
                                  Map<String, KieBaseModel> kBaseModels,
                                  Map<String, KieSessionModel> kSessionModels,
                                  Map<String, InternalKieModule> kJarFromKBaseName) {
        for ( InternalKieModule kJar : kJars.values() ) {
            KieProjectModel kieProject = kJar.getKieProjectModel();
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                kBaseModels.put( kieBaseModel.getName(),
                                 kieBaseModel );
                ((KieBaseModelImpl) kieBaseModel).setKProject( kieProject ); // should already be set, but just in case

                kJarFromKBaseName.put( kieBaseModel.getName(),
                                       kJar );
                for ( KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values() ) {
                    ((KieSessionModelImpl) kieSessionModel).setKBase( kieBaseModel ); // should already be set, but just in case
                    kSessionModels.put( kieSessionModel.getName(),
                                        kieSessionModel );
                }
            }
        }
    }

    public static KieBase createKieBase(KieBaseModel kBaseModel,
                                        KieProject indexedParts) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        Set<String> includes = kBaseModel.getIncludes();
        if ( includes != null && !includes.isEmpty() ) {
            for ( String include : includes ) {
                InternalKieModule includeModule = indexedParts.getKieModuleForKBase( include );
                if ( includeModule == null ) {
                    log.error( "Unable to build KieBase, could not find include: " + include );
                    return null;
                }
                addFiles( ckbuilder,
                          indexedParts.getKieBaseModel( include ),
                          includeModule );
            }
        }

        InternalKieModule kModule = indexedParts.getKieModuleForKBase( kBaseModel.getName() );
        addFiles( ckbuilder,
                  kBaseModel,
                  kModule );

        ckbuilder.build();

        if ( kbuilder.hasErrors() ) {
            log.error( "Unable to build KieBaseModel:" + kBaseModel.getName() + "\n" + kbuilder.getErrors().toString() );
        }

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return kBase;
    }

    public static void addFiles(CompositeKnowledgeBuilder ckbuilder,
                                KieBaseModel kieBaseModel,
                                InternalKieModule kieJar) {
        int fileCount = 0;
        String prefixPath = kieBaseModel.getName().replace( '.',
                                                            '/' );
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
}
