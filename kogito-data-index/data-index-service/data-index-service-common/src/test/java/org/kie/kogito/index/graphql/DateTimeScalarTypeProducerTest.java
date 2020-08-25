/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.graphql;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DateTimeScalarTypeProducerTest {

    GraphQLScalarType dateTimeScalar;

    public DateTimeScalarTypeProducerTest() {
        GraphQLScalarTypeProducer producer = new GraphQLScalarTypeProducer();
        dateTimeScalar = producer.dateTimeScalar();
    }

    @Test
    public void testParseValue() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        assertThat(dateTimeScalar.getCoercing().parseValue(null)).isNull();
    }

    @Test
    public void testParseLiteral() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        assertThat(dateTimeScalar.getCoercing().parseLiteral(null)).isNull();
    }

    @Test
    public void testSerializeNull() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        try {
            dateTimeScalar.getCoercing().serialize(null);
            fail("Method should throw CoercingSerializeException");
        } catch (CoercingSerializeException ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected something we can convert to 'java.time.OffsetDateTime' but was 'null'.");
        }
    }

    @Test
    public void testSerializeInvalidType() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        try {
            dateTimeScalar.getCoercing().serialize(1);
            fail("Method should throw CoercingSerializeException");
        } catch (CoercingSerializeException ex) {
            assertThat(ex.getMessage()).isEqualTo("Expected something we can convert to 'java.time.OffsetDateTime' but was 'java.lang.Integer'.");
        }
    }

    @Test
    public void testSerializeZonedDateTime() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        ZonedDateTime time = ZonedDateTime.now();
        String result = (String) dateTimeScalar.getCoercing().serialize(time);
        assertThat(result).isEqualTo(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(time));
    }

    @Test
    public void testSerializeMillisAsString() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        ZonedDateTime time = ZonedDateTime.now();
        String result = (String) dateTimeScalar.getCoercing().serialize(String.valueOf(time.toInstant().toEpochMilli()));
        assertThat(result).isEqualTo(time.withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }

    @Test
    public void testSerializeInvalidString() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        try {
            dateTimeScalar.getCoercing().serialize("test");
            fail("Method should throw CoercingSerializeException");
        } catch (CoercingSerializeException ex) {
            assertThat(ex.getMessage()).isEqualTo("Invalid ISO-8601 value : 'test'. because of : 'Text 'test' could not be parsed at index 0'");
        }
    }

    @Test
    public void testSerializeString() {
        assertThat(dateTimeScalar.getName()).isEqualTo("DateTime");
        String result = (String) dateTimeScalar.getCoercing().serialize("2019-08-20T19:26:02.092+00:00");
        assertThat(result).isEqualTo("2019-08-20T19:26:02.092Z");
    }
}
