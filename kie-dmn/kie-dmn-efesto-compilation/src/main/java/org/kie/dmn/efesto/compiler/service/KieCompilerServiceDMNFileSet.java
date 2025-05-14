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
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.api.identifiers.LocalCompilationSourceIdDmn;
import org.kie.dmn.efesto.compiler.model.DmnCompilationContext;
import org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.EfestoCompilationManagerException;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoFileSetResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.getCleanedFilename;
import static org.kie.dmn.efesto.compiler.utils.DmnCompilerUtils.getDMNModelsFromFiles;

public class KieCompilerServiceDMNFileSet extends AbstractKieCompilerServiceDMN {

    private static final Logger LOGGER = LoggerFactory.getLogger(KieCompilerServiceDMNFileSet.class);

    @Override
    @SuppressWarnings( "rawtypes")
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoFileSetResource efestoFileSetResource &&
                efestoFileSetResource.getModelLocalUriId().model().equalsIgnoreCase("dmn");
    }

    @Override
    @SuppressWarnings( "rawtypes")
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
        EfestoFileSetResource fileSetResource = (EfestoFileSetResource) toProcess;
        Set<File> dmnFiles = fileSetResource.getContent();
        try {
            List<DMNModel> dmnModels = getDMNModelsFromFiles(dmnFiles, dmnContext.getCustomDMNProfiles(), dmnContext.getRuntimeTypeCheckOption(), dmnContext.getContextClassloader());
            storeSources(dmnFiles);
            return dmnModels.stream()
                    .map(
                    dmnModel -> {
                        File dmnFile = new File(dmnModel.getResource().getSourcePath());
                        String modelSource = readFile(dmnFile);
                        return DmnCompilerUtils.getDefaultEfestoCompilationOutput(getCleanedFilename(dmnFile),
                                                                           dmnModel.getName(),
                                                                                  modelSource,
                                                                           dmnModel);
                    })
                    .toList();

        } catch (Exception e) {
            throw new EfestoCompilationManagerException(e);
        }
    }

    private void storeSources(Set<File> dmnFiles) {
        dmnFiles.forEach(file -> {
            String fileName = file.getName();
            LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                    .get(KieDmnComponentRoot.class)
                    .get(DmnIdFactory.class)
                    .get(fileName);
            String modelSource = readFile(file);
            ContextStorage.putEfestoCompilationSource(localCompilationSourceIdDmn, modelSource);
        });
    }

    private String readFile(File toRead) {
        try {
            return Files.readString(toRead.toPath());
        } catch (IOException e) {
            throw new KieCompilerServiceException(e);
        }
    }
}
