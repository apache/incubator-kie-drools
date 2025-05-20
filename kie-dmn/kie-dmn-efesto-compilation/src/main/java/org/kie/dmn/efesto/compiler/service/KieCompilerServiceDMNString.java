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

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.api.identifiers.LocalCompilationSourceIdDmn;
import org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils;
import org.kie.dmn.validation.DMNValidator;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.model.EfestoStringResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.getDMNModel;

public class KieCompilerServiceDMNString extends AbstractKieCompilerServiceDMN {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieCompilerServiceDMNString.class);

    @Override
    @SuppressWarnings( "rawtypes")
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoStringResource && ((EfestoStringResource) toProcess).getModelLocalUriId().model().equalsIgnoreCase("dmn");
    }

    @Override
    @SuppressWarnings( "rawtypes")
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }
        EfestoStringResource stringResource = (EfestoStringResource) toProcess;
        String modelSource = stringResource.getContent();
        List<DMNMessage> messages = validator.validate(new StringReader(modelSource),
                                                       DMNValidator.Validation.VALIDATE_SCHEMA,
                                                       DMNValidator.Validation.VALIDATE_MODEL,
                                                       DMNValidator.Validation.VALIDATE_COMPILATION,
                                                       DMNValidator.Validation.ANALYZE_DECISION_TABLE);
        // see https://github.com/apache/incubator-kie-issues/issues/1619
//        if (DmnCompilerUtils.hasError(messages)) {
//            return Collections.emptyList();
//        }
        try {
            ModelLocalUriId modelLocalUriId =stringResource.getModelLocalUriId();
            String basePath = modelLocalUriId.basePath();
            String fileName = basePath.substring(0, basePath.lastIndexOf("/"));
            LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                    .get(KieDmnComponentRoot.class)
                    .get(DmnIdFactory.class)
                    .get(fileName);
            ContextStorage.putEfestoCompilationSource(localCompilationSourceIdDmn, modelSource);
            DMNModel dmnModel = getDMNModel(modelSource);
            return Collections.singletonList(DmnCompilerUtils.getDefaultEfestoCompilationOutput(modelLocalUriId,
                    modelSource, dmnModel));
        } catch (Exception e) {
            throw new EfestoCompilationManagerException(e);
        }
    }
}
