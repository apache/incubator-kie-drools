/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.assembler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.core.impl.DMNKnowledgeBuilderError;
import org.kie.dmn.core.impl.DMNPackageImpl;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.utils.ChainedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNAssemblerService implements KieAssemblerService {


    private static final Logger logger = LoggerFactory.getLogger( DMNAssemblerService.class );
    public static final String ORG_KIE_DMN_PREFIX = "org.kie.dmn";
    public static final String DMN_PROFILE_PREFIX = ORG_KIE_DMN_PREFIX + ".profiles.";
    public static final String DMN_COMPILER_CACHE_KEY = "DMN_COMPILER_CACHE_KEY";
    public static final String DMN_PROFILES_CACHE_KEY = "DMN_PROFILES_CACHE_KEY";

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DMN;
    }

    @Override
    public void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration)
            throws Exception {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        DMNCompiler dmnCompiler = kbuilderImpl.getCachedOrCreate( DMN_COMPILER_CACHE_KEY, () -> getCompiler( kbuilderImpl ) );

        DMNModel model = dmnCompiler.compile(resource);
        if( model != null ) {
            String namespace = model.getNamespace();

            PackageRegistry pkgReg = kbuilderImpl.getOrCreatePackageRegistry( new PackageDescr( namespace ) );
            InternalKnowledgePackage kpkgs = pkgReg.getPackage();
            kpkgs.addCloningResource( DMN_COMPILER_CACHE_KEY, dmnCompiler );

            Map<ResourceType, ResourceTypePackage> rpkg = kpkgs.getResourceTypePackages();

            DMNPackageImpl dmnpkg = (DMNPackageImpl) rpkg.get( ResourceType.DMN );
            if ( dmnpkg == null ) {
                dmnpkg = new DMNPackageImpl( namespace );
                rpkg.put(ResourceType.DMN, dmnpkg);
            } else {
                if ( dmnpkg.getModel( model.getName() ) != null ) {
                    kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.ERROR, resource, namespace, "Duplicate model name " + model.getName() + " in namespace " + namespace));
                    logger.error( "Duplicate model name {} in namespace {}", model.getName(), namespace );
                }
            }
            dmnpkg.addModel( model.getName(), model );
            dmnpkg.addProfiles(kbuilderImpl.getCachedOrCreate(DMN_PROFILES_CACHE_KEY, () -> getDMNProfiles(kbuilderImpl)));
        } else {
            kbuilderImpl.addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.ERROR, resource, "Unable to compile DMN model for the resource"));
            logger.error( "Unable to compile DMN model for resource {}", resource.getSourcePath() );
        }
    }

    private List<DMNProfile> getDMNProfiles(KnowledgeBuilderImpl kbuilderImpl) {
        Map<String, String> dmnProfileProperties = new HashMap<>();
        kbuilderImpl.getBuilderConfiguration().getChainedProperties().mapStartsWith(dmnProfileProperties, DMN_PROFILE_PREFIX, false);
        List<DMNProfile> dmnProfiles = new ArrayList<>();
        if (!isStrictMode(kbuilderImpl.getBuilderConfiguration().getChainedProperties())) {
            dmnProfiles.add(new ExtendedDMNProfile());
        }
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

    private static boolean isStrictMode(ChainedProperties properties) {
        String val = System.getProperty("org.kie.dmn.strictConformance");
        if (val == null) {
            return Boolean.parseBoolean(properties.getProperty("org.kie.dmn.strictConformance", "false"));
        }
        return "".equals(val) || Boolean.parseBoolean(val);
    }

    private DMNCompiler getCompiler(KnowledgeBuilderImpl kbuilderImpl) {
        List<DMNProfile> dmnProfiles = kbuilderImpl.getCachedOrCreate(DMN_PROFILES_CACHE_KEY, () -> getDMNProfiles(kbuilderImpl));

        DMNCompilerConfiguration compilerConfig = compilerConfigWithKModulePrefs(kbuilderImpl, dmnProfiles);

        return DMNFactory.newCompiler(compilerConfig);
    }

    private DMNCompilerConfiguration compilerConfigWithKModulePrefs(KnowledgeBuilderImpl kbuilderImpl, List<DMNProfile> dmnProfiles) {
        DMNCompilerConfigurationImpl config = (DMNCompilerConfigurationImpl) DMNFactory.newCompilerConfiguration();
        
        Map<String, String> dmnPrefs = new HashMap<>();
        kbuilderImpl.getBuilderConfiguration().getChainedProperties().mapStartsWith(dmnPrefs, ORG_KIE_DMN_PREFIX, true);
        config.setProperties(dmnPrefs);
        
        if (!dmnProfiles.isEmpty()) {
            for (DMNProfile dmnProfile : dmnProfiles) {
                config.addExtensions(dmnProfile.getExtensionRegisters());
                config.addDRGElementCompilers(dmnProfile.getDRGElementCompilers());
                config.addFEELProfile(dmnProfile);
            }
        }

        return config;
    }
}
