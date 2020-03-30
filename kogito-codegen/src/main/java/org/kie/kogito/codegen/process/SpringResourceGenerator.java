/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process;

import java.util.Arrays;
import java.util.List;

import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.GeneratorContext;

/**
 * REST resources generator based on Spring Web API {@linkplain https://docs.spring.io/spring/docs/current/spring
 * -framework-reference/web.html#spring-web}.
 * The implementation is based on template files to generated the endpoints that are handled on
 * {@link AbstractResourceGenerator}.
 */
public class SpringResourceGenerator extends AbstractResourceGenerator {

    private static final String RESOURCE_TEMPLATE = "/class-templates/spring/SpringRestResourceTemplate.java";

    public SpringResourceGenerator(
            GeneratorContext context,
            WorkflowProcess process,
            String modelfqcn,
            String processfqcn,
            String appCanonicalName) {
        super(context, process, modelfqcn, processfqcn, appCanonicalName);
    }

    @Override
    protected String getResourceTemplate() {
        return RESOURCE_TEMPLATE;
    }

    @Override
    public String getUserTaskResourceTemplate() {
        return "/class-templates/spring/SpringRestResourceUserTaskTemplate.java";
    }

    @Override
    protected String getSignalResourceTemplate() {
        return "/class-templates/spring/SpringRestResourceSignalTemplate.java";
    }

    @Override
    public List<String> getRestAnnotations() {
        return Arrays.asList("PostMapping", "GetMapping", "PutMapping", "DeleteMapping");
    }
}