/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.trusty.storage.infinispan;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.infinispan.protostream.MessageMarshaller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.storage.infinispan.testfield.AbstractTestField;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

abstract class MarshallerTestTemplate<T> {

    protected final Class<T> modelClass;

    protected MarshallerTestTemplate(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    protected abstract T buildEmptyObject();

    protected abstract MessageMarshaller<T> buildMarshaller();

    protected abstract List<AbstractTestField<T, ?>> getTestFieldList();

    @Test
    void allPropertiesAreCoveredByTheMarshaller() throws IOException {
        List<AbstractTestField<T, ?>> list = getTestFieldList();

        T object = buildEmptyObject();
        list.forEach(td -> td.setValue(object));

        MessageMarshaller.ProtoStreamWriter protoStreamWriter = mock(MessageMarshaller.ProtoStreamWriter.class);
        MessageMarshaller<T> marshaller = buildMarshaller();
        marshaller.writeTo(protoStreamWriter, object);

        assertEquals(list.size(), mockingDetails(protoStreamWriter).getInvocations().size());
        for (AbstractTestField<T, ?> td : list) {
            td.verifyWriter(protoStreamWriter);
        }
    }

    @Test
    void allPropertiesAreCoveredByTheUnmarshaller() throws IOException {
        List<AbstractTestField<T, ?>> list = getTestFieldList();

        MessageMarshaller.ProtoStreamReader protoStreamReader = mock(MessageMarshaller.ProtoStreamReader.class);
        for (AbstractTestField<T, ?> td : list) {
            td.mockReader(protoStreamReader);
        }

        MessageMarshaller<T> marshaller = buildMarshaller();
        T output = marshaller.readFrom(protoStreamReader);

        assertEquals(list.size(), mockingDetails(protoStreamReader).getInvocations().size());
        list.forEach(td -> td.assertValue(output));
    }

    @Test
    void noUncoveredProperties() throws IOException {
        List<String> testFieldNameList = getTestFieldList().stream()
                .map(AbstractTestField::getFieldName)
                .collect(Collectors.toList());

        streamAllNonStaticFields(modelClass).forEach(field -> {
            String serializedFieldName = field.isAnnotationPresent(JsonProperty.class)
                    ? field.getAnnotation(JsonProperty.class).value()
                    : field.getName();

            long matches = testFieldNameList.stream().filter(n -> n.equals(serializedFieldName)).count();
            assertEquals(1, matches, () -> String.format("Field \"%s\" of %s model is not handled properly in the corresponding test", serializedFieldName, modelClass.getSimpleName()));
        });
    }

    private static Stream<Field> streamAllNonStaticFields(Class<?> type) {
        if (type == null || Object.class.equals(type)) {
            return Stream.empty();
        }
        return Stream.concat(
                Arrays.stream(type.getDeclaredFields()).filter(f -> (f.getModifiers() & Modifier.STATIC) == 0),
                streamAllNonStaticFields(type.getSuperclass())
        );
    }
}
