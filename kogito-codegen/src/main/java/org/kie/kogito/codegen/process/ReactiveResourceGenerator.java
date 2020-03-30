/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process;

import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.GeneratorContext;

public class ReactiveResourceGenerator extends ResourceGenerator {

    private static final String REACTIVE_RESOURCE_TEMPLATE = "/class-templates/ReactiveRestResourceTemplate.java";

    public ReactiveResourceGenerator(
            GeneratorContext context,
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String appCanonicalName) {
        super(context, process, modelfqcn, processfqcn, appCanonicalName);
    }

    @Override
    protected String getResourceTemplate() {
        return REACTIVE_RESOURCE_TEMPLATE;
    }
}