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

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.api.identifiers.LocalCompilationSourceIdDmn;
import org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils;
import org.kie.dmn.validation.DMNValidator;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.io.MemoryFile;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.getDMNModel;


public class KieCompilerServiceDMNFile extends AbstractKieCompilerServiceDMN {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoFileResource && ((EfestoFileResource) toProcess).getModelType().equalsIgnoreCase("dmn");
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }
        EfestoFileResource fileResource = (EfestoFileResource) toProcess;
        File dmnFile = fileResource.getContent();
        String modelSource;
        try {
            modelSource = dmnFile instanceof MemoryFile memoryFile ? new String(memoryFile.getContent(), StandardCharsets.UTF_8) : ( Files.readString(dmnFile.toPath()));

        } catch (IOException e) {
            throw new KieCompilerServiceException(String.format("%s failed to read content of %s",
                                                                this.getClass().getName(),
                                                                fileResource.getContent()));
        }
        List<DMNMessage> messages = validator.validate(new StringReader(modelSource),
                                                       DMNValidator.Validation.VALIDATE_SCHEMA,
                                                       DMNValidator.Validation.VALIDATE_MODEL,
                                                       DMNValidator.Validation.VALIDATE_COMPILATION,
                                                       DMNValidator.Validation.ANALYZE_DECISION_TABLE);
        if (DmnCompilerUtils.hasError(messages)) {
            return Collections.emptyList();
        }
        try {
            String fileName = dmnFile.getName();
            LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                    .get(KieDmnComponentRoot.class)
                    .get(DmnIdFactory.class)
                    .get(fileName);

            ContextStorage.putEfestoCompilationSource(localCompilationSourceIdDmn, modelSource);
            DMNModel dmnModel = getDMNModel(modelSource);
            return Collections.singletonList(DmnCompilerUtils.getDefaultEfestoCompilationOutput(fileName,
                    dmnModel.getName(),
                    modelSource));
        } catch (Exception e) {
            throw new EfestoCompilationManagerException(e);
        }
    }
}
