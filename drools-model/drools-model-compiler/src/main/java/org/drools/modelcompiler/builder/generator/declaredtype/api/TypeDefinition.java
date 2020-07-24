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

package org.drools.modelcompiler.builder.generator.declaredtype.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface TypeDefinition {

    String getTypeName();

    List<? extends FieldDefinition> getFields();

    List<FieldDefinition> getKeyFields();

    Optional<String> getSuperTypeName();

    default List<String> getInterfacesNames() { return Collections.emptyList(); }

    List<AnnotationDefinition> getAnnotationsToBeAdded();

    List<FieldDefinition> findInheritedDeclaredFields();

    default List<MethodDefinition> getMethods() { return Collections.emptyList(); }

    default Optional<String> getJavadoc() {
        return Optional.empty();
    }
 }
