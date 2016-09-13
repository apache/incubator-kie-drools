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

package org.drools.compiler.kie.builder.impl;

import com.google.protobuf.ExtensionRegistry;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.kie.builder.impl.KieModuleCache.CompDataEntry;
import org.drools.compiler.kie.builder.impl.KieModuleCache.CompilationData;
import org.drools.compiler.kie.builder.impl.KieModuleCache.Header;
import org.drools.compiler.kie.builder.impl.KieModuleCache.KModuleCache;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.compiler.kproject.xml.DependencyFilter;
import org.drools.compiler.kproject.xml.PomModel;
import org.drools.core.builder.conf.impl.DecisionTableConfigurationImpl;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.common.ResourceProvider;
import org.drools.core.rule.KieModuleMetaInfo;
import org.drools.core.rule.TypeMetaInfo;
import org.drools.core.util.Drools;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.RuleTemplateModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.io.ResourceTypeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.filterFileInKBase;
import static org.drools.core.util.ClassUtils.convertResourceToClassName;

public abstract class AbstractKieModule
        implements
        InternalKieModule {

    private static final Logger log = LoggerFactory.getLogger(AbstractKieModule.class);

    private final Map<String, KnowledgeBuilder> kBuilders = new HashMap<String, KnowledgeBuilder>();

    private final Map<String, Results> resultsCache = new HashMap<String, Results>();

    protected final ReleaseId releaseId;

    private final KieModuleModel kModuleModel;

    private Map<ReleaseId, InternalKieModule> kieDependencies;

    // Map< KBaseName, CompilationCache>
    protected Map<String, CompilationCache> compilationCache = new HashMap<String, CompilationCache>();

    private Map<String, TypeMetaInfo> typesMetaInfo;

    private Map<String, ResourceConfiguration> resourceConfigurationCache = new HashMap<String, ResourceConfiguration>();

    protected PomModel pomModel;

    private Collection<ReleaseId> unresolvedDependencies;

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

    public Collection<ReleaseId> getJarDependencies(DependencyFilter filter) {
        if( pomModel == null ) {
            getPomModel();
        }
        Collection<ReleaseId> deps = null;
        if( pomModel != null ) {
            deps = pomModel.getDependencies(filter);
        }
        return deps == null ? Collections.<ReleaseId> emptyList() : deps;
    }

    public Collection<ReleaseId> getUnresolvedDependencies() {
        return unresolvedDependencies == null ? Collections.<ReleaseId> emptyList() : unresolvedDependencies;
    }

    public void setUnresolvedDependencies(Collection<ReleaseId> unresolvedDependencies) {
        this.unresolvedDependencies = unresolvedDependencies;
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
                typesMetaInfo = KieModuleMetaInfo.unmarshallMetaInfos(new String(bytes, IoUtils.UTF8_CHARSET)).getTypeMetaInfos();
            }
        }
        return typesMetaInfo;
    }

    @SuppressWarnings("deprecation")
    static KnowledgeBuilder buildKnowledgePackages( KieBaseModelImpl kBaseModel,
                                                    KieProject kieProject,
                                                    ResultsImpl messages ) {
        AbstractKieModule kModule = (AbstractKieModule) kieProject.getKieModuleForKBase(kBaseModel.getName());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(getBuilderConfiguration(kBaseModel, kieProject, kModule));
        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        Set<Asset> assets = new HashSet<Asset>();

        boolean allIncludesAreValid = true;
        for (String include : kieProject.getTransitiveIncludes(kBaseModel)) {
            if (StringUtils.isEmpty(include)) {
                continue;
            }
            InternalKieModule includeModule = kieProject.getKieModuleForKBase(include);
            if (includeModule == null) {
                String text = "Unable to build KieBase, could not find include: " + include;
                log.error(text);
                messages.addMessage(Message.Level.ERROR, KieModuleModelImpl.KMODULE_SRC_PATH, text);
                allIncludesAreValid = false;
                continue;
            }
            addFiles( assets, kieProject.getKieBaseModel(include), includeModule );
        }

        if (!allIncludesAreValid) {
            return null;
        }

        addFiles( assets, kBaseModel, kModule );

        if (assets.isEmpty()) {
            if (kModule instanceof FileKieModule) {
                log.warn("No files found for KieBase " + kBaseModel.getName() + ", searching folder " + kModule.getFile());
            } else {
                log.warn("No files found for KieBase " + kBaseModel.getName());
            }
        } else {
            for (Asset asset : assets) {
                asset.kmodule.addResourceToCompiler(ckbuilder, kBaseModel, asset.name);
            }
        }

        ckbuilder.build();

        if ( kbuilder.hasErrors() ) {
            for ( KnowledgeBuilderError error : kbuilder.getErrors() ) {
                messages.addMessage( error );
            }
            log.error("Unable to build KieBaseModel:" + kBaseModel.getName() + "\n" + kbuilder.getErrors().toString());
        }
        if ( kbuilder.hasResults( ResultSeverity.WARNING ) ) {
            for ( KnowledgeBuilderResult warn : kbuilder.getResults( ResultSeverity.WARNING ) ) {
                messages.addMessage( warn );
            }
            log.warn( "Warning : " + kBaseModel.getName() + "\n" + kbuilder.getResults( ResultSeverity.WARNING ).toString() );
        }

        // cache KnowledgeBuilder and results
        kModule.cacheKnowledgeBuilderForKieBase(kBaseModel.getName(), kbuilder);
        kModule.cacheResultsForKieBase(kBaseModel.getName(), messages);

        return kbuilder;
    }

    private static class Asset {
        private final InternalKieModule kmodule;
        private final String name;

        private Asset( InternalKieModule kmodule, String name ) {
            this.kmodule = kmodule;
            this.name = name;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            Asset asset = (Asset) o;
            return kmodule.equals( asset.kmodule ) && name.equals( asset.name );
        }

        @Override
        public int hashCode() {
            int result = kmodule.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }

    private static KnowledgeBuilderConfigurationImpl getBuilderConfiguration(KieBaseModelImpl kBaseModel, KieProject kieProject, AbstractKieModule kModule) {
        KnowledgeBuilderConfigurationImpl pconf = new KnowledgeBuilderConfigurationImpl(kieProject.getClonedClassLoader());
        pconf.setCompilationCache(kModule.getCompilationCache(kBaseModel.getName()));
        setModelPropsOnConf( kBaseModel, pconf );
        return pconf;
    }

    private static void setModelPropsOnConf( KieBaseModelImpl kBaseModel, KnowledgeBuilderConfigurationImpl pconf ) {
        KieModuleModel kModuleModel = kBaseModel.getKModule();
        for (Map.Entry<String, String> entry : kModuleModel.getConfigurationProperties().entrySet()) {
            pconf.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public KnowledgeBuilderConfiguration getBuilderConfiguration(KieBaseModel kBaseModel) {
        KnowledgeBuilderConfigurationImpl pconf = new KnowledgeBuilderConfigurationImpl();
        setModelPropsOnConf( (KieBaseModelImpl) kBaseModel, pconf );
        return pconf;
    }

    private static void addFiles(Set<Asset> assets,
                                 KieBaseModel kieBaseModel,
                                 InternalKieModule kieModule) {
        for (String fileName : kieModule.getFileNames()) {
            if (!fileName.startsWith(".") && !fileName.endsWith(".properties") && filterFileInKBase(kieModule, kieBaseModel, fileName)) {
                assets.add(new Asset( kieModule, fileName ));
            }
        }
    }

    public final boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName) {
        return addResourceToCompiler(ckbuilder, kieBaseModel, fileName, null);
    }

    public final boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs) {
        ResourceConfiguration conf = getResourceConfiguration(fileName);
        Resource resource = getResource(fileName);
        if (resource != null) {
            ResourceType resourceType = conf instanceof ResourceConfigurationImpl && ((ResourceConfigurationImpl)conf).getResourceType() != null ?
                                        ((ResourceConfigurationImpl)conf).getResourceType() :
                                        ResourceType.determineResourceType(fileName);

            if (resourceType == ResourceType.DTABLE && conf instanceof DecisionTableConfiguration) {
                for (RuleTemplateModel template : kieBaseModel.getRuleTemplates()) {
                    if (template.getDtable().equals( fileName )) {
                        Resource templateResource = getResource( template.getTemplate() );
                        if ( templateResource != null ) {
                            ( (DecisionTableConfiguration) conf ).addRuleTemplateConfiguration( templateResource, template.getRow(), template.getCol() );
                        }
                    }
                }
            }

            if (conf == null) {
                ckbuilder.add(resource, resourceType, rcs);
            } else {
                ckbuilder.add(resource, resourceType, conf, rcs);
            }
            return true;
        }
        return false;
    }

    public boolean hasResource(String fileName) {
        byte[] bytes = getBytes(fileName);
        return bytes != null && bytes.length > 0;
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
            int lastDot = fileName.lastIndexOf( '.' );
            if (lastDot >= 0 && fileName.length() > lastDot+1) {
                String extension = fileName.substring( lastDot+1 );
                Properties prop = new Properties();
                prop.setProperty(ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS, DecisionTableConfigurationImpl.class.getName());
                prop.setProperty(DecisionTableConfigurationImpl.DROOLS_DT_TYPE, DecisionTableInputType.valueOf( extension.toUpperCase() ).toString());
                conf = ResourceTypeImpl.fromProperties(prop);
            }
        }
        resourceConfigurationCache.put(fileName, conf);
        return conf;
    }

    protected CompilationCache getCompilationCache(String kbaseName) {
        // Map< DIALECT, Map< RESOURCE, List<BYTECODE> > >
        CompilationCache cache = compilationCache.get(kbaseName);
        if (cache == null) {
            byte[] fileContents = getBytes(KieBuilderImpl.getCompilationCachePath(releaseId, kbaseName));
            if (fileContents != null) {
                ExtensionRegistry registry = KieModuleCacheHelper.buildRegistry();
                try {
                    Header _header = KieModuleCacheHelper.readFromStreamWithHeaderPreloaded(new ByteArrayInputStream(fileContents), registry);

                    if (!Drools.isCompatible(_header.getVersion().getVersionMajor(),
                                             _header.getVersion().getVersionMinor(),
                                             _header.getVersion().getVersionRevision())) {
                        // if cache has been built with an incompatible version avoid to use it
                        log.warn("The compilation cache has been built with an incompatible version. " +
                                 "You should recompile your project in order to use it with current release.");
                        return null;
                    }

                    KModuleCache _cache = KModuleCache.parseFrom(_header.getPayload());

                    cache = new CompilationCache();
                    for (CompilationData _data : _cache.getCompilationDataList()) {
                        for (CompDataEntry _entry : _data.getEntryList()) {
                            cache.addEntry(_data.getDialect(), _entry.getId(),  _entry.getData().toByteArray());
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

    public InputStream getPomAsStream() {
        byte[] pom = getBytes(((ReleaseIdImpl)releaseId).getPomXmlPath());
        return pom != null ? new ByteArrayInputStream(pom) : null;
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
    
    public static class CompilationCache implements Serializable {
        private static final long serialVersionUID = 3812243055974412935L;
        // this is a { DIALECT -> ( RESOURCE, List<CompilationEntry> ) } cache
        protected final Map<String, Map<String, List<CompilationCacheEntry>>> compilationCache = new HashMap<String, Map<String, List<CompilationCacheEntry>>>();

        public void addEntry(String dialect, String className, byte[] bytecode) {
            Map<String, List<CompilationCacheEntry>> resourceEntries = compilationCache.get(dialect);
            if( resourceEntries == null ) {
                resourceEntries = new HashMap<String, List<CompilationCacheEntry>>();
                compilationCache.put(dialect, resourceEntries);
            }
                    
            String key = className.contains("$") ? className.substring(0, className.indexOf('$') ) + ".class" : className; 
            List<CompilationCacheEntry> bytes = resourceEntries.get(key);
            if( bytes == null ) {
                bytes = new ArrayList<CompilationCacheEntry>();
                resourceEntries.put(key, bytes);
            }
            //System.out.println(String.format("Adding to in-memory cache: %s %s", key, className ));
            bytes.add(new CompilationCacheEntry(className, bytecode));
        }

        public Map<String, List<CompilationCacheEntry>> getCacheForDialect(String dialect) {
            return compilationCache.get(dialect);
        }
        
    }
    
    public static class CompilationCacheEntry implements Serializable {
        private static final long serialVersionUID = 1423987159014688588L;
        public final String className;
        public final byte[] bytecode;
        
        public CompilationCacheEntry( String className, byte[] bytecode) {
            this.className = className;
            this.bytecode = bytecode;
        }
    }

    @Override
    public ResourceProvider createResourceProvider() {
        try {
            return new KieModuleResourceProvider(this, getFile().toURI().toURL());
        } catch (Exception e) {
            return null;
        }
    }

    private static class KieModuleResourceProvider implements ResourceProvider {

        private final InternalKieModule kieModule;
        private final URL kieModuleUrl;

        private KieModuleResourceProvider(InternalKieModule kieModule, URL kieModuleUrl) {
            this.kieModule = kieModule;
            this.kieModuleUrl = kieModuleUrl;
        }

        @Override
        public InputStream getResourceAsStream(String name) throws IOException {
            if (name.endsWith( "/" )) {
                name = name.substring( 0, name.length()-1 );
            }
            Resource resource = kieModule.getResource(name);
            return resource != null ? resource.getInputStream() : null;
        }

        @Override
        public URL getResource(String name) {
            if (name.endsWith( "/" )) {
                name = name.substring( 0, name.length()-1 );
            }
            return kieModule.hasResource(name) ? createURLForResource(name) : null;
        }

        private URL createURLForResource(String name) {
            try {
                if (kieModule instanceof ZipKieModule) {
                    return new URL("jar", "", kieModuleUrl + "!/" + name);
                } else {
                    return new URL(kieModuleUrl, name);
                }
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }
}
