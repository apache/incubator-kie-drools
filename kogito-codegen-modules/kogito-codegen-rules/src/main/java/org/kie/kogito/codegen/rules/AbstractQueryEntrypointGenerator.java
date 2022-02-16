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
package org.kie.kogito.codegen.rules;

import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import static org.kie.kogito.codegen.rules.RuleCodegen.TEMPLATE_RULE_FOLDER;

public abstract class AbstractQueryEntrypointGenerator implements RuleFileGenerator {

    protected final RuleUnitDescription ruleUnit;
    protected final QueryGenerator query;
    protected final KogitoBuildContext context;

    protected final String queryName;
    protected final String queryClassName;
    protected final String targetClassName;
    protected final TemplatedGenerator generator;

    protected AbstractQueryEntrypointGenerator(
            QueryGenerator query,
            String targetClassNameSuffix,
            String templateName) {
        this.ruleUnit = query.ruleUnit();
        this.query = query;
        this.context = query.context();

        this.queryName = query.name();
        this.queryClassName = ruleUnit.getSimpleName() + "Query" + queryName;
        this.targetClassName = queryClassName + targetClassNameSuffix;

        this.generator = TemplatedGenerator.builder()
                .withPackageName(query.model().getNamespace())
                .withTemplateBasePath(TEMPLATE_RULE_FOLDER)
                .withTargetTypeName(targetClassName)
                .withFallbackContext(JavaKogitoBuildContext.CONTEXT_NAME)
                .build(context, templateName);
    }

    @Override
    public String generatedFilePath() {
        return generator.generatedFilePath();
    }

}
