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

package org.kie.kogito.codegen;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.kie.api.definition.process.WorkflowProcess;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.context.SpringBootKogitoBuildContext;
import org.kie.kogito.codegen.process.AbstractResourceGenerator;
import org.kie.kogito.codegen.process.ReactiveResourceGenerator;
import org.kie.kogito.codegen.process.ResourceGenerator;
import org.kie.kogito.codegen.process.SpringResourceGenerator;

public class ResourceGeneratorFactory {

    enum GeneratorType {
        SPRING(SpringBootKogitoBuildContext.class, false),
        QUARKUS(QuarkusKogitoBuildContext.class, false),
        SPRING_REACTIVE(SpringBootKogitoBuildContext.class, true),
        QUARKUS_REACTIVE(QuarkusKogitoBuildContext.class, true);

        Class<? extends KogitoBuildContext> buildContextClass;
        boolean reactive;

        GeneratorType(Class<? extends KogitoBuildContext> buildContextClass,
                      boolean reactive) {
            this.buildContextClass = buildContextClass;
            this.reactive = reactive;
        }

        public static Optional<GeneratorType> from(GeneratorContext context) {
            return Arrays.stream(GeneratorType.values())
                    .filter(v -> Objects.equals(v.reactive, isReactiveGenerator(context)))
                    .filter(v -> v.buildContextClass.isInstance(context.getBuildContext()))
                    .findFirst();
        }

        static boolean isReactiveGenerator(GeneratorContext context) {
            return "reactive".equals(context.getApplicationProperty(GeneratorConfig.KOGITO_REST_RESOURCE_TYPE_PROP)
                                             .orElse(""));
        }
    }

    public Optional<AbstractResourceGenerator> create(GeneratorContext context,
                                                      WorkflowProcess process,
                                                      String modelfqcn,
                                                      String processfqcn,
                                                      String appCanonicalName) {

        return GeneratorType
                .from(context)
                .map(type -> {
                    switch (type) {
                        case SPRING:
                            return new SpringResourceGenerator(context,
                                                               process,
                                                               modelfqcn,
                                                               processfqcn,
                                                               appCanonicalName);
                        case QUARKUS:
                            return new ResourceGenerator(context,
                                                         process,
                                                         modelfqcn,
                                                         processfqcn,
                                                         appCanonicalName);
                        case QUARKUS_REACTIVE:
                            return new ReactiveResourceGenerator(context,
                                                                 process,
                                                                 modelfqcn,
                                                                 processfqcn,
                                                                 appCanonicalName);
                        default:
                            throw new NoSuchElementException("No Resource Generator for: " + type);
                    }
                });
    }
}