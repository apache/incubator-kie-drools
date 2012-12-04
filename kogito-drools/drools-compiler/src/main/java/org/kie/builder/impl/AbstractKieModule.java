package org.kie.builder.impl;

import org.drools.RuleBaseConfiguration;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.KieBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieContainer;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceFactory;
import org.kie.util.ClassLoaderUtil;
import org.kie.util.CompositeClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.kie.builder.impl.KieBuilderImpl.isKieExtension;

public abstract class AbstractKieModule
    implements
    InternalKieModule {

    private static final Logger                             log               = LoggerFactory.getLogger( AbstractKieModule.class );

    private final Map<String, Collection<KnowledgePackage>> packageCache      = new HashMap<String, Collection<KnowledgePackage>>();

    protected final GAV                                     gav;
    
    private final KieModuleModel                            kModuleModel;

    private Map<GAV, InternalKieModule>                     dependencies      =  Collections.<GAV, InternalKieModule>emptyMap();
    

    public AbstractKieModule(GAV gav, KieModuleModel kModuleModel) {
        this.gav = gav;
        this.kModuleModel = kModuleModel;
    }   
    
    public KieModuleModel getKieModuleModel() {
        return this.kModuleModel;
    }
    
//    public void index() {
//        if ( kieModules == null ) { 
//            kieModules = new HashMap<GAV, InternalKieModule>();
//            kieModules.putAll( dependencies );
//            kieModules.put( gav, this );
//            indexParts( kieModules, kBaseModels, kSessionModels, kJarFromKBaseName );
//        }
//    }
//    
//    public Map<GAV, InternalKieModule> getKieModules() {
//        if ( kieModules == null ) {
//            index();
//        }        
//        return kieModules;
//    }
    
//    public void verify(Messages messages) {
//        if ( kieModules == null ) {
//            kieModules = new HashMap<GAV, InternalKieModule>();
//            kieModules.putAll( dependencies );
//            kieModules.put( gav, this );
//            indexParts( kieModules, kBaseModels, kSessionModels, kJarFromKBaseName );       
//            
//            for ( KieBaseModel model : kBaseModels.values() ) {
//                createKieBase( ( KieBaseModelImpl)  model, this, messages );
//            }
//        }
//     }    

    public Map<GAV, InternalKieModule> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<GAV, InternalKieModule> dependencies) {
        this.dependencies = dependencies;
    }

    public GAV getGAV() {
        return gav;
    }

    public Map<String, Collection<KnowledgePackage>> getKnowledgePackageCache() {
        return packageCache;
    }

    public static void indexParts(Map<GAV, InternalKieModule> kJars,
                                  Map<String, KieBaseModel> kBaseModels,
                                  Map<String, KieSessionModel> kSessionModels,
                                  Map<String, InternalKieModule> kJarFromKBaseName) {
        for ( InternalKieModule kJar : kJars.values() ) {
            KieModuleModel kieProject = kJar.getKieModuleModel();
            for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
                kBaseModels.put( kieBaseModel.getName(),
                                 kieBaseModel );
                ((KieBaseModelImpl) kieBaseModel).setKModule( kieProject ); // should already be set, but just in case

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
        return createKieBase(( KieBaseModelImpl ) kBaseModel, indexedParts, new Messages() );
    }
    
    public static KieBase createKieBase(KieBaseModelImpl kBaseModel,
                                        KieProject indexedParts,
                                        Messages messages) {
        CompositeClassLoader cl = ( CompositeClassLoader ) indexedParts.getClassLoader(); // the most clone the CL, as each builder and rbase populates it

        PackageBuilderConfiguration pconf = new PackageBuilderConfiguration( null,
                                                                             cl.clone() );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( pconf );
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        Set<String> includes = kBaseModel.getIncludes();
        if ( includes != null && !includes.isEmpty() ) {
            for ( String include : includes ) {
                if ( StringUtils.isEmpty( include ) ) {
                    continue;
                }
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
            for ( KnowledgeBuilderError error : kbuilder.getErrors() ) {
                messages.addMessage( error );
            }
            log.error( "Unable to build KieBaseModel:" + kBaseModel.getName() + "\n" + kbuilder.getErrors().toString() );
        }

        RuleBaseConfiguration rconf = new RuleBaseConfiguration( null,
                                                                 cl );
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase( rconf );

        kBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kBase;
    }

    public static void addFiles(CompositeKnowledgeBuilder ckbuilder,
                                KieBaseModel kieBaseModel,
                                InternalKieModule kieModule) {
        int fileCount = 0;
        String prefixPath = kieBaseModel.getName().replace( '.',
                                                            '/' );
        for ( String fileName : kieModule.getFileNames() ) {
            if ( ((KieBaseModelImpl)kieBaseModel).isDefault() || fileName.startsWith( prefixPath ) ) {
                if ( isKieExtension(fileName) ) {
                    ckbuilder.add( ResourceFactory.newByteArrayResource( kieModule.getBytes( fileName ) ),
                                   ResourceType.determineResourceType( fileName ) );
                    fileCount++;
                }
            }
        }
        if ( fileCount == 0 ) {
            log.warn("No files found for KieBase " + kieBaseModel.getName() + ", searching folder " + kieModule.getFile());
        }

    }
}
