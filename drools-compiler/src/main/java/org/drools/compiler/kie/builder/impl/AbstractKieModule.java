/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.kie.builder.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

import org.drools.base.RuleBase;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.io.ResourceConfigurationImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.util.StringUtils;
import org.drools.wiring.api.ResourceProvider;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.RuleTemplateModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.ResourceChangeSet;
import org.kie.internal.io.ResourceTypeImpl;
import org.kie.util.maven.support.DependencyFilter;
import org.kie.util.maven.support.PomModel;
import org.kie.util.maven.support.ReleaseIdImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.internal.builder.KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration;

public abstract class AbstractKieModule implements InternalKieModule, Serializable {

    private static final String SPRING_BOOT_PREFIX = "BOOT-INF/classes/";

    private static final Logger log = LoggerFactory.getLogger(AbstractKieModule.class);

    private final transient Map<String, KnowledgeBuilder> kBuilders = new HashMap<>();

    private final transient Map<String, Results> resultsCache = new HashMap<>();

    protected ReleaseId releaseId;

    private transient KieModuleModel kModuleModel;

    private Map<ReleaseId, InternalKieModule> kieDependencies;

    // Map< KBaseName, CompilationCache>
    protected Map<String, CompilationCache> compilationCache = new HashMap<>();

    private final transient Map<String, ResourceConfiguration> resourceConfigurationCache = new HashMap<>();

    protected transient PomModel pomModel;

    private Collection<ReleaseId> unresolvedDependencies;

    public AbstractKieModule() { }

    public AbstractKieModule(ReleaseId releaseId, KieModuleModel kModuleModel) {
        this.releaseId = releaseId;
        this.kModuleModel = kModuleModel;
    }

    public KieModuleModel getKieModuleModel() {
        return this.kModuleModel;
    }

    public Map<ReleaseId, InternalKieModule> getKieDependencies() {
        return kieDependencies == null ? Collections.emptyMap() : kieDependencies;
    }

    public void addKieDependency(InternalKieModule dependency) {
        if (kieDependencies == null) {
            kieDependencies = new HashMap<>();
        }
        kieDependencies.put(dependency.getReleaseId(), dependency);
    }

    public Collection<ReleaseId> getJarDependencies(DependencyFilter filter ) {
        if( pomModel == null ) {
            getPomModel();
        }
        Collection<ReleaseId> deps = null;
        if( pomModel != null ) {
            deps = pomModel.getDependencies(filter);
        }
        return deps == null ? Collections.emptyList() : deps;
    }

    public Collection<ReleaseId> getUnresolvedDependencies() {
        return unresolvedDependencies == null ? Collections.emptyList() : unresolvedDependencies;
    }

    public void setUnresolvedDependencies(Collection<ReleaseId> unresolvedDependencies) {
        this.unresolvedDependencies = unresolvedDependencies;
    }

    public ReleaseId getReleaseId() {
        return releaseId;
    }

    @Override
    public ClassLoader getModuleClassLoader() {
        return kBuilders.isEmpty() ? null : (( InternalKnowledgeBuilder ) kBuilders.values().iterator().next()).getRootClassLoader();
    }

    public KnowledgeBuilder getKnowledgeBuilderForKieBase( String kieBaseName) {
        return kBuilders.get(kieBaseName);
    }

    public InternalKnowledgePackage getPackage(String packageName) {
        for (KnowledgeBuilder kbuilder : kBuilders.values()) {
            InternalKnowledgePackage pkg = (( InternalKnowledgeBuilder ) kbuilder).getPackage( packageName );
            if (pkg != null) {
                return pkg;
            }
        }
        return null;
    }

    @Override
    public Collection<KiePackage> getKnowledgePackagesForKieBase(String kieBaseName) {
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
        Map<String, byte[]> classes = new HashMap<>();
        for (String fileName : getFileNames()) {
            if (fileName.endsWith(".class")) {
                classes.put(fileName, getBytes(fileName));
            }
        }
        return classes;
    }

    public KnowledgePackagesBuildResult buildKnowledgePackages(KieBaseModelImpl kBaseModel, KieProject kieProject, BuildContext buildContext) {
        Collection<KiePackage> pkgs = getKnowledgePackagesForKieBase(kBaseModel.getName());

        if ( pkgs == null ) {
            KnowledgeBuilder kbuilder = kieProject.buildKnowledgePackages(kBaseModel, buildContext);
            if ( kbuilder.hasErrors() ) {
                // Messages already populated by the buildKnowlegePackages
                return new KnowledgePackagesBuildResult(true, null);
            }
            pkgs = kbuilder.getKnowledgePackages();
        }

        return new KnowledgePackagesBuildResult(false, pkgs);
    }

    public InternalKnowledgeBase createKieBase(KieBaseModelImpl kBaseModel, KieProject kieProject, BuildContext buildContext, KieBaseConfiguration conf) {
        KnowledgePackagesBuildResult knowledgePackagesBuildResult = buildKnowledgePackages(kBaseModel, kieProject, buildContext);
        if(knowledgePackagesBuildResult.hasErrors()) {
            return null;
        }

        Collection<KiePackage> pkgs = knowledgePackagesBuildResult.getPkgs();
        checkStreamMode( kBaseModel, conf, pkgs );

        ClassLoader cl = kieProject.getClassLoader();
        if (conf == null) {
            conf = getKnowledgeBaseConfiguration(kBaseModel, cl);
        } else if (conf instanceof RuleBaseConfiguration ) {
            ((RuleBaseConfiguration)conf).setClassLoader(cl);
        }

        RuleBase kBase = RuleBaseFactory.newRuleBase(kBaseModel.getName(), conf);
        kBase.addPackages( pkgs );
        return KnowledgeBaseFactory.newKnowledgeBase(kBase);
    }

    public static void checkStreamMode( KieBaseModel kBaseModel, KieBaseConfiguration conf, Collection<? extends KiePackage> pkgs ) {
        if ( kBaseModel.getEventProcessingMode() == EventProcessingOption.CLOUD &&
             (conf == null || conf.getOption(EventProcessingOption.KEY) == EventProcessingOption.CLOUD ) ) {
            for (KiePackage kpkg : pkgs) {
                if ( ((KnowledgePackageImpl ) kpkg).needsStreamMode() ) {
                    throw new RuntimeException( "The requested KieBase \"" + kBaseModel.getName() + "\" has been set to run in CLOUD mode but requires features only available in STREAM mode" );
                }
            }
        }
    }

    private KieBaseConfiguration getKnowledgeBaseConfiguration(KieBaseModelImpl kBaseModel, ClassLoader cl) {
        KieBaseConfiguration kbConf = RuleBaseFactory.newKnowledgeBaseConfiguration(null, cl);
        kbConf.setOption(kBaseModel.getEqualsBehavior());
        kbConf.setOption(kBaseModel.getEventProcessingMode());
        kbConf.setOption(kBaseModel.getDeclarativeAgenda());
        kbConf.setOption(kBaseModel.getSequential());
        kbConf.setOption(kBaseModel.getSessionsPool());
        kbConf.setOption(kBaseModel.getMutability());
        return kbConf;
    }

    public KnowledgeBuilderConfiguration createBuilderConfiguration( KieBaseModel kBaseModel, ClassLoader classLoader) {
        KnowledgeBuilderConfigurationImpl pconf = newKnowledgeBuilderConfiguration(classLoader).as(KnowledgeBuilderConfigurationImpl.KEY);
        pconf.setCompilationCache(getCompilationCache(kBaseModel.getName()));
        setModelPropsOnConf( kBaseModel, pconf );
        return pconf;
    }

    private static void setModelPropsOnConf( KieBaseModel kBaseModel, KnowledgeBuilderConfigurationImpl pconf ) {
        if (kBaseModel.getPrototypes() != null) {
            pconf.setProperty(PrototypesOption.PROPERTY_NAME, kBaseModel.getPrototypes().toString());
        }
        KieModuleModel kModuleModel = ((KieBaseModelImpl) kBaseModel).getKModule();
        for (Map.Entry<String, String> entry : kModuleModel.getConfigurationProperties().entrySet()) {
            pconf.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public final boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName) {
        return addResourceToCompiler(ckbuilder, kieBaseModel, fileName, null);
    }

    public final boolean addResourceToCompiler(CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, ResourceChangeSet rcs) {
        Resource resource = getResource(fileName);
        if (resource != null) {
            ResourceConfiguration conf = getResourceConfiguration(fileName);
            ResourceType resourceType = conf instanceof ResourceConfigurationImpl && ((ResourceConfigurationImpl)conf).getResourceType() != null ?
                                        ((ResourceConfigurationImpl)conf).getResourceType() :
                                        ResourceType.determineResourceType(fileName);

            if (resourceType == ResourceType.DTABLE && conf instanceof DecisionTableConfiguration) {
                addDTableToCompiler( ckbuilder, kieBaseModel, fileName, resource, rcs, ( DecisionTableConfiguration ) conf );
            } else {
                ckbuilder.add(resource, resourceType, conf, rcs);
            }
            return true;
        }
        return false;
    }

    private void addDTableToCompiler( CompositeKnowledgeBuilder ckbuilder, KieBaseModel kieBaseModel, String fileName, Resource resource, ResourceChangeSet rcs, DecisionTableConfiguration dtableConf ) {
        for (RuleTemplateModel template : kieBaseModel.getRuleTemplates()) {
            boolean isInSpringBoot = fileName.startsWith(SPRING_BOOT_PREFIX);
            if (isInSpringBoot) {
                fileName = fileName.substring(SPRING_BOOT_PREFIX.length());
            }
            if (template.getDtable().equals( fileName )) {
                Resource templateResource = getResource( (isInSpringBoot ? SPRING_BOOT_PREFIX : "") + template.getTemplate() );
                if ( templateResource != null ) {
                    dtableConf.addRuleTemplateConfiguration( templateResource, template.getRow(), template.getCol() );
                } else {
                    throw new RuntimeException( "Cannot find resource: '" + template.getTemplate() + "'" );
                }
            }
        }
        addDTableToCompiler( ckbuilder, resource, dtableConf, rcs );
    }

    public static void addDTableToCompiler( CompositeKnowledgeBuilder ckbuilder, Resource resource, DecisionTableConfiguration dtableConf ) {
        addDTableToCompiler( ckbuilder, resource, dtableConf, null );
    }

    private static void addDTableToCompiler( CompositeKnowledgeBuilder ckbuilder, Resource resource, DecisionTableConfiguration dtableConf, ResourceChangeSet rcs ) {
        String sheetNames = dtableConf.getWorksheetName();
        if (sheetNames == null || sheetNames.indexOf( ',' ) < 0) {
            ckbuilder.add( resource, ResourceType.DTABLE, dtableConf, rcs );
        } else {
            for (String sheetName : sheetNames.split( "\\," ) ) {
                ckbuilder.add( resource, ResourceType.DTABLE, new DecisionTableConfigurationDelegate( dtableConf, sheetName), rcs );
            }
        }
    }

    public boolean hasResource(String fileName) {
        byte[] bytes = getBytes(fileName);
        return bytes != null && bytes.length > 0;
    }

    public ResourceConfiguration getResourceConfiguration(String fileName) {
        return resourceConfigurationCache.computeIfAbsent(fileName, this::loadResourceConfiguration);
    }

    private ResourceConfiguration loadResourceConfiguration( String fileName ) {
        return loadResourceConfiguration( fileName, this::isAvailable, file -> new ByteArrayInputStream(getBytes( fileName + ".properties")) );
    }

    public static ResourceConfiguration loadResourceConfiguration( String fileName, Predicate<String> fileAvailable, Function<String, InputStream> fileProvider ) {
        ResourceConfiguration conf;
        Properties prop = new Properties();
        if ( fileAvailable.test( fileName + ".properties") ) {
            try ( InputStream input = fileProvider.apply( fileName + ".properties") ) {
                prop.load(input);
            } catch (IOException e) {
                log.error(String.format("Error loading resource configuration from file: %s.properties", fileName ));
            }
        }
        if (ResourceType.DTABLE.matchesExtension( fileName )) {
            int lastDot = fileName.lastIndexOf( '.' );
            if (lastDot >= 0 && fileName.length() > lastDot+1) {
                String extension = fileName.substring( lastDot+1 );
                Object confClass = prop.get(ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS);
                if (confClass == null || confClass.toString().equals( ResourceConfigurationImpl.class.getCanonicalName() )) {
                    prop.setProperty( ResourceTypeImpl.KIE_RESOURCE_CONF_CLASS, DecisionTableConfigurationImpl.class.getName() );
                }
                prop.setProperty(DecisionTableConfigurationImpl.DROOLS_DT_TYPE, DecisionTableInputType.valueOf( extension.toUpperCase() ).toString());
            }
        }
        conf = prop.isEmpty() ? null : ResourceTypeImpl.fromProperties(prop);
        if (conf instanceof DecisionTableConfiguration && (( DecisionTableConfiguration ) conf).getWorksheetName() == null) {
            (( DecisionTableConfiguration ) conf).setWorksheetName( prop.getProperty( "sheets" ) );
        }
        return conf;
    }

    @Override
    public CompilationCache getCompilationCache(String kbaseName) {
        return CompilationCacheProvider.get().getCompilationCache( this, compilationCache, kbaseName );
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
