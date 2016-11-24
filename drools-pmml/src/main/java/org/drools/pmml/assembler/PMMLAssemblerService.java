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

package org.drools.pmml.assembler;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DroolsParserException;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.pmml.pmml_4_2.PMML4Compiler;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.assembler.KieAssemblerService;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.io.IOException;

public class PMMLAssemblerService implements KieAssemblerService {

    public PMMLAssemblerService() {
    }


    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    public PackageDescr pmmlModelToPackageDescr(KnowledgeBuilderImpl kbuilder,
                                                PMML4Compiler compiler,
                                                Resource resource) throws DroolsParserException,
                                                                          IOException {
        String theory = compiler.compile(resource.getInputStream(),
                                         kbuilder.getRootClassLoader());

        if (!compiler.getResults().isEmpty()) {
            for (KnowledgeBuilderResult result : compiler.getResults() ) {
                kbuilder.addBuilderResult(result);
            }
            return null;
        }

        return kbuilder.generatedDrlToPackageDescr( resource, theory );
    }

    @Override
    public void addResource(KnowledgeBuilder kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kbuilderImpl = ((KnowledgeBuilderImpl)kbuilder);

        PMML4Compiler compiler = new PMML4Compiler();
        if (compiler.getResults().isEmpty()) {
            PackageDescr descr = pmmlModelToPackageDescr(kbuilderImpl, compiler, resource);
            if (descr != null) {
                kbuilderImpl.addPackage(descr);
            }
        } else {
            for (KnowledgeBuilderResult result : compiler.getResults() ) {
                kbuilderImpl.addBuilderResult(result);
            }
        }
        compiler.clearResults();
    }

    @Override
    public Class getServiceInterface() {
        return KieAssemblerService.class;
    }
}
