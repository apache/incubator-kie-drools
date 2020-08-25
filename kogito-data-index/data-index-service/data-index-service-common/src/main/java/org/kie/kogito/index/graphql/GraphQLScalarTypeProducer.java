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

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import graphql.schema.Coercing;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

@ApplicationScoped
public class GraphQLScalarTypeProducer {

    public static ZonedDateTime parseDateTime(String s) {
        try {
            return ZonedDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new CoercingSerializeException("Invalid ISO-8601 value : '" + s + "'. because of : '" + e.getMessage() + "'");
        }
    }

    public String formatDateTime(ZonedDateTime dateTime) {
        try {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(dateTime);
        } catch (DateTimeException e) {
            throw new CoercingSerializeException("Unable to turn TemporalAccessor into OffsetDateTime because of : '" + e.getMessage() + "'.");
        }
    }

    @Produces
    public GraphQLScalarType dateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .description("An ISO-8601 compliant DateTime Scalar")
                .coercing(new Coercing() {
                    @Override
                    public Object serialize(Object input) {
                        ZonedDateTime dateTime;
                        if (input instanceof ZonedDateTime) {
                            dateTime = (ZonedDateTime) input;
                        } else if (input instanceof String) {
                            try {
                                dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(input.toString())), ZoneOffset.UTC);
                            } catch (NumberFormatException ex) {
                                dateTime = parseDateTime(input.toString());
                            }
                        } else if (input instanceof Long) {
                            dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli((Long) input), ZoneOffset.UTC);
                        } else {
                            throw new CoercingSerializeException("Expected something we can convert to 'java.time.OffsetDateTime' but was '" + (input == null ? "null" : input.getClass().getName()) + "'.");
                        }
                        return formatDateTime(dateTime);
                    }

                    @Override
                    public Object parseValue(Object input) {
                        return input == null ? null : parseDateTime((String) input).truncatedTo(ChronoUnit.MILLIS).toInstant().toEpochMilli();
                    }

                    @Override
                    public Object parseLiteral(Object input) {
                        return null;
                    }
                })
                .build();
    }
}
