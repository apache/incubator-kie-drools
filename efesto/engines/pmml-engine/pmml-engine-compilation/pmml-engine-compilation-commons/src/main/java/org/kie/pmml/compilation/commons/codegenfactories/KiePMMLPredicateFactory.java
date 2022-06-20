/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compilation.commons.codegenfactories;

import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.*;

import java.util.List;

import static org.kie.pmml.compilation.commons.codegenfactories.KiePMMLCompoundPredicateFactory.getCompoundPredicateVariableDeclaration;
import static org.kie.pmml.compilation.commons.codegenfactories.KiePMMLFalsePredicateFactory.getFalsePredicateVariableDeclaration;
import static org.kie.pmml.compilation.commons.codegenfactories.KiePMMLSimplePredicateFactory.getSimplePredicateVariableDeclaration;
import static org.kie.pmml.compilation.commons.codegenfactories.KiePMMLSimpleSetPredicateFactory.getSimpleSetPredicateVariableDeclaration;
import static org.kie.pmml.compilation.commons.codegenfactories.KiePMMLTruePredicateFactory.getTruePredicateVariableDeclaration;

/**
 * Facade for actual implementations
 */
public class KiePMMLPredicateFactory {

    private static final String PREDICATE_NOT_MANAGED = "Predicate %s not managed";

    private KiePMMLPredicateFactory() {
        // Avoid instantiation
    }

    public static BlockStmt getKiePMMLPredicate(final String variableName,
                                                final Predicate predicate,
                                                final List<Field<?>> fields) {
        if (predicate instanceof SimplePredicate) {
            return getSimplePredicateVariableDeclaration(variableName, (SimplePredicate) predicate, fields);
        } else if (predicate instanceof SimpleSetPredicate) {
            return getSimpleSetPredicateVariableDeclaration(variableName, (SimpleSetPredicate) predicate);
        } else if (predicate instanceof CompoundPredicate) {
            return getCompoundPredicateVariableDeclaration(variableName, (CompoundPredicate) predicate, fields);
        } else if (predicate instanceof True) {
            return getTruePredicateVariableDeclaration(variableName, (True) predicate);
        } else if (predicate instanceof False) {
            return getFalsePredicateVariableDeclaration(variableName, (False) predicate);
        } else {
            throw new IllegalArgumentException(String.format(PREDICATE_NOT_MANAGED, predicate.getClass()));
        }
    }

}
