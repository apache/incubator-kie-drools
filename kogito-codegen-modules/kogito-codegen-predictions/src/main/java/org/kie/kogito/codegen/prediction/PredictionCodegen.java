/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.prediction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.drools.codegen.common.GeneratedFile;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.core.AbstractGenerator;
import org.kie.kogito.codegen.prediction.config.PredictionConfigGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.codegen.prediction.PredictionCodegenUtils.generateModelFromGeneratedResources;
import static org.kie.kogito.codegen.prediction.PredictionCodegenUtils.generateModelsFromResource;

public class PredictionCodegen extends AbstractGenerator {

    public static final String GENERATOR_NAME = "predictions";
    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionCodegen.class);

    private final Collection<PMMLResource> resources;

    public PredictionCodegen(KogitoBuildContext context,
            Collection<PMMLResource> resources) {
        super(context, GENERATOR_NAME, new PredictionConfigGenerator(context));
        this.resources = resources;
    }

    @Override
    public Optional<ApplicationSection> section() {
        LOGGER.debug("section");
        return Optional.of(new PredictionModelsGenerator(context(), applicationCanonicalName(), resources));
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public int priority() {
        return 40;
    }

    @Override
    public String applicationCanonicalName() {
        return super.applicationCanonicalName();
    }

    @Override
    protected Collection<GeneratedFile> internalGenerate() {
        LOGGER.debug("internalGenerate");
        Collection<GeneratedFile> files = new ArrayList<>();
        for (PMMLResource resource : resources) {
            generateModelsFromResource(files, resource, this);
            for (Map.Entry<String, GeneratedResources> generatedResourcesEntry : resource.getGeneratedResourcesMap().entrySet()) {
                generateModelFromGeneratedResources(files, generatedResourcesEntry);
            }
        }
        return files;
    }
}
