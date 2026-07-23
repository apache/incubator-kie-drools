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
package org.kie.kogito.trusty.service.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.api.CounterfactualSearchDomain;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainValue;
import org.kie.kogito.explainability.api.NamedTypedValue;
import org.kie.kogito.tracing.typedvalue.BaseTypedValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionInput;
import org.kie.kogito.trusty.storage.api.model.decision.DecisionOutcome;

public class CounterfactualParameterValidation {

    private CounterfactualParameterValidation() {
        //Prevent instantiation of this utility class
    }

    private interface Check<C, D> {

        boolean check(Collection<C> structure1,
                Collection<D> structure2);
    }

    /**
     * Checks if two structured parameters are consistent.
     * What constitutes "consistent" is determined by the concrete sub-classes.
     *
     * @param <C> Type C of one parameter
     * @param <CV> Converter to convert Type C to internal representation
     * @param <D> Type D of another parameter
     * @param <DV> Converter to convert Type D to internal representation
     */
    private static abstract class BaseCheck<C, CV, D, DV> implements Check<C, D> {

        @Override
        public boolean check(Collection<C> structure1, Collection<D> structure2) {
            Collection<StructureHolder<CV>> normalisedStructure1 = normaliseStructure1(structure1);
            Collection<StructureHolder<DV>> normalisedStructure2 = normaliseStructure2(structure2);

            return doCheck(normalisedStructure1, normalisedStructure2);
        }

        protected boolean doCheck(Collection<StructureHolder<CV>> normalisedStructure1,
                Collection<StructureHolder<DV>> normalisedStructure2) {
            if (Objects.isNull(normalisedStructure1) && Objects.isNull(normalisedStructure2)) {
                return true;
            }
            if (Objects.isNull(normalisedStructure1)) {
                return false;
            }
            if (Objects.isNull(normalisedStructure2)) {
                return false;
            }
            if (normalisedStructure1.isEmpty() && normalisedStructure2.isEmpty()) {
                return true;
            }

            Map<String, StructureHolder<CV>> structure1Map = normalisedStructure1.stream().collect(Collectors.toMap(ih -> ih.name, ih -> ih));
            Map<String, StructureHolder<DV>> structure2Map = normalisedStructure2.stream().collect(Collectors.toMap(ih -> ih.name, ih -> ih));
            if (!checkMembership(structure1Map, structure2Map)) {
                return false;
            }

            //Check direct descendents
            Collection<StructureHolder<CV>> structure1ChildStructures =
                    structure1Map.values()
                            .stream()
                            .map(ih -> getChildrenOfStructure1(ih.original))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
            Collection<StructureHolder<DV>> structure2ChildStructures =
                    structure2Map.values()
                            .stream()
                            .map(ih -> getChildrenOfStructure2(ih.original))
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());

            return doCheck(structure1ChildStructures, structure2ChildStructures);
        }

        protected Collection<StructureHolder<CV>> normaliseStructure1(Collection<C> structure1) {
            if (Objects.isNull(structure1)) {
                return null;
            }
            return structure1.stream().map(this::convertStructure1toHolder).collect(Collectors.toList());
        }

        protected Collection<StructureHolder<DV>> normaliseStructure2(Collection<D> structure2) {
            if (Objects.isNull(structure2)) {
                return null;
            }
            return structure2.stream().map(this::convertStructure2toHolder).collect(Collectors.toList());
        }

        protected abstract StructureHolder<CV> convertStructure1toHolder(C value);

        protected abstract StructureHolder<DV> convertStructure2toHolder(D value);

        protected abstract Collection<StructureHolder<CV>> getChildrenOfStructure1(CV value);

        protected abstract Collection<StructureHolder<DV>> getChildrenOfStructure2(DV value);

        protected abstract boolean checkMembership(Map<String, StructureHolder<CV>> structure1Map,
                Map<String, StructureHolder<DV>> structure2Map);
    }

    private static class IdenticalCheck extends BaseCheck<DecisionInput, TypedValue, CounterfactualSearchDomain, CounterfactualSearchDomainValue> {

        @Override
        @SuppressWarnings("EqualsBetweenInconvertibleTypes")
        protected boolean checkMembership(Map<String, StructureHolder<TypedValue>> structure1Map,
                Map<String, StructureHolder<CounterfactualSearchDomainValue>> structure2Map) {
            //Are the maps equal in size?
            boolean validSize = structure2Map.size() == structure1Map.size();
            //Do all members of Structure 1 exist in Structure 2?
            boolean validEntries = structure1Map.entrySet().stream().allMatch(e -> Objects.equals(e.getValue(), structure2Map.get(e.getKey())));
            //If they're equal size and the members are identical the structures must be equal.
            return validSize && validEntries;
        }

        @Override
        protected StructureHolder<TypedValue> convertStructure1toHolder(DecisionInput value) {
            return new StructureHolder<>(value.getValue().getKind(),
                    value.getName(),
                    value.getValue().getType(),
                    value.getValue());
        }

        @Override
        protected StructureHolder<CounterfactualSearchDomainValue> convertStructure2toHolder(CounterfactualSearchDomain value) {
            return new StructureHolder<>(value.getValue().getKind(),
                    value.getName(),
                    value.getValue().getType(),
                    value.getValue());
        }

        @Override
        protected Collection<StructureHolder<TypedValue>> getChildrenOfStructure1(TypedValue value) {
            if (value.getKind() != BaseTypedValue.Kind.STRUCTURE) {
                return Collections.emptyList();
            }
            return value.toStructure().getValue()
                    .entrySet()
                    .stream()
                    .map(e -> new StructureHolder<TypedValue>(e.getValue().getKind(),
                            e.getKey(),
                            e.getValue().getType(),
                            e.getValue()))
                    .collect(Collectors.toList());
        }

        @Override
        protected Collection<StructureHolder<CounterfactualSearchDomainValue>> getChildrenOfStructure2(CounterfactualSearchDomainValue value) {
            if (value.getKind() != BaseTypedValue.Kind.STRUCTURE) {
                return Collections.emptyList();
            }
            return value.toStructure().getValue()
                    .entrySet()
                    .stream()
                    .map(e -> new StructureHolder<CounterfactualSearchDomainValue>(e.getValue().getKind(),
                            e.getKey(),
                            e.getValue().getType(),
                            e.getValue()))
                    .collect(Collectors.toList());
        }
    }

    private static class SubsetCheck extends BaseCheck<DecisionOutcome, TypedValue, NamedTypedValue, TypedValue> {

        @Override
        protected boolean checkMembership(Map<String, StructureHolder<TypedValue>> structure1Map,
                Map<String, StructureHolder<TypedValue>> structure2Map) {
            //Is the second map at least the size of the first?
            boolean validSize = structure2Map.size() <= structure1Map.size();
            //Do all members of Structure 2 exist in Structure 1?
            boolean validEntries = structure2Map.entrySet().stream().allMatch(e -> Objects.equals(e.getValue(), structure1Map.get(e.getKey())));
            //If Structure 2's size is less than of equal to that of Structure 1 and all members of Structure 2 exist in Structure 1 then Structure 2 must be s subset of Structure 1.
            return validSize && validEntries;
        }

        @Override
        protected StructureHolder<TypedValue> convertStructure1toHolder(DecisionOutcome value) {
            return new StructureHolder<>(value.getOutcomeResult().getKind(),
                    value.getOutcomeName(),
                    value.getOutcomeResult().getType(),
                    value.getOutcomeResult());
        }

        @Override
        protected StructureHolder<TypedValue> convertStructure2toHolder(NamedTypedValue value) {
            return new StructureHolder<>(value.getValue().getKind(),
                    value.getName(),
                    value.getValue().getType(),
                    value.getValue());
        }

        @Override
        protected Collection<StructureHolder<TypedValue>> getChildrenOfStructure1(TypedValue value) {
            if (value.getKind() != BaseTypedValue.Kind.STRUCTURE) {
                return Collections.emptyList();
            }
            return value.toStructure().getValue()
                    .entrySet()
                    .stream()
                    .map(e -> new StructureHolder<TypedValue>(e.getValue().getKind(),
                            e.getKey(),
                            e.getValue().getType(),
                            e.getValue()))
                    .collect(Collectors.toList());
        }

        @Override
        protected Collection<StructureHolder<TypedValue>> getChildrenOfStructure2(TypedValue value) {
            return getChildrenOfStructure1(value);
        }
    }

    private static final IdenticalCheck IDENTICAL = new IdenticalCheck();
    private static final SubsetCheck SUBSET = new SubsetCheck();

    /**
     * Checks whether the two structures are identical; irrespective of values.
     *
     * @param inputs Inputs for a Decision
     * @param searchDomains Search Domains for a Counterfactual Explanation
     * @return True if they are identical
     */
    public static boolean isStructureIdentical(Collection<DecisionInput> inputs, Collection<CounterfactualSearchDomain> searchDomains) {
        return IDENTICAL.check(inputs, searchDomains);
    }

    /**
     * Checks whether the structure of the Goals is a subset of the structure of the Outcomes; irrespective of values.
     *
     * @param outcomes Outcomes for a Decision
     * @param goals Goals for a Counterfactual Explanation
     * @return True if Goals is a subset of Outcomes
     */
    public static boolean isStructureSubset(Collection<DecisionOutcome> outcomes, Collection<NamedTypedValue> goals) {
        return SUBSET.check(outcomes, goals);
    }

    private static class StructureHolder<T> {

        private final TypedValue.Kind kind;
        private final String name;
        private final String typeRef;
        private final T original;

        public StructureHolder(BaseTypedValue.Kind kind, String name, String typeRef, T original) {
            this.kind = kind;
            this.name = name;
            this.typeRef = typeRef;
            this.original = original;
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
