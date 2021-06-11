/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.service.common;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

public class TypedValueStructureUtils {

    private TypedValueStructureUtils() {
        //Prevent instantiation of this utility class
    }

    public static <D extends TypedVariable<D>, C extends TypedVariable<C>> boolean isStructureIdentical(Collection<D> originalInputs,
            Collection<C> counterfactualSearchDomains) {

        //Basic size checks
        if (Objects.isNull(originalInputs) && Objects.isNull(counterfactualSearchDomains)) {
            return true;
        }
        if (Objects.isNull(originalInputs)) {
            return false;
        }
        if (Objects.isNull(counterfactualSearchDomains)) {
            return false;
        }
        if (originalInputs.isEmpty() && counterfactualSearchDomains.isEmpty()) {
            return true;
        }

        //Check all peers are equal
        Map<String, StructureHolder<D>> originalInputsMap = originalInputs.stream().map(StructureHolder::new).collect(Collectors.toMap(ih -> ih.name, ih -> ih));
        Map<String, StructureHolder<C>> searchDomainsInputMap =
                counterfactualSearchDomains.stream().map(StructureHolder::new).collect(Collectors.toMap(ih -> ih.name, ih -> ih));
        if (originalInputsMap.size() != searchDomainsInputMap.size()) {
            return false;
        }
        if (!originalInputsMap.entrySet().stream().allMatch(e -> Objects.equals(e.getValue(), searchDomainsInputMap.get(e.getKey())))) {
            return false;
        }

        //Check direct descendents
        Collection<D> originalInputsStructures =
                originalInputsMap.values()
                        .stream()
                        .filter(e -> e.kind == TypedValue.Kind.STRUCTURE)
                        .map(ih -> ih.original.getComponents())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
        Collection<C> searchDomainsStructures =
                searchDomainsInputMap.values()
                        .stream()
                        .filter(e -> e.kind == TypedValue.Kind.STRUCTURE)
                        .map(ih -> ih.original.getComponents())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

        return isStructureIdentical(originalInputsStructures, searchDomainsStructures);
    }

    private static class StructureHolder<T extends TypedVariable<T>> {

        private final TypedValue.Kind kind;
        private final String name;
        private final String typeRef;
        private final T original;

        public StructureHolder(T typedVariableWithValue) {
            this.kind = typedVariableWithValue.getKind();
            this.name = typedVariableWithValue.getName();
            this.typeRef = typedVariableWithValue.getTypeRef();
            this.original = typedVariableWithValue;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StructureHolder<?> that = (StructureHolder<?>) o;
            return kind == that.kind && Objects.equals(name, that.name) && Objects.equals(typeRef, that.typeRef);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kind, name, typeRef);
        }
    }
}
