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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.io.ByteArrayResource;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.api.identifiers.LocalCompilationSourceIdDmn;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.slf4j.Logger;

import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.getCleanedFilenameForURI;

public abstract class AbstractKieCompilerServiceDMN implements KieCompilerService<EfestoCompilationOutput,
        EfestoCompilationContext> {

//    static final DMNValidator validator = DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()));

    @Override
    public boolean hasCompilationSource(String fileName) {
        LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(fileName);
        return ContextStorage.getEfestoCompilationSource(localCompilationSourceIdDmn) != null;
    }

    @Override
    public String getCompilationSource(String fileName) {
        LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(fileName);
        return ContextStorage.getEfestoCompilationSource(localCompilationSourceIdDmn);
    }

    @Override
    public String getModelType() {
        return "dmn";
    }

    protected DmnCompilationContext getDmnCompilationContext(EfestoCompilationContext context) {
        return context instanceof DmnCompilationContext dmnCompilationContext ? dmnCompilationContext :
                DmnCompilationContext.buildWithEfestoCompilationContext((EfestoCompilationContextImpl) context);
    }

    protected List<DMNMessage> validateDMN(EfestoCompilationContext context, Collection<Resource> resources) {
        DmnCompilationContext dmnCompilationContext = getDmnCompilationContext(context);
        if (dmnCompilationContext.getValidations() == null || dmnCompilationContext.getValidations().isEmpty()) {
            return Collections.emptyList();
        } else {
            return DMNValidatorFactory.newValidator(List.of(new ExtendedDMNProfile()))
                    .validateUsing(dmnCompilationContext.getValidations().toArray(new DMNValidator.Validation[]{}))
                    .theseModels(resources.toArray(new Resource[]{}));
        }
    }

    protected List<EfestoCompilationOutput> getListEfestoCompilationOutput(List<ModelSourceTuple> modelSourceTuples,
                                                                           DmnCompilationContext dmnContext,
                                                                           Logger logger) {
        try {
            Map<LocalCompilationSourceIdDmn, ModelSourceTuple> mappedModelSourceTuple = new HashMap<>();
            List<Resource> resources = new ArrayList<>();
            for (ModelSourceTuple modelSourceTuple : modelSourceTuples) {
                LocalCompilationSourceIdDmn key = getLocalCompilationSourceIdDmnFromModelSourceTuple(modelSourceTuple);
                if (mappedModelSourceTuple.containsKey(key)) {
                    logger.warn("Duplicate LocalCompilationSourceIdDmn key {} for model {}  ", key,
                                modelSourceTuple.model);
                } else {
                    mappedModelSourceTuple.put(key, modelSourceTuple);
                    resources.add(new ByteArrayResource(modelSourceTuple.source.getBytes(StandardCharsets.UTF_8)));
                }
            }
            List<EfestoCompilationOutput> toReturn = new ArrayList<>();
            List<DMNMessage> validationMessages = validateDMN(dmnContext, resources);
            mappedModelSourceTuple.values()
                    .forEach(modelSourceTuple -> {
                        DMNModel dmnModel = modelSourceTuple.model;
                        String modelSource = modelSourceTuple.source;
                        File dmnFile = new File(dmnModel.getResource().getSourcePath());
                        toReturn.add(DmnCompilerUtils.getDefaultEfestoCompilationOutput(getCleanedFilenameForURI(dmnFile),
                                                                                        dmnModel.getName(),
                                                                                        modelSource,
                                                                                        dmnModel,
                                                                                        validationMessages));
                    });
            storeMappedModelTuple(mappedModelSourceTuple);
            return toReturn;
        } catch (Exception e) {
            logger.error("ERROR", e);
            throw new KieCompilerServiceException(e);
        }
    }

    protected void storeMappedModelTuple(Map<LocalCompilationSourceIdDmn, ModelSourceTuple> mappedModelTuple) {
        mappedModelTuple.forEach((localCompilationSourceIdDmn, modelSourceTuple) ->
                                         ContextStorage.putEfestoCompilationSource(localCompilationSourceIdDmn,
                                                                                   modelSourceTuple.source));
    }

    protected LocalCompilationSourceIdDmn getLocalCompilationSourceIdDmnFromModelSourceTuple(ModelSourceTuple modelSourceTuple) {
        String fileName = modelSourceTuple.model.getResource().getSourcePath();
        fileName = getCleanedFilenameForURI(fileName);
        return new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(fileName);
    }

    protected ModelSourceTuple readDMNModel(DMNModel toRead) {
        return new ModelSourceTuple(toRead, readResource(toRead.getResource()));
    }

    private String readResource(Resource toRead) {
        try (InputStream is = toRead.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new KieCompilerServiceException(e);
        }
    }

    protected static class ModelSourceTuple {

        protected final DMNModel model;
        protected final String source;

        public ModelSourceTuple(DMNModel model, String source) {
            this.model = model;
            this.source = source;
        }
    }
}
