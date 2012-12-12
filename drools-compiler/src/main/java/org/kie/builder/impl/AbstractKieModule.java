package org.kie.builder.impl;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.core.util.StringUtils;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.kproject.models.KieBaseModelImpl;
import org.kie.KieBase;
import org.kie.KieBaseConfiguration;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.CompositeKnowledgeBuilder;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.Results;
import org.kie.definition.KnowledgePackage;
import org.kie.internal.utils.CompositeClassLoader;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.io.ResourceTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.kie.builder.impl.KieBuilderImpl.filterFileInKBase;

public abstract class AbstractKieModule
    implements
    InternalKieModule {

    private static final Logger                             log               = LoggerFactory.getLogger( AbstractKieModule.class );

    private final Map<String, Collection<KnowledgePackage>> packageCache      = new HashMap<String, Collection<KnowledgePackage>>();
    
    private final Map<String, Results>                      resultsCache      = new HashMap<String, Results>();

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

    public Map<String, Results> getKnowledgeResultsCache() {
        return resultsCache;
    }    
    
    @SuppressWarnings("deprecation")
    static KnowledgeBuilder buildKnowledgePackages(KieBaseModelImpl kBaseModel,
                                                   KieProject indexedParts,
                                                   ResultsImpl messages) {
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
        } else {
            // no errors, so cache the packages
            kModule.getKnowledgePackageCache().put( kBaseModel.getName(), 
                                                    kbuilder.getKnowledgePackages() );     
        }
        
        // always cache results
        kModule.getKnowledgeResultsCache().put( kBaseModel.getName(), 
                                                messages );   
        
        return kbuilder;        
    }
    
    @SuppressWarnings("deprecation")
    public static KieBase createKieBase(KieBaseModelImpl kBaseModel,
                                        KieProject indexedParts,
                                        ResultsImpl messages) {
        CompositeClassLoader cl = indexedParts.getClassLoader(); // the most clone the CL, as each builder and rbase populates it

        InternalKieModule kModule = indexedParts.getKieModuleForKBase( kBaseModel.getName() );
        
        Collection<KnowledgePackage> pkgs = kModule.getKnowledgePackageCache().get( kBaseModel.getName()  );
        
        if ( pkgs == null ) {
            KnowledgeBuilder kbuilder = buildKnowledgePackages(kBaseModel, indexedParts, messages);
            if ( kbuilder.hasErrors() ) {
                // Messages already populated by the buildKnowlegePackages
                return null;
            }        
        }
        
        // if we get to here, then we know the pkgs is now cached
        pkgs = kModule.getKnowledgePackageCache().get( kBaseModel.getName()  );

        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase( getKnowledgeBaseConfiguration(kBaseModel, cl) );

        kBase.addKnowledgePackages( pkgs );
        return kBase;
    }

    private static KieBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModelImpl kBaseModel, ClassLoader cl) {
        KieBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, cl);
        kbConf.setOption(kBaseModel.getEqualsBehavior());
        kbConf.setOption(kBaseModel.getEventProcessingMode());
        return kbConf;
    }

    public static void addFiles(CompositeKnowledgeBuilder ckbuilder,
                                KieBaseModel kieBaseModel,
                                InternalKieModule kieModule) {
        int fileCount = 0;
        for ( String fileName : kieModule.getFileNames() ) {
            if ( !fileName.endsWith( ".properties" ) && filterFileInKBase(kieBaseModel, fileName) ) {
                ResourceConfiguration conf = null;
                if( kieModule.isAvailable( fileName+".properties" ) ) {
                    // configuration file available
                    Properties prop = new Properties();
                    try {
                        prop.load( new ByteArrayInputStream( kieModule.getBytes(fileName+".properties") ) );
                    } catch ( IOException e ) {
                        log.error( "Error loading resource configuration from file: "+fileName+".properties" );
                    }
                    conf = ResourceTypeImpl.fromProperties( prop );
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
        if ( fileCount == 0 ) {
            if (kieModule instanceof FileKieModule) {
                log.warn("No files found for KieBase " + kieBaseModel.getName() + ", searching folder " + kieModule.getFile());
            } else {
                log.warn("No files found for KieBase " + kieBaseModel.getName());
            }
        }

    }
}
