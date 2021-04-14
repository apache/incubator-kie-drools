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
package org.kie.kogito.trusty.storage.infinispan;

import java.util.List;
import java.util.stream.Collectors;

import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.JsonNodeTestField;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange.LOWER_BOUND;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange.UPPER_BOUND;

public class CounterfactualDomainRangeMarshallerTest extends MarshallerTestTemplate<CounterfactualDomainRange> {

    private static final List<AbstractTestField<CounterfactualDomainRange, ?>> TEST_FIELD_LIST = List.of(
            new JsonNodeTestField<>(LOWER_BOUND, new IntNode(1), CounterfactualDomainRange::getLowerBound, CounterfactualDomainRange::setLowerBound),
            new JsonNodeTestField<>(UPPER_BOUND, new IntNode(2), CounterfactualDomainRange::getUpperBound, CounterfactualDomainRange::setUpperBound));

    public CounterfactualDomainRangeMarshallerTest() {
        super(CounterfactualDomainRange.class);
    }

    @Override
    protected CounterfactualDomainRange buildEmptyObject() {
        return new CounterfactualDomainRange();
    }

    @Override
    protected MessageMarshaller<CounterfactualDomainRange> buildMarshaller() {
        return new CounterfactualDomainRangeMarshaller(new ObjectMapper());
    }

    @Override
    protected List<AbstractTestField<CounterfactualDomainRange, ?>> getTestFieldList() {
        return TEST_FIELD_LIST;
    }

    @Test
    @Override
    void noUncoveredProperties() {
        List<String> testFieldNameList = getTestFieldList().stream()
                .map(AbstractTestField::getFieldName)
                .collect(Collectors.toList());

        //Type is marshalled by the super-class marshaller so ignore inherited properties
        streamNonStaticFields(modelClass).forEach(field -> {
            String serializedFieldName = field.isAnnotationPresent(JsonProperty.class)
                    ? field.getAnnotation(JsonProperty.class).value()
                    : field.getName();

            long matches = testFieldNameList.stream().filter(n -> n.equals(serializedFieldName)).count();
            assertEquals(1, matches, () -> String.format("Field \"%s\" of %s model is not handled properly in the corresponding test", serializedFieldName, modelClass.getSimpleName()));
        });
    }

}
