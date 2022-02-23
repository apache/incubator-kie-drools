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

package org.kie.kogito.codegen.process.persistence.marshaller;

import java.util.Collection;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.persistence.proto.ProtoGenerator;
import org.kie.kogito.codegen.process.persistence.proto.ReflectionProtoGenerator;

public class ReflectionMarshallerGeneratorTest extends AbstractMarshallerGeneratorTest<Class<?>> {

    @Override
    protected ProtoGenerator.Builder protoGeneratorBuilder() {
        return ReflectionProtoGenerator.builder();
    }

    @Override
    protected MarshallerGenerator generator(KogitoBuildContext context, Collection<Class<?>> rawDataClasses) {
        return new ReflectionMarshallerGenerator(context, rawDataClasses);
    }

    @Override
    protected Class<?> convertToType(Class<?> clazz) {
        return clazz;
    }
}
