/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler.service;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.api.identifiers.LocalCompilationSourceIdDmn;
import org.kie.dmn.core.compiler.utils.DmnCompilerUtils;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

import static org.kie.dmn.core.compiler.utils.DmnCompilerUtils.getDMNModel;

public class KieCompilationServiceDMNFile implements KieCompilerService<EfestoCompilationOutput, EfestoCompilationContext> {

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
        try {
            File dmnFile = fileResource.getContent();
            String fileName = dmnFile.getName();
            String modelSource = Files.readString(dmnFile.toPath());
            LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                    .get(KieDmnComponentRoot.class)
                    .get(DmnIdFactory.class)
                    .get(fileName);

            DMNModel dmnModel = getDMNModel(modelSource);
            return Collections.singletonList(DmnCompilerUtils.getDefaultEfestoCompilationOutput(fileName,
                                                                                                dmnModel.getName(),
                                                                                                modelSource));
        } catch (Exception e) {
            throw new EfestoCompilationManagerException(e);
        }
    }

    @Override
    public String getModelType() {
        return "dmn";
    }

}
