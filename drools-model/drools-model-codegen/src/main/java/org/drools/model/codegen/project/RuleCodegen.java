/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.model.codegen.project;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.drools.codegen.common.DroolsModelBuildContext;
import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.model.codegen.execmodel.PackageModelWriter;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleCodegen {

    public static final GeneratedFileType RULE_TYPE = GeneratedFileType.of("RULE", GeneratedFileType.Category.SOURCE);

    public static final String GENERATOR_NAME = "rules";

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleCodegen.class);

    private final DroolsModelBuildContext context;
    private final String name;

    public static RuleCodegen ofResources(DroolsModelBuildContext context, Collection<Resource> resources) {
        return new RuleCodegen(context, resources);
    }

    private final Collection<Resource> resources;

    private boolean hotReloadMode = false;
    private final boolean decisionTableSupported;

    private RuleCodegen(DroolsModelBuildContext context, Collection<Resource> resources) {
        Objects.requireNonNull(context, "context cannot be null");
        this.name = GENERATOR_NAME;
        this.context = context;
        this.resources = resources;
        this.decisionTableSupported = DecisionTableFactory.getDecisionTableProvider() != null;
    }

    public boolean isEmpty() {
        return resources.isEmpty();
    }

    protected Collection<GeneratedFile> internalGenerate() {

        DroolsModelBuilder droolsModelBuilder = new DroolsModelBuilder(context(), resources, decisionTableSupported, PackageModelWriter::new);

        droolsModelBuilder.build();
        Collection<GeneratedFile> generatedFiles = droolsModelBuilder.generateCanonicalModelSources();

        if (!droolsModelBuilder.hasRuleUnits()) {
            KieSessionModelBuilder kieSessionModelBuilder = new KieSessionModelBuilder(context(),
                    droolsModelBuilder.packageSources());
            generatedFiles.addAll(kieSessionModelBuilder.generate());
        }

        if (LOGGER.isDebugEnabled()) {
            generatedFiles.stream().forEach(genFile -> LOGGER.debug(genFile.toStringWithContent()));
        }

        return generatedFiles;
    }

    public boolean isEnabled() {
        return !isEmpty();
    }

    public DroolsModelBuildContext context() {
        return this.context;
    }

    public String name() {
        return name;
    }

    public final Collection<GeneratedFile> generate() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        return internalGenerate();
    }

    public RuleCodegen withHotReloadMode() {
        hotReloadMode = true;
        return this;
    }
}
