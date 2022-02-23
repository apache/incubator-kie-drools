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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Optional;

import org.infinispan.protostream.descriptors.FieldDescriptor;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

public class ReflectionMarshallerGenerator extends AbstractMarshallerGenerator<Class<?>> {

    public ReflectionMarshallerGenerator(KogitoBuildContext context, Collection<Class<?>> rawDataClasses) {
        super(context, rawDataClasses);
    }

    public ReflectionMarshallerGenerator(KogitoBuildContext context) {
        this(context, null);
    }

    @Override
    protected boolean isArray(String javaType, FieldDescriptor field) {
        Optional<Class<?>> clazz = modelClasses.stream().filter(cls -> cls.getName().equals(javaType)).findFirst();
        if (clazz.isPresent()) {
            try {
                PropertyDescriptor[] pds = Introspector.getBeanInfo(clazz.get()).getPropertyDescriptors();
                for (PropertyDescriptor pd : pds) {
                    if (pd.getName().equals(field.getName())) {
                        return pd.getPropertyType().isArray();
                    }
                }
                return false;
            } catch (IntrospectionException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
