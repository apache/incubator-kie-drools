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
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;
import org.kie.kogito.trusty.storage.infinispan.testfield.MappedCollectionTestField;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical.CATEGORIES;

public class CounterfactualDomainCategoricalMarshallerTest extends MarshallerTestTemplate<CounterfactualDomainCategorical> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final CounterfactualDomainCategoricalMarshaller MARSHALLER = new CounterfactualDomainCategoricalMarshaller(MAPPER);

    private static final List<AbstractTestField<CounterfactualDomainCategorical, ?>> TEST_FIELD_LIST = List.of(
            new MappedCollectionTestField<>(CATEGORIES,
                    List.of(new TextNode("A"), new TextNode("B")),
                    CounterfactualDomainCategorical::getCategories,
                    CounterfactualDomainCategorical::setCategories,
                    MARSHALLER::safeJsonFromString,
                    MARSHALLER::safeStringFromJson,
                    String.class));

    public CounterfactualDomainCategoricalMarshallerTest() {
        super(CounterfactualDomainCategorical.class);
    }

    @Override
    protected CounterfactualDomainCategorical buildEmptyObject() {
        return new CounterfactualDomainCategorical();
    }

    @Override
    protected MessageMarshaller<CounterfactualDomainCategorical> buildMarshaller() {
        return MARSHALLER;
    }

    @Override
    protected List<AbstractTestField<CounterfactualDomainCategorical, ?>> getTestFieldList() {
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
