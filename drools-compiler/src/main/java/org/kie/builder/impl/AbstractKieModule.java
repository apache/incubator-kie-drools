package org.kie.builder.impl;

import static org.kie.builder.impl.KieBuilderImpl.isKieExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.StringUtils;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.kproject.models.KieBaseModelImpl;
import org.drools.kproject.models.KieSessionModelImpl;
import org.kie.KieBase;
import org.kie.KnowledgeBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.definition.KnowledgePackage;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.util.CompositeClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractKieModule
    implements
    InternalKieModule {

    private static final Logger                             log               = LoggerFactory.getLogger( AbstractKieModule.class );

    private final Map<String, Collection<KnowledgePackage>> packageCache      = new HashMap<String, Collection<KnowledgePackage>>();

    protected final GAV                                     gav;
    
    private final KieModuleModel                            kModuleModel;

    private Map<GAV, InternalKieModule>                     dependencies;
    

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
        return dependencies == null ? Collections.<GAV, InternalKieModule>emptyMap() : dependencies;
    }

    public void addDependency(InternalKieModule dependency) {
        if (dependencies == null) {
            dependencies = new HashMap<GAV, InternalKieModule>();
        }
        dependencies.put(dependency.getGAV(), dependency);
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

    static KieBase createKieBase(KieBaseModel kBaseModel,
                                 KieProject indexedParts) {
        return createKieBase(( KieBaseModelImpl ) kBaseModel, indexedParts, new Messages() );
    }
    
    @SuppressWarnings("deprecation")
    static KieBase createKieBase(KieBaseModelImpl kBaseModel,
                                        KieProject indexedParts,
                                        Messages messages) {
        CompositeClassLoader cl = indexedParts.getClassLoader(); // the most clone the CL, as each builder and rbase populates it

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

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase( getKnowledgeBaseConfiguration(kBaseModel, cl) );

        kBase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kBase;
    }

    private static KnowledgeBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModelImpl kBaseModel, ClassLoader cl) {
        KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, cl);
        kbConf.setOption(kBaseModel.getEqualsBehavior());
        kbConf.setOption(kBaseModel.getEventProcessingMode());
        return kbConf;
    }

    public static void addFiles(CompositeKnowledgeBuilder ckbuilder,
                                KieBaseModel kieBaseModel,
                                InternalKieModule kieModule) {
        int fileCount = 0;
        String prefixPath = kieBaseModel.getName().replace( '.',
                                                            '/' );
        for ( String fileName : kieModule.getFileNames() ) {
            if ( ((KieBaseModelImpl)kieBaseModel).isDefault() || fileName.startsWith( prefixPath ) ) {
                if ( isKieExtension(fileName) && !fileName.endsWith( ".properties" )) {
                    ResourceConfiguration conf = null;
                    if( kieModule.isAvailable( fileName+".properties" ) ) {
                        // configuration file available
                        Properties prop = new Properties();
                        try {
                            prop.load( new ByteArrayInputStream( kieModule.getBytes(fileName+".properties") ) );
                        } catch ( IOException e ) {
                            log.error( "Error loading resource configuration from file: "+fileName+".properties" );
                        }
                        conf = ResourceType.fromProperties( prop );
                    }
                    if( conf == null ) {
                        ckbuilder.add( ResourceFactory.newByteArrayResource( kieModule.getBytes( fileName ) ),
                                       ResourceType.determineResourceType( fileName ) );
                    } else {
                        ckbuilder.add( ResourceFactory.newByteArrayResource( kieModule.getBytes( fileName ) ),
                                       ResourceType.determineResourceType( fileName ),
                                       conf );
                    }
                    fileCount++;
                }
            }
        }
        if ( fileCount == 0 ) {
            if (kieModule instanceof FileKieModule) {
                log.warn("No files found for KieBase " + kieBaseModel.getName() + ", searching folder " + kieModule.getFile());
            } else {
                log.warn("No files found for KieBase " + kieBaseModel.getName());
            }
        }

    }
}
