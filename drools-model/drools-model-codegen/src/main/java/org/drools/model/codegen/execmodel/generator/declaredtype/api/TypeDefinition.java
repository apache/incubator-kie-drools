/**
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
package org.drools.model.codegen.execmodel.generator.declaredtype.api;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface TypeDefinition {

    String getTypeName();

    default List<? extends FieldDefinition> getFields() {
        return Collections.emptyList();
    }

    default List<FieldDefinition> getKeyFields() {
        return Collections.emptyList();
    }

    default Optional<String> getSuperTypeName() {
        return Optional.empty();
    }

    default List<String> getInterfacesNames() {
        return Collections.emptyList();
    }

    default List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return Collections.emptyList();
    }

    default List<FieldDefinition> findInheritedDeclaredFields() {
        return Collections.emptyList();
    }

    default List<MethodDefinition> getMethods() {
        return Collections.emptyList();
    }

    default Optional<String> getJavadoc() {
        return Optional.empty();
    }
}
