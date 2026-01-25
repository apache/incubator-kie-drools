/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.efesto.compiler.service;

import java.util.Collection;
import java.util.List;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.efesto.compiler.model.DMNResourceSetResource;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.getDMNModelsFromResources;

@SuppressWarnings("rawtypes")
public class KieCompilerServiceDMNResourceSet extends AbstractKieCompilerServiceDMN {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieCompilerServiceDMNResourceSet.class);

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof DMNResourceSetResource dmnFileSystemResourceSetResource &&
                dmnFileSystemResourceSetResource.getModelLocalUriId().model().equalsIgnoreCase("dmn");
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }
        if (!(context instanceof DmnCompilationContext dmnContext)) {
            throw new KieCompilerServiceException(String.format("Wrong %s context parameter for from %s",
                                                                context.getClass().getName(),
                                                                this.getClass().getName()));
        }
        DMNResourceSetResource dmnResourceSetResource = (DMNResourceSetResource) toProcess;
        Collection<Resource> dmnResources = dmnResourceSetResource.getContent();
        List<DMNModel> dmnModels = getDMNModelsFromResources(dmnResources, dmnContext.getCustomDMNProfiles(),
                                                             dmnContext.getRuntimeTypeCheckOption(),
                                                             dmnContext.getContextClassloader());
        List<ModelSourceTuple> modelSourceTuples = dmnModels.stream()
                .map(this::readDMNModel)
                .toList();
        return getListEfestoCompilationOutput(modelSourceTuples, dmnContext, LOGGER);
    }
}
