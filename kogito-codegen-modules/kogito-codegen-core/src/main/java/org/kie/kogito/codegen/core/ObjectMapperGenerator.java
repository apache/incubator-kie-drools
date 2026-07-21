/*
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
package org.kie.kogito.codegen.core;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import static org.drools.codegen.common.GeneratedFileType.REST;

public class ObjectMapperGenerator {

    private ObjectMapperGenerator() {
    }

    public static GeneratedFile generate(KogitoBuildContext context) {
        TemplatedGenerator generator = TemplatedGenerator.builder()
                .withTemplateBasePath("class-templates/config")
                .build(context, "GlobalObjectMapper");

        return new GeneratedFile(REST,
                generator.generatedFilePath(),
                generator.compilationUnitOrThrow().toString());
    }
}
