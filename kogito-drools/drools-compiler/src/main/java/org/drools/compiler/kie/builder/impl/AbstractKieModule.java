package org.drools.compiler.kie.builder.impl;

import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.core.util.StringUtils;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.CompositeClassLoader;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceType;
import org.kie.internal.io.ResourceTypeImpl;
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

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;

public abstract class AbstractKieModule
    implements
    InternalKieModule {

    private static final Logger                             log               = LoggerFactory.getLogger( AbstractKieModule.class );

    private final Map<String, KnowledgeBuilder>             kBuilders         = new HashMap<String, KnowledgeBuilder>();
    
    private final Map<String, Results>                      resultsCache      = new HashMap<String, Results>();

    protected final ReleaseId releaseId;
    
    private final KieModuleModel                            kModuleModel;

    private Map<ReleaseId, InternalKieModule>               dependencies;
    

    public AbstractKieModule(ReleaseId releaseId, KieModuleModel kModuleModel) {
        this.releaseId = releaseId;
        this.kModuleModel = kModuleModel;
    }   
    
    public KieModuleModel getKieModuleModel() {
        return this.kModuleModel;
    }
    
    public Map<ReleaseId, InternalKieModule> getDependencies() {
        return dependencies == null ? Collections.<ReleaseId, InternalKieModule>emptyMap() : dependencies;
    }

    public void addDependency(InternalKieModule dependency) {
        if (dependencies == null) {
            dependencies = new HashMap<ReleaseId, InternalKieModule>();
        }
        dependencies.put(dependency.getReleaseId(), dependency);
    }

    public ReleaseId getReleaseId() {
        return releaseId;
    }

    public KnowledgeBuilder getKnowledgeBuilderForKieBase(String kieBaseName) {
        return kBuilders.get(kieBaseName);
    }

    public Collection<KnowledgePackage> getKnowledgePackagesForKieBase(String kieBaseName) {
        KnowledgeBuilder kbuilder = kBuilders.get(kieBaseName);
        return kbuilder != null ? kbuilder.getKnowledgePackages() : null;
    }

    public void cacheKnowledgeBuilderForKieBase(String kieBaseName, KnowledgeBuilder kbuilder) {
        kBuilders.put(kieBaseName, kbuilder);
    }

    public Map<String, Results> getKnowledgeResultsCache() {
        return resultsCache;
    }

    public void cacheResultsForKieBase(String kieBaseName, Results results) {
        resultsCache.put(kieBaseName, results);
    }

    public Map<String, byte[]> getClassesMap() {
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        for ( String fileName : getFileNames() ) {
            if ( fileName.endsWith( ".class" ) ) {
                classes.put( fileName, getBytes( fileName ) );
            }
        }
        return classes;
    }

    @SuppressWarnings("deprecation")
    static KnowledgeBuilder buildKnowledgePackages(KieBaseModelImpl kBaseModel,
                                                   KieProject kieProject,
                                                   ResultsImpl messages) {
        CompositeClassLoader cl = kieProject.getClassLoader(); // the most clone the CL, as each builder and rbase populates it

        PackageBuilderConfiguration pconf = new PackageBuilderConfiguration( null,
                                                                             cl.clone() );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pconf);
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        Set<String> includes = kBaseModel.getIncludes();
        if ( includes != null && !includes.isEmpty() ) {
            for ( String include : includes ) {
                if ( StringUtils.isEmpty( include ) ) {
                    continue;
                }
                InternalKieModule includeModule = kieProject.getKieModuleForKBase( include );
                if ( includeModule == null ) {
                    log.error( "Unable to build KieBase, could not find include: " + include );
                    return null;
                }
                addFiles(ckbuilder,
                        kieProject.getKieBaseModel(include),
                        includeModule);
            }
        }

        InternalKieModule kModule = kieProject.getKieModuleForKBase( kBaseModel.getName() );
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
        
        // cache KnowledgeBuilder and results
        kModule.cacheKnowledgeBuilderForKieBase( kBaseModel.getName(), kbuilder );
        kModule.cacheResultsForKieBase( kBaseModel.getName(), messages );
        
        return kbuilder;        
    }
    
    private static void addFiles( CompositeKnowledgeBuilder ckbuilder,
                                  KieBaseModel kieBaseModel,
                                  InternalKieModule kieModule ) {
        int fileCount = 0;
        for ( String fileName : kieModule.getFileNames() ) {
            if ( filterFileInKBase(kieBaseModel, fileName) && !fileName.endsWith( ".properties" ) ) {
                ResourceConfiguration conf = getResourceConfiguration(kieModule, fileName);
                byte[] bytes = kieModule.getBytes( fileName );
                if ( bytes == null || bytes.length == 0 ) {
                    continue;
                }
                if ( conf == null ) {
                    ckbuilder.add( ResourceFactory.newByteArrayResource( bytes ).setSourcePath( fileName ),
                                   ResourceType.determineResourceType( fileName ) );
                } else {
                    ckbuilder.add( ResourceFactory.newByteArrayResource(bytes).setSourcePath(fileName),
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

    public static ResourceConfiguration getResourceConfiguration(InternalKieModule kieModule, String fileName) {
        ResourceConfiguration conf = null;
        if( kieModule.isAvailable( fileName+".properties" ) ) {
            // configuration file available
            Properties prop = new Properties();
            try {
                prop.load( new ByteArrayInputStream( kieModule.getBytes(fileName+".properties") ) );
            } catch ( IOException e ) {
                log.error( "Error loading resource configuration from file: "+fileName+".properties" );
            }
            conf = ResourceTypeImpl.fromProperties(prop);
        }
        return conf;
    }
}
