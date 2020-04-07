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

package org.kie.pmml.models.tree.compiler.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.DataType;
import org.dmg.pmml.SimplePredicate;
import org.junit.Test;
import org.kie.pmml.models.drooled.tuples.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTTestUtils.getSimplePredicate;

public class KiePMMLASTFactoryUtilsTest {

    @Test
    public void getConstraintEntryFromSimplePredicates() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        String fieldName = "FIELD_NAME";
        List<SimplePredicate> simplePredicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate(fieldName, DataType.STRING, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        final List<KiePMMLFieldOperatorValue> retrieved = Collections.singletonList(KiePMMLASTFactoryUtils.getConstraintEntryFromSimplePredicates(fieldName, "or", simplePredicates, fieldTypeMap));
        assertEquals(simplePredicates.size(), retrieved.size());
        IntStream.range(0, simplePredicates.size()).forEach(i -> {
            SimplePredicate simplePredicate = simplePredicates.get(i);
            KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.get(i);
            assertEquals(fieldName, kiePMMLFieldOperatorValue.getName());
            assertNotNull(kiePMMLFieldOperatorValue.getConstraintsAsString());
        });
    }
}