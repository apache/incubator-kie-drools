/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.service.common.messaging;

import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

import com.fasterxml.jackson.databind.node.TextNode;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

class MessagingUtilsTest {

    TypedVariable typedVariable = new TypedVariable(TypedValue.Kind.UNIT, "name", "string", new TextNode("sample"), emptyList());

    @Test
    void modelToTracingTypedValue() {
        Assertions.assertNull(MessagingUtils.modelToTracingTypedValue(null));

        TypedValue typedValue = MessagingUtils.modelToTracingTypedValue(typedVariable);
        Assertions.assertNotNull(typedValue);
        Assertions.assertTrue(typedValue instanceof UnitValue);
    }

    @Test
    void modelToTracingTypedValueCollection() {
        Assertions.assertNull(MessagingUtils.modelToTracingTypedValueCollection(null));

        Collection<TypedValue> typedValues = MessagingUtils.modelToTracingTypedValueCollection(singletonList(typedVariable));
        Assertions.assertNotNull(typedValues);
        Assertions.assertEquals(1, typedValues.size());
        Assertions.assertTrue(typedValues.iterator().next() instanceof UnitValue);
    }

    @Test
    void modelToTracingTypedValueMap() {
        Assertions.assertNull(MessagingUtils.modelToTracingTypedValueMap(null));

        Map<String, TypedValue> valueMap = MessagingUtils.modelToTracingTypedValueMap(singletonList(typedVariable));
        Assertions.assertNotNull(valueMap);
        Assertions.assertEquals(1, valueMap.size());
        Assertions.assertTrue(valueMap.containsKey("name"));
        Assertions.assertTrue(valueMap.get("name") instanceof UnitValue);
    }
}
