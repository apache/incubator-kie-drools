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

public interface FieldDefinition {

    String getFieldName();

    String getObjectType();

    String getInitExpr();

    default List<AnnotationDefinition> getFieldAnnotations() { return Collections.emptyList(); }

    default List<AnnotationDefinition> setterAnnotations() { return Collections.emptyList(); }

    default List<AnnotationDefinition> getterAnnotations() { return Collections.emptyList(); }

    boolean isKeyField();

    boolean createAccessors();

    boolean isStatic();

    boolean isFinal();

    default boolean isOverride() {
        return false;
    }

    default Optional<String> overriddenGetterName() {
        return Optional.empty();
    }

    default Optional<String> overriddenSetterName() {
        return Optional.empty();
    }

    default Optional<String> getJavadocComment() {
        return Optional.empty();
    }
}
