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

package org.kie.kogito.quarkus.workflow.deployment;

import java.lang.reflect.Field;
import java.util.Collection;

import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.jboss.jandex.ClassInfo;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.persistence.marshaller.AbstractMarshallerGenerator;

public class JandexMarshallerGenerator extends AbstractMarshallerGenerator<ClassInfo> {

    public JandexMarshallerGenerator(KogitoBuildContext context, Collection<ClassInfo> rawDataClasses) {
        super(context, rawDataClasses);
    }

    @Override
    protected boolean isArray(String javaType, FieldDescriptor field) {
        try {
            Field declaredField = Class.forName(javaType).getDeclaredField(field.getName());
            return declaredField.getType().isArray() && !declaredField.getType().isPrimitive();
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            return false;
        }
    }
}
