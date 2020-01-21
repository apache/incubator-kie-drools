/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.assembler.executor;

import java.util.Collection;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.assembler.converter.KiePMMLConverter;
import org.kie.pmml.compiler.executor.PMMLCompilerExecutor;
import org.kie.pmml.compiler.executor.PMMLCompilerExecutorImpl;
import org.kie.pmml.compiler.implementations.ModelImplementationProviderFinderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PMMLAssemblerExecutor default implementation
 */
public class PMMLAssemblerExecutorImpl implements KieAssemblerService {

    private static final Logger logger = LoggerFactory.getLogger(PMMLAssemblerExecutorImpl.class );

    public static final String PMML_COMPILER_CACHE_KEY = "PMML_COMPILER_CACHE_KEY";

    private PMMLCompilerExecutor pmmlCompilerExecutor;
    private KiePMMLConverter kiePMMLConverter;

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    @Override
    public void addResources(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        // TODO {gcardosi}
//        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
//        Collection<KiePMMLModel> kiePMMLModels = new ArrayList<>();
//        if (kbuilderImpl.getKnowledgeBase() != null) {
//            for (InternalKnowledgePackage pr : kbuilderImpl.getKnowledgeBase().getPackagesMap().values()) {
//                ResourceTypePackage resourceTypePackage = pr.getResourceTypePackages().get(ResourceType.PMML);
//                if (resourceTypePackage != null) {
//                    PMMLPackage dmnpkg = (PMMLPackage) resourceTypePackage;
//                    kiePMMLModels.addAll(dmnpkg.getAllModels().values());
//                }
//            }
//        }
//        for (PackageRegistry pr : kbuilderImpl.getPackageRegistry().values()) {
//            ResourceTypePackage resourceTypePackage = pr.getPackage().getResourceTypePackages().get(ResourceType.PMML);
//            if (resourceTypePackage != null) {
//                PMMLPackage dmnpkg = (PMMLPackage) resourceTypePackage;
//                kiePMMLModels.addAll(dmnpkg.getAllModels().values());
//            }
//        }
    }

    @Override
    public void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        // TODO {gcardosi}
//        logger.warn("invoked legacy addResource (no control on the order of the assembler compilation): " + resource.getSourcePath());
//        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
//        PMMLCompilerExecutor pmmlCompiler = kbuilderImpl.getCachedOrCreate( PMML_COMPILER_CACHE_KEY, () -> getCompiler( kbuilderImpl ) );
//
//        Collection<KiePMMLModel> kiePMMLModels = new ArrayList<>();
//        for (PackageRegistry pr : kbuilderImpl.getPackageRegistry().values()) {
//            ResourceTypePackage resourceTypePackage = pr.getPackage().getResourceTypePackages().get(ResourceType.PMML);
//            if (resourceTypePackage != null) {
//                PMMLPackage dmnpkg = (PMMLPackage) resourceTypePackage;
//                kiePMMLModels.addAll(dmnpkg.getAllModels().values());
//            }
//        }
//
//        compileResourceToModel(kbuilderImpl, pmmlCompiler, resource, kiePMMLModels);
    }

    private KiePMMLModel compileResourceToModel(KnowledgeBuilderImpl kbuilderImpl, PMMLCompilerExecutor pmmlCompiler, Resource resource, Collection<KiePMMLModel> KiePMMLModels) throws Exception {
//        KiePMMLModel model = pmmlCompiler.getResults(resource.getSourcePath());
//        if( model != null ) {
//            String namespace = model.getNamespace();
//
//            PackageRegistry pkgReg = kbuilderImpl.getOrCreatePackageRegistry( new PackageDescr(namespace ) );
//            InternalKnowledgePackage kpkgs = pkgReg.getPackage();
//            kpkgs.addCloningResource( PMML_COMPILER_CACHE_KEY, pmmlCompiler );
//
//            ResourceTypePackageRegistry rpkg = kpkgs.getResourceTypePackages();
//
//            PMMLPackageImpl dmnpkg = rpkg.computeIfAbsent(ResourceType.PMML, rtp -> new PMMLPackageImpl(namespace));
//            if ( dmnpkg.getModel( model.getName() ) != null ) {
//                kbuilderImpl.addBuilderResult(new PMMLKnowledgeBuilderError(ResultSeverity.ERROR, resource, namespace, "Duplicate model name " + model.getName() + " in namespace " + namespace));
//                logger.error( "Duplicate model name {} in namespace {}", model.getName(), namespace );
//            }
//            dmnpkg.addModel( model.getName(), model );
//            for (PMMLMessage m : model.getMessages()) {
//                kbuilderImpl.addBuilderResult(PMMLKnowledgeBuilderError.from(resource, namespace, m));
//            }
//            dmnpkg.addProfiles(kbuilderImpl.getCachedOrCreate(PMML_PROFILES_CACHE_KEY, () -> getPMMLProfiles(kbuilderImpl)));
//        } else {
//            kbuilderImpl.addBuilderResult(new PMMLKnowledgeBuilderError(ResultSeverity.ERROR, resource, "Unable to compile PMML model for the resource"));
//            logger.error( "Unable to compile PMML model for resource {}", resource.getSourcePath() );
//        }
        return new KiePMMLModel("NAME", PMML_MODEL.REGRESSION_MODEL) {
        };
    }

    private PMMLCompilerExecutor getCompiler(KnowledgeBuilderImpl kbuilderImpl) {
        return new PMMLCompilerExecutorImpl(new ModelImplementationProviderFinderImpl());
    }

//        @Override
//    public List<KnowledgeBuilderResult> getResults(Resource resource) throws Exception {
//        // TODO {gcardosi} read actual Resource content
//        return getResults(resource.toString());
//    }
//
//    private List<KnowledgeBuilderResult> getResults(String resource) throws Exception {
//        return pmmlCompilerExecutor
//                .getResults(resource)
//                .stream()
//                .map(kiePMMLModel -> kiePMMLConverter.getFromKiePMMLModel(kiePMMLModel))
//                .collect(Collectors.toList());
//    }
}
