package org.drools.compiler.kie.builder.impl;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.compiler.kie.builder.impl.KieModuleCache.CompDataEntry;
import org.drools.compiler.kie.builder.impl.KieModuleCache.CompilationData;
import org.drools.compiler.kie.builder.impl.KieModuleCache.Header;
import org.drools.compiler.kie.builder.impl.KieModuleCache.KModuleCache;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.xml.PomModel;
import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeMetaInfo;
import org.drools.core.util.StringUtils;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.io.ResourceTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;

public abstract class AbstractKieModule
        implements
        InternalKieModule {

    private static final Logger log = LoggerFactory.getLogger(AbstractKieModule.class);

    private final Map<String, KnowledgeBuilder> kBuilders = new HashMap<String, KnowledgeBuilder>();

    private final Map<String, Results> resultsCache = new HashMap<String, Results>();

    protected final ReleaseId releaseId;

    private final KieModuleModel kModuleModel;

    private Map<ReleaseId, InternalKieModule> kieDependencies;

    // this is a { KBASE_NAME -> DIALECT -> ( RESOURCE, BYTECODE ) } cache
    protected Map<String, Map<String, Map<String, byte[]>>> compilationCache = new HashMap<String, Map<String, Map<String, byte[]>>>();

    private Map<String, TypeMetaInfo> typesMetaInfo;

    private Map<String, ResourceConfiguration> resourceConfigurationCache = new HashMap<String, ResourceConfiguration>();

    protected PomModel pomModel;

    public AbstractKieModule(ReleaseId releaseId, KieModuleModel kModuleModel) {
        this.releaseId = releaseId;
        this.kModuleModel = kModuleModel;
    }

    public KieModuleModel getKieModuleModel() {
        return this.kModuleModel;
    }

    public Map<ReleaseId, InternalKieModule> getKieDependencies() {
        return kieDependencies == null ? Collections.<ReleaseId, InternalKieModule> emptyMap() : kieDependencies;
    }

    public void addKieDependency(InternalKieModule dependency) {
        if (kieDependencies == null) {
            kieDependencies = new HashMap<ReleaseId, InternalKieModule>();
        }
        kieDependencies.put(dependency.getReleaseId(), dependency);
    }

    public Collection<ReleaseId> getJarDependencies() {
        if( pomModel == null ) {
            getPomModel();
        }
        Collection<ReleaseId> deps = null;
        if( pomModel != null ) {
            deps = pomModel.getDependencies();
        }
        return deps == null ? Collections.<ReleaseId> emptyList() : deps;
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
        for (String fileName : getFileNames()) {
            if (fileName.endsWith(".class")) {
                if (includeTypeDeclarations || !isTypeDeclaration(fileName)) {
                    classes.put(fileName, getBytes(fileName));
                }
            }
        }
        return classes;
    }

    private boolean isTypeDeclaration(String fileName) {
        Map<String, TypeMetaInfo> info = getTypesMetaInfo();
        TypeMetaInfo typeInfo = info == null ? null : info.get(convertResourceToClassName(fileName));
        return typeInfo != null && typeInfo.isDeclaredType();
    }

    private Map<String, TypeMetaInfo> getTypesMetaInfo() {
        if (typesMetaInfo == null) {
            byte[] bytes = getBytes(KieModuleModelImpl.KMODULE_INFO_JAR_PATH);
            if (bytes != null) {
                typesMetaInfo = KieModuleMetaInfo.unmarshallMetaInfos(new String(bytes)).getTypeMetaInfos();
            }
        }
        return typesMetaInfo;
    }

    @SuppressWarnings("deprecation")
    static KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel,
                                                    KieProject kieProject,
                                                    ResultsImpl messages ) {
        AbstractKieModule kModule = (AbstractKieModule) kieProject.getKieModuleForKBase(kBaseModel.getName());

        PackageBuilderConfiguration pconf = new PackageBuilderConfiguration(kieProject.getClonedClassLoader());
        pconf.setCompilationCache(kModule.getCompilationCache(kBaseModel.getName()));

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(pconf);
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        Map<String, InternalKieModule> assets = new HashMap<String, InternalKieModule>();

        for (String include : getTransitiveIncludes(kieProject, kBaseModel)) {
            if (StringUtils.isEmpty(include)) {
                continue;
            }
            InternalKieModule includeModule = kieProject.getKieModuleForKBase(include);
            if (includeModule == null) {
                String text = "Unable to build KieBase, could not find include: " + include;
                log.error(text);
                messages.addMessage(Message.Level.ERROR, KieModuleModelImpl.KMODULE_SRC_PATH, text);
                return null;
            }
            addFiles(assets,
                    kieProject.getKieBaseModel(include),
                    includeModule);
        }

        addFiles(assets,
                kBaseModel,
                kModule);

        if (assets.isEmpty()) {
            if (kModule instanceof FileKieModule) {
                log.warn("No files found for KieBase " + kBaseModel.getName() + ", searching folder " + kModule.getFile());
            } else {
                log.warn("No files found for KieBase " + kBaseModel.getName());
            }
        } else {
            for (Map.Entry<String, InternalKieModule> entry : assets.entrySet()) {
                entry.getValue().addResourceToCompiler(ckbuilder, entry.getKey());
            }
        }

        ckbuilder.build();

        if (kbuilder.hasErrors()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                messages.addMessage(error);
            }
            log.error("Unable to build KieBaseModel:" + kBaseModel.getName() + "\n" + kbuilder.getErrors().toString());
        }

        // cache KnowledgeBuilder and results
        kModule.cacheKnowledgeBuilderForKieBase(kBaseModel.getName(), kbuilder);
        kModule.cacheResultsForKieBase(kBaseModel.getName(), messages);

        return kbuilder;
    }

    private static Set<String> getTransitiveIncludes(KieProject kieProject, KieBaseModelImpl kBaseModel) {
        Set<String> includes = new HashSet<String>();
        getTransitiveIncludes(kieProject, kBaseModel, includes);
        return includes;
    }

    private static void getTransitiveIncludes(KieProject kieProject, KieBaseModelImpl kBaseModel, Set<String> includes) {
        if (kBaseModel == null) {
            return;
        }
        Set<String> incs = kBaseModel.getIncludes();
        if (incs != null && !incs.isEmpty()) {
            for (String inc : incs) {
                if (!includes.contains(inc)) {
                    includes.add(inc);
                    getTransitiveIncludes(kieProject, (KieBaseModelImpl) kieProject.getKieBaseModel(inc), includes);
                }
            }
        }
    }

    private static void addFiles(Map<String, InternalKieModule> assets,
                                 KieBaseModel kieBaseModel,
                                 InternalKieModule kieModule) {
        for (String fileName : kieModule.getFileNames()) {
            if (!fileName.startsWith(".") && !fileName.endsWith(".properties") && filterFileInKBase(kieModule, kieBaseModel, fileName)) {
                assets.put(fileName, kieModule);
            }
        }
    }

    public boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, String fileName) {
        ResourceConfiguration conf = getResourceConfiguration(fileName);
        Resource resource = getResource(fileName);
        if (resource != null) {
            if (conf == null) {
                ckbuilder.add(resource,
                              ResourceType.determineResourceType(fileName));
            } else {
                ResourceType confType = conf instanceof ResourceConfigurationImpl ?
                                        ((ResourceConfigurationImpl)conf).getResourceType() :
                                        null;
                ckbuilder.add(resource,
                              confType != null ? confType : ResourceType.determineResourceType(fileName),
                              conf);
            }
            return true;
        }
        return false;
    }

    public Resource getResource(String fileName) {
        byte[] bytes = getBytes(fileName);
        if (bytes != null && bytes.length > 0) {
            return ResourceFactory.newByteArrayResource(bytes).setSourcePath(fileName);
        }
        return null;
    }

    public ResourceConfiguration getResourceConfiguration(String fileName) {
        ResourceConfiguration conf = resourceConfigurationCache.get(fileName);
        if (conf != null) {
            return conf;
        }
        if (isAvailable(fileName + ".properties")) {
            // configuration file available
            Properties prop = new Properties();
            try {
                prop.load(new ByteArrayInputStream(getBytes(fileName + ".properties")));
            } catch (IOException e) {
                log.error("Error loading resource configuration from file: " + fileName + ".properties");
            }
            conf = ResourceTypeImpl.fromProperties(prop);
        } else if (ResourceType.DTABLE.matchesExtension(fileName)) {
            if (fileName.endsWith(".csv")) {
                Properties prop = new Properties();
                prop.setProperty(ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS, DecisionTableConfigurationImpl.class.getName());
                prop.setProperty(DecisionTableConfigurationImpl.DROOLS_DT_TYPE, DecisionTableInputType.CSV.toString());
                conf = ResourceTypeImpl.fromProperties(prop);
            }
        }
        resourceConfigurationCache.put(fileName, conf);
        return conf;
    }

    protected Map<String, Map<String, byte[]>> getCompilationCache(String kbaseName) {
        Map<String, Map<String, byte[]>> cache = compilationCache.get(kbaseName);
        if (cache == null) {
            byte[] fileContents = getBytes(KieBuilderImpl.getCompilationCachePath(releaseId, kbaseName));
            if (fileContents != null) {
                ExtensionRegistry registry = KieModuleCacheHelper.buildRegistry();
                try {
                    Header _header = KieModuleCacheHelper.readFromStreamWithHeaderPreloaded(new ByteArrayInputStream(fileContents), registry);
                    KModuleCache _cache = KModuleCache.parseFrom(_header.getPayload());

                    cache = new HashMap<String, Map<String, byte[]>>();
                    for (CompilationData _data : _cache.getCompilationDataList()) {
                        Map<String, byte[]> bytecode = new HashMap<String, byte[]>();
                        cache.put(_data.getDialect(), bytecode);
                        for (CompDataEntry _entry : _data.getEntryList()) {
                            bytecode.put(_entry.getId(), _entry.getData().toByteArray());
                        }
                    }
                    compilationCache.put(kbaseName, cache);
                } catch (Exception e) {
                    log.error("Unable to load compilation cache... ", e);
                }
            }
        }
        return cache;
    }

    public PomModel getPomModel() {
        if (pomModel == null) {
            try {
                byte[] pomXml = getPomXml();
                if( pomXml != null ) {
                    PomModel tempPomModel = PomModel.Parser.parse("pom.xml", new ByteArrayInputStream(pomXml));
                    validatePomModel(tempPomModel); // throws an exception if invalid
                    pomModel = tempPomModel;
                }
            } catch( Exception e ) {
                // nothing to do as it was not possible to retrieve pom.xml
            }
        }
        return pomModel;
    }

    public void setPomModel(PomModel pomModel) {
        this.pomModel = pomModel;
    }

    private void validatePomModel(PomModel pomModel) {
        ReleaseId pomReleaseId = pomModel.getReleaseId();
        if (StringUtils.isEmpty(pomReleaseId.getGroupId()) || StringUtils.isEmpty(pomReleaseId.getArtifactId()) || StringUtils.isEmpty(pomReleaseId.getVersion())) {
            throw new RuntimeException("Maven pom.properties exists but ReleaseId content is malformed");
        }
    }

    private byte[] getPomXml() {
        return getBytes(((ReleaseIdImpl)releaseId).getPomXmlPath());
    }

    public static boolean updateResource(CompositeKnowledgeBuilder ckbuilder, 
                                         InternalKieModule kieModule,
                                         String resourceName,
                                         ResourceChangeSet changes) {
        ResourceConfiguration conf = kieModule.getResourceConfiguration(resourceName);
        Resource resource = kieModule.getResource(resourceName);
        if (resource != null) {
            if (conf == null) {
                ckbuilder.add(resource,
                        ResourceType.determineResourceType(resourceName),
                        changes );
            } else {
                ckbuilder.add(resource,
                        ResourceType.determineResourceType(resourceName),
                        conf,
                        changes );
            }
            return true;
        }
        return false;
    }
    
}
