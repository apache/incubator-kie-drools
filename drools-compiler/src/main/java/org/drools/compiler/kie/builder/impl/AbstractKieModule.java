package org.drools.compiler.kie.builder.impl;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.compiler.kie.builder.impl.KieModuleCache.CompDataEntry;
import org.drools.compiler.kie.builder.impl.KieModuleCache.CompilationData;
import org.drools.compiler.kie.builder.impl.KieModuleCache.Header;
import org.drools.compiler.kie.builder.impl.KieModuleCache.KModuleCache;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.rule.TypeMetaInfo;
import org.drools.core.util.StringUtils;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.util.StringUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceTypeImpl;
import org.kie.internal.utils.CompositeClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.core.rule.TypeMetaInfo.unmarshallMetaInfos;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;

public abstract class AbstractKieModule
    implements
    InternalKieModule {

    private static final Logger                             log               = LoggerFactory.getLogger( AbstractKieModule.class );

    private final Map<String, KnowledgeBuilder>             kBuilders         = new HashMap<String, KnowledgeBuilder>();
    
    private final Map<String, Results>                      resultsCache      = new HashMap<String, Results>();

    protected final ReleaseId releaseId;
    
    private final KieModuleModel                            kModuleModel;

    private Map<ReleaseId, InternalKieModule>               dependencies;

    // this is a { KBASE_NAME -> DIALECT -> ( RESOURCE, BYTECODE ) } cache
    protected Map<String, Map<String, Map<String, byte[]>>> compilationCache  = new HashMap<String, Map<String,Map<String,byte[]>>>();

    private Map<String, TypeMetaInfo>                       typesMetaInfo;

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

    public Map<String, byte[]> getClassesMap(boolean includeTypeDeclarations) {
        Map<String, byte[]> classes = new HashMap<String, byte[]>();
        for ( String fileName : getFileNames() ) {
            if ( fileName.endsWith( ".class" ) ) {
                if ( includeTypeDeclarations || !isTypeDeclaration(fileName) ) {
                    classes.put( fileName, getBytes( fileName ) );
                }
            }
        }
        return classes;
    }

    private boolean isTypeDeclaration(String fileName) {
        Map<String, TypeMetaInfo> info = getTypesMetaInfo();
        TypeMetaInfo typeInfo = info == null ? null : info.get(convertResourceToClassName( fileName ));
        return typeInfo != null && typeInfo.isDeclaredType();
    }

    private Map<String, TypeMetaInfo> getTypesMetaInfo() {
        if (typesMetaInfo == null) {
            byte[] bytes = getBytes( KieModuleModelImpl.KMODULE_INFO_JAR_PATH );
            if (bytes != null) {
                typesMetaInfo = unmarshallMetaInfos(new String(bytes));
            }
        }
        return typesMetaInfo;
    }

    @SuppressWarnings("deprecation")
    static KnowledgeBuilder buildKnowledgePackages(KieBaseModelImpl kBaseModel,
                                                   KieProject kieProject,
                                                   ResultsImpl messages) {
        AbstractKieModule kModule = (AbstractKieModule) kieProject.getKieModuleForKBase( kBaseModel.getName() );

        PackageBuilderConfiguration pconf = new PackageBuilderConfiguration( kieProject.getClassLoader() );
        pconf.setCompilationCache( kModule.getCompilationCache( kBaseModel.getName() ) );

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
        } else if( ResourceType.DTABLE.matchesExtension( fileName ) ) {
            if( fileName.endsWith( ".csv" ) ) {
                Properties prop = new Properties();
                prop.setProperty( ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS, DecisionTableConfigurationImpl.class.getName() );
                prop.setProperty( DecisionTableConfigurationImpl.DROOLS_DT_TYPE, DecisionTableInputType.CSV.toString() );
                conf = ResourceTypeImpl.fromProperties(prop);
            }
        }
        return conf;
    }
    
    protected Map<String, Map<String, byte[]>> getCompilationCache( String kbaseName ) {
        Map<String, Map<String, byte[]>> cache = compilationCache.get( kbaseName );
        if( cache == null ) {
            byte[] fileContents = getBytes( KieBuilderImpl.getCompilationCachePath( releaseId, kbaseName ) );
            if( fileContents != null) {
                ExtensionRegistry registry = KieModuleCacheHelper.buildRegistry();
                try {
                    Header _header = KieModuleCacheHelper.readFromStreamWithHeaderPreloaded( new ByteArrayInputStream( fileContents ), registry );
                    KModuleCache _cache = KModuleCache.parseFrom( _header.getPayload() );
                    
                    cache = new HashMap<String, Map<String,byte[]>>();
                    for( CompilationData _data : _cache.getCompilationDataList() ) {
                        Map<String,byte[]> bytecode = new HashMap<String, byte[]>();
                        cache.put( _data.getDialect(), bytecode );
                        for( CompDataEntry _entry : _data.getEntryList() ) {
                            bytecode.put( _entry.getId(), _entry.getData().toByteArray() );
                        }
                    }
                    compilationCache.put( kbaseName, cache );
                } catch (Exception e ) {
                    log.error( "Unable to load compilation cache... ", e );
                }
            }
        }
        return cache;
    }
    
}
