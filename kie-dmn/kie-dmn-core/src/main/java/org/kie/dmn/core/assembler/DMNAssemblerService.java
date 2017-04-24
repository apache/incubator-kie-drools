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

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.impl.DMNKnowledgeBuilderError;
import org.kie.dmn.core.impl.DMNPackageImpl;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.io.ResourceTypePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DMNAssemblerService implements KieAssemblerService {

    private static final Logger logger = LoggerFactory.getLogger( DMNAssemblerService.class );
    public static final String DMN_EXTENSION_REGISTER_PREFIX = "org.kie.dmn.marshaller.extension.";

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DMN;
    }

    @Override
    public void addResource(KnowledgeBuilder kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration)
            throws Exception {
        DMNCompiler dmnCompiler = getCompiler(kbuilder);

        DMNModel model = dmnCompiler.compile(resource);
        if( model != null ) {
            String namespace = model.getNamespace();

            KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
            PackageRegistry pkgReg = kbuilderImpl.getPackageRegistry( namespace );
            if ( pkgReg == null ) {
                pkgReg = kbuilderImpl.newPackage( new PackageDescr( namespace ) );
            }
            InternalKnowledgePackage kpkgs = pkgReg.getPackage();

            Map<ResourceType, ResourceTypePackage> rpkg = kpkgs.getResourceTypePackages();

            DMNPackageImpl dmnpkg = (DMNPackageImpl) rpkg.get( ResourceType.DMN );
            if ( dmnpkg == null ) {
                dmnpkg = new DMNPackageImpl( namespace );
                rpkg.put(ResourceType.DMN, dmnpkg);
            } else {
                if ( dmnpkg.getModel( model.getName() ) != null ) {
                    ((KnowledgeBuilderImpl) kbuilder).addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.ERROR, resource, namespace, "Duplicate model name " + model.getName() + " in namespace " + namespace));
                    logger.error( "Duplicate model name {} in namespace {}", model.getName(), namespace );
                }
            }
            dmnpkg.addModel( model.getName(), model );
        } else {
            ((KnowledgeBuilderImpl) kbuilder).addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.ERROR, resource, "Unable to compile DMN model for the resource"));
            logger.error( "Unable to compile DMN model for resource {}", resource.getSourcePath() );
        }
    }

    private DMNCompiler getCompiler(KnowledgeBuilder kbuilder) {
        // TODO currently because KieAssembler is a singleton it is not possible to cache the compiler inside this KieAssembler
        DMNCompiler dmnCompiler;
        
        Map<String, String> extensionProperties = new HashMap<>();
        ( (KnowledgeBuilderImpl) kbuilder ).getBuilderConfiguration().getChainedProperties()
                .mapStartsWith(extensionProperties, DMN_EXTENSION_REGISTER_PREFIX, false);
        if( !extensionProperties.isEmpty() ) {
            List<DMNExtensionRegister> extensionRegisters = new ArrayList<>();
            try {
                for (Map.Entry<String, String> extensionProperty : extensionProperties.entrySet()) {
                    String extRegClassName = extensionProperty.getValue();
                    DMNExtensionRegister extRegister = (DMNExtensionRegister) ((KnowledgeBuilderImpl) kbuilder).getRootClassLoader()
                            .loadClass(extRegClassName).newInstance();
                    extensionRegisters.add(extRegister);
                }
                DMNCompilerConfiguration compilerConfig = DMNFactory.newCompilerConfiguration();
                compilerConfig.addExtensions(extensionRegisters);
                dmnCompiler = DMNFactory.newCompiler(compilerConfig);
            } catch(Exception e) {
                ((KnowledgeBuilderImpl) kbuilder).addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.WARNING, "Trying to load a non-existing extension element register "+e.getLocalizedMessage()));
                logger.error( "Trying to load a non-existing extension element register {}", e.getLocalizedMessage(), e);
                ((KnowledgeBuilderImpl) kbuilder).addBuilderResult(new DMNKnowledgeBuilderError(ResultSeverity.WARNING, "DMN Compiler configuration contained errors, fall-back using empty-configuration compiler."));
                logger.warn( "DMN Compiler configuration contained errors, fall-back using empty-configuration compiler." );
                dmnCompiler = DMNFactory.newCompiler();
            }
        } else {
            dmnCompiler = DMNFactory.newCompiler();
        }
        return dmnCompiler;
    }

    @Override
    public Class getServiceInterface() {
        return KieAssemblerService.class;
    }
}
