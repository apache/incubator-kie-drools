/**
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
package org.kie.dmn.core.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNDecisionLogicCompilerFactory;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.ImportDMNResolverUtil;
import org.kie.dmn.core.compiler.ImportDMNResolverUtil.ImportType;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.core.impl.DMNKnowledgeBuilderError;
import org.kie.dmn.core.impl.DMNPackageImpl;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.utils.ChainedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNAssemblerService implements KieAssemblerService {


    private static final Logger logger = LoggerFactory.getLogger( DMNAssemblerService.class );
    public static final String ORG_KIE_DMN_PREFIX = "org.kie.dmn";
    public static final String DMN_PROFILE_PREFIX = ORG_KIE_DMN_PREFIX + ".profiles.";
    public static final String DMN_RUNTIME_LISTENER_PREFIX = ORG_KIE_DMN_PREFIX + ".runtime.listeners.";
    public static final String DMN_DECISION_LOGIC_COMPILER = ORG_KIE_DMN_PREFIX + ".decisionlogiccompilerfactory";
    public static final String DMN_COMPILER_CACHE_KEY = "DMN_COMPILER_CACHE_KEY";
    public static final String DMN_PROFILES_CACHE_KEY = "DMN_PROFILES_CACHE_KEY";

    private DMNCompilerConfigurationImpl externalCompilerConfiguration;

    public DMNAssemblerService(DMNCompilerConfigurationImpl externalCompilerConfiguration) {
        this.externalCompilerConfiguration = externalCompilerConfiguration;
    }

    public DMNAssemblerService() {
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DMN;
    }

    @Override
    public void addResourcesAfterRules(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        EvalHelper.clearGenericAccessorCache();
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        DMNCompilerImpl dmnCompiler = (DMNCompilerImpl) kbuilderImpl.getCachedOrCreate(DMN_COMPILER_CACHE_KEY, () -> getCompiler(kbuilderImpl));
        DMNMarshaller dmnMarshaller = dmnCompiler.getMarshaller();
        List<DMNResource> dmnResources = new ArrayList<>();
        for (ResourceWithConfiguration r : resources) {
            Definitions definitions = dmnMarshaller.unmarshal(r.getResource().getReader());
            DMNResource dmnResource = new DMNResource(definitions, r);
            dmnResources.add(dmnResource);
        }

        Collection<DMNModel> dmnModels = new ArrayList<>();
        // KIE API: KieContainer upgrade using KieContainer#updateToVersion -based DMN Import resolution strategy
        if (kbuilderImpl.getKnowledgeBase() != null) {
            for (InternalKnowledgePackage pr : kbuilderImpl.getKnowledgeBase().getPackagesMap().values()) {
                ResourceTypePackage resourceTypePackage = pr.getResourceTypePackages().get(ResourceType.DMN);
                if (resourceTypePackage != null) {
                    DMNPackageImpl dmnpkg = (DMNPackageImpl) resourceTypePackage;
                    dmnModels.addAll(dmnpkg.getAllModels().values());
                }
            }
        }
        // Workbench: InternalKieBuilder#createFileSet#build -based DMN Import resolution strategy
        for (PackageRegistry pr : kbuilderImpl.getPackageRegistry().values()) {
            ResourceTypePackage resourceTypePackage = pr.getPackage().getResourceTypePackages().get(ResourceType.DMN);
            if (resourceTypePackage != null) {
                DMNPackageImpl dmnpkg = (DMNPackageImpl) resourceTypePackage;
                dmnModels.addAll(dmnpkg.getAllModels().values());
            }
        }

        enrichDMNResourcesWithImportsDependencies(dmnResources, dmnModels);
        List<DMNResource> sortedDmnResources = DMNResourceDependenciesSorter.sort(dmnResources);

        for (DMNResource dmnRes : sortedDmnResources) {
            DMNModel dmnModel = internalAddResource(kbuilderImpl, dmnCompiler, dmnRes, dmnModels);
            dmnModels.add(dmnModel);
        }
    }

    public static void enrichDMNResourcesWithImportsDependencies(List<DMNResource> dmnResources, Collection<DMNModel> dmnModels) {
        for (DMNResource r : dmnResources) {
            for (Import i : r.getDefinitions().getImport()) {
                if (ImportDMNResolverUtil.whichImportType(i) == ImportType.DMN) {
                    Either<String, DMNModel> inAlreadyCompiled = ImportDMNResolverUtil.resolveImportDMN(i, dmnModels, x -> new QName(x.getNamespace(), x.getName()));
                    if (inAlreadyCompiled.isLeft()) { // the DMN Model is not already available in the KieBuilder and needs to be compiled.
                        Either<String, DMNResource> resolvedResult = ImportDMNResolverUtil.resolveImportDMN(i, dmnResources, DMNResource::getModelID);
                        DMNResource located = resolvedResult.getOrElseThrow(RuntimeException::new);
                        r.addDependency(located.getModelID());
                    } else {
                        // do nothing: the DMN Model is already available in the KieBuilder.
                    }
                }
            }
        }
    }

    private DMNModel internalAddResource(KnowledgeBuilderImpl kbuilder, DMNCompiler dmnCompiler, DMNResource dmnRes, Collection<DMNModel> dmnModels) throws Exception {
        ResourceWithConfiguration r = dmnRes.getResAndConfig();
        r.getBeforeAdd().accept(kbuilder);
        DMNModel dmnModel = compileResourceToModel(kbuilder, dmnCompiler, r.getResource(), dmnRes, dmnModels);
        r.getAfterAdd().accept(kbuilder);
        return dmnModel;
    }

    @Override
    public void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        logger.warn("invoked legacy addResourceAfterRules (no control on the order of the assembler compilation): {}", resource.getSourcePath());
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        DMNCompiler dmnCompiler = kbuilderImpl.getCachedOrCreate( DMN_COMPILER_CACHE_KEY, () -> getCompiler( kbuilderImpl ) );

        Collection<DMNModel> dmnModels = new ArrayList<>();
        for (PackageRegistry pr : kbuilderImpl.getPackageRegistry().values()) {
            ResourceTypePackage resourceTypePackage = pr.getPackage().getResourceTypePackages().get(ResourceType.DMN);
            if (resourceTypePackage != null) {
                DMNPackageImpl dmnpkg = (DMNPackageImpl) resourceTypePackage;
                dmnModels.addAll(dmnpkg.getAllModels().values());
            }
        }

        compileResourceToModel(kbuilderImpl, dmnCompiler, resource, null, dmnModels);
    }

    private DMNModel compileResourceToModel(KnowledgeBuilderImpl kbuilderImpl, DMNCompiler dmnCompiler, Resource resource, DMNResource dmnRes, Collection<DMNModel> dmnModels) {
        DMNModel model = dmnRes != null ?
                dmnCompiler.compile(dmnRes.getDefinitions(), resource, dmnModels) :
                dmnCompiler.compile(resource, dmnModels);
        if( model != null ) {
            String namespace = model.getNamespace();

            PackageRegistry pkgReg = kbuilderImpl.getOrCreatePackageRegistry( new PackageDescr( namespace ) );
            InternalKnowledgePackage kpkgs = pkgReg.getPackage();
            kpkgs.addCloningResource( DMN_COMPILER_CACHE_KEY, dmnCompiler );

            ResourceTypePackageRegistry rpkg = kpkgs.getResourceTypePackages();

            DMNPackageImpl dmnpkg = rpkg.computeIfAbsent(ResourceType.DMN, rtp -> new DMNPackageImpl(namespace));
            if ( dmnpkg.getModel( model.getName() ) != null ) {
                kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.ERROR, resource, namespace, "Duplicate model name " + model.getName() + " in namespace " + namespace));
                logger.error( "Duplicate model name {} in namespace {}", model.getName(), namespace );
            }
            dmnpkg.addModel( model.getName(), model );
            for (DMNMessage m : model.getMessages()) {
                kbuilderImpl.addBuilderResult(DMNKnowledgeBuilderError.from(resource, namespace, m));
            }
            dmnpkg.addProfiles(kbuilderImpl.getCachedOrCreate(DMN_PROFILES_CACHE_KEY, () -> getDMNProfiles(kbuilderImpl)));
        } else {
            kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.ERROR, resource, "Unable to compile DMN model for the resource"));
            logger.error("Unable to compile DMN model for the resource {}", resource.getSourcePath());
        }
        return model;
    }

    protected List<DMNProfile> getDMNProfiles(KnowledgeBuilderImpl kbuilderImpl) {
        ChainedProperties chainedProperties = kbuilderImpl.getBuilderConfiguration().getChainedProperties();

        List<DMNProfile> dmnProfiles = new ArrayList<>();
        dmnProfiles.addAll(getDefaultDMNProfiles(chainedProperties));

        Map<String, String> dmnProfileProperties = new HashMap<>();
        chainedProperties.mapStartsWith(dmnProfileProperties, DMN_PROFILE_PREFIX, false);
        if (!dmnProfileProperties.isEmpty()) {
            try {
                for (Map.Entry<String, String> dmnProfileProperty : dmnProfileProperties.entrySet()) {
                    DMNProfile dmnProfile = (DMNProfile) kbuilderImpl.getRootClassLoader()
                                                                     .loadClass(dmnProfileProperty.getValue()).newInstance();
                    dmnProfiles.add(dmnProfile);
                }
                return dmnProfiles;
            } catch (Exception e) {
                kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.WARNING, "Trying to load a non-existing Kie DMN profile " + e.getLocalizedMessage()));
                logger.error("Trying to load a non-existing Kie DMN profile {}", e.getLocalizedMessage(), e);
                kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.WARNING, "DMN Compiler configuration contained errors, will fall-back using empty-configuration compiler."));
                logger.warn("DMN Compiler configuration contained errors, will fall-back using empty-configuration compiler.");
            }
        }
        return dmnProfiles;
    }

    public static List<DMNProfile> getDefaultDMNProfiles(ChainedProperties properties) {
        if (!isStrictMode(properties)) {
            return List.of(new ExtendedDMNProfile());
        } else {
            return Collections.emptyList();
        }
    }

    public static boolean isStrictMode(ChainedProperties properties) {
        String val = properties.getProperty("org.kie.dmn.strictConformance", "false");
        return "".equals(val) || Boolean.parseBoolean(val);
    }

    private DMNCompiler getCompiler(KnowledgeBuilderImpl kbuilderImpl) {
        List<DMNProfile> dmnProfiles = kbuilderImpl.getCachedOrCreate(DMN_PROFILES_CACHE_KEY, () -> getDMNProfiles(kbuilderImpl));
        DMNCompilerConfigurationImpl compilerConfiguration;

        // Beware: compilerConfiguration can't be cached in DMNAssemblerService
        if (externalCompilerConfiguration == null) {
            compilerConfiguration = compilerConfigWithKModulePrefs(kbuilderImpl.getRootClassLoader(), kbuilderImpl.getBuilderConfiguration().getChainedProperties(), dmnProfiles, (DMNCompilerConfigurationImpl) DMNFactory.newCompilerConfiguration());
        } else {
            compilerConfiguration = externalCompilerConfiguration;
        }

        if (isStrictMode(kbuilderImpl.getBuilderConfiguration().getChainedProperties())) {
            compilerConfiguration.setProperty(RuntimeTypeCheckOption.PROPERTY_NAME, "true");
        }

        try {
            applyDecisionLogicCompilerFactory(kbuilderImpl.getRootClassLoader(), compilerConfiguration);
        } catch (Exception e) {
            kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.WARNING, "Trying to load a non-existing DMNDecisionLogicCompilerFactory " + e.getLocalizedMessage()));
            logger.error("Trying to load a non-existing DMNDecisionLogicCompilerFactory {}", e.getLocalizedMessage(), e);
            kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.WARNING, "DMN Compiler configuration contained errors, will fall-back to defaults."));
            logger.warn("DMN Compiler configuration contained errors, will fall-back to defaults.");
        }

        return DMNFactory.newCompiler(compilerConfiguration);
    }

    public static DMNCompilerConfigurationImpl applyDecisionLogicCompilerFactory(ClassLoader classLoader, DMNCompilerConfigurationImpl config) throws Exception {
        String definedDLCompiler = config.getProperties().get(DMN_DECISION_LOGIC_COMPILER);
        if (definedDLCompiler != null) {
            DMNDecisionLogicCompilerFactory factory = (DMNDecisionLogicCompilerFactory) classLoader.loadClass(definedDLCompiler).newInstance();
            config.setDecisionLogicCompilerFactory(factory);
        }
        return config;
    }

    /**
     * Returns a DMNCompilerConfiguration with the specified properties set, and applying the explicited dmnProfiles.
     * @param classLoader 
     * @param chainedProperties applies properties --it does not do any classloading nor profile loading based on these properites, just passes the values. 
     * @param dmnProfiles applies these DMNProfile(s) to the DMNCompilerConfiguration
     * @param config
     * @return
     */
    public static DMNCompilerConfigurationImpl compilerConfigWithKModulePrefs(ClassLoader classLoader, ChainedProperties chainedProperties, List<DMNProfile> dmnProfiles, DMNCompilerConfigurationImpl config) {

        config.setRootClassLoader(classLoader);

        Map<String, String> dmnPrefs = new HashMap<>();
        chainedProperties.mapStartsWith(dmnPrefs, ORG_KIE_DMN_PREFIX, true);
        config.setProperties(dmnPrefs);
        
        for (DMNProfile dmnProfile : dmnProfiles) {
            config.addExtensions(dmnProfile.getExtensionRegisters());
            config.addDRGElementCompilers(dmnProfile.getDRGElementCompilers());
            config.addFEELProfile(dmnProfile);
        }

        return config;
    }
}
