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
package org.kie.kogito.taskassigning.index.service.client.graphql.date;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.kie.kogito.taskassigning.index.service.client.graphql.Argument;

public abstract class DateArgument<T> implements Argument {

    public static final String TYPE_ID = "DateArgument";

    public enum Condition {
        IS_NULL("isNull"),
        EQUAL("equal"),
        GREATER_THAN("greaterThan"),
        GREATER_THAN_EQUAL("greaterThanEqual"),
        LESS_THAN("lessThan"),
        LESS_THAN_EQUAL("lessThanEqual");

        private final String function;

        Condition(String function) {
            this.function = function;
        }

        public String getFunction() {
            return function;
        }
    }

    protected T value;
    protected Condition condition;

    protected DateArgument(T value, Condition condition) {
        this.value = value;
        this.condition = condition;
    }

    public T getValue() {
        return value;
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public String getTypeId() {
        return TYPE_ID;
    }

    public static String formatDateTime(ZonedDateTime value) {
        return value != null ? DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value) : null;
    }
}
