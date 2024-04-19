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
package org.kie.dmn.efesto.compiler.service;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilationServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilationService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.getDMNModel;

/**
 * For the moment being, use this for DMN "validation", since DMN does not have a code-generation phase
 */
public class KieCompilationServiceDMNInputStream implements KieCompilationService<EfestoCompilationOutput, EfestoCompilationContext> {

    static final DMNValidator validator = DMNValidatorFactory.newValidator(Arrays.asList(new ExtendedDMNProfile()));

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoInputStreamResource && ((EfestoInputStreamResource) toProcess).getModelType().equalsIgnoreCase("dmn");
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilationServiceException(String.format("%s can not process %s",
                    this.getClass().getName(),
                    toProcess.getClass().getName()));
        }
        EfestoInputStreamResource inputStreamResource = (EfestoInputStreamResource) toProcess;
        List<DMNMessage> messages = validator.validate(new InputStreamReader(inputStreamResource.getContent()),
                DMNValidator.Validation.VALIDATE_SCHEMA,
                DMNValidator.Validation.VALIDATE_MODEL,
                DMNValidator.Validation.VALIDATE_COMPILATION,
                DMNValidator.Validation.ANALYZE_DECISION_TABLE);
        if (DmnCompilerUtils.hasError(messages)) {
            return Collections.emptyList();
        } else {
            String modelSource = getModelSource(inputStreamResource.getContent());
            DMNModel dmnModel = getDMNModel(modelSource);
            return Collections.singletonList(DmnCompilerUtils.getDefaultEfestoCompilationOutput(inputStreamResource.getFileName(),
                    dmnModel.getName(),
                    modelSource));
        }
    }

    @Override
    public String getModelType() {
        return "dmn";
    }


    private String getModelSource(InputStream inputStream) {
        String newLine = System.getProperty("line.separator");
        try (inputStream) {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining(newLine));
        } catch (Exception e) {
            throw new EfestoCompilationManagerException(e);
        }
    }
}
