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
package org.drools.model.project.codegen;

import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class RuleCodegenError extends Error {

    private final KnowledgeBuilderResult[] errors;

    public RuleCodegenError(Collection<? extends KnowledgeBuilderResult> errors) {
        this(errors.toArray(new KnowledgeBuilderResult[errors.size()]));
    }

    public RuleCodegenError(KnowledgeBuilderResult... errors) {
        super("Errors were generated during the code-generation process:\n" +
                Arrays.stream(errors)
                        .map(KnowledgeBuilderResult::toString)
                        .collect(Collectors.joining("\n")));
        this.errors = errors;
    }

    public RuleCodegenError(Exception ex, KnowledgeBuilderResult... errors) {
        super("Errors were generated during the code-generation process:\n" +
                ex.getMessage() + "\n" +
                Arrays.stream(errors)
                        .map(KnowledgeBuilderResult::toString)
                        .collect(Collectors.joining("\n")),
                ex);
        this.errors = errors;
    }

    public KnowledgeBuilderResult[] getErrors() {
        return errors;
    }
}
