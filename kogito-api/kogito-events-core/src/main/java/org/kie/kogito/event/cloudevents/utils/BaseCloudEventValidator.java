/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event.cloudevents.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

abstract class BaseCloudEventValidator {

    private static final Pattern RFC2046 = Pattern.compile("^[a-zA-Z]+/[a-zA-Z]+(?:[+\\-.][a-zA-Z0-9]+){0,10}$");

    protected abstract String getRfc3339Attribute();

    protected abstract String getRfc2046Attribute();

    final void validateCloudEvent(Map<String, Object> cloudEvent) throws InvalidCloudEventException {
        List<String> errors = new ArrayList<>();

        CloudEventUtils.getMissingAttributes(cloudEvent)
                .forEach(missingAttribute -> errors.add("Missing mandatory attribute: " + missingAttribute));

        validateNonEmptyAttributes(cloudEvent, errors);

        validateRfc2046Attributes(cloudEvent, errors);

        validateRfc3339Attributes(cloudEvent, errors);

        if (!errors.isEmpty()) {
            throw new InvalidCloudEventException(errors);
        }
    }

    private static <T> void validateAttribute(Map<String, Object> cloudEvent, String attribute, Predicate<T> validation, String message, Collection<String> errors) {
        @SuppressWarnings("unchecked")
        T value = (T) cloudEvent.get(attribute);
        if (value != null && !validation.test(value)) {
            errors.add(attribute + message);
        }
    }

    /**
     * Validates attributes that must adhere to the format specified in <a href="https://datatracker.ietf.org/doc/html/rfc3339">RFC 3339</a>.
     *
     * @param cloudEvent the CloudEvent
     * @param errors the error list where the found errors should be put
     */
    private void validateRfc3339Attributes(Map<String, Object> cloudEvent, List<String> errors) {
        validateAttribute(
                cloudEvent,
                getRfc3339Attribute(),
                BaseCloudEventValidator::isRfc3339Value,
                " MUST adhere to the format specified in RFC 3339 (https://datatracker.ietf.org/doc/html/rfc3339).",
                errors);
    }

    /**
     * Validates attributes that must adhere to the format specified in <a href="https://datatracker.ietf.org/doc/html/rfc2046">RFC 2046</a>.
     *
     * @param cloudEvent the CloudEvent
     * @param errors the error list where the found errors should be put
     */
    private void validateRfc2046Attributes(Map<String, Object> cloudEvent, List<String> errors) {
        validateAttribute(
                cloudEvent,
                getRfc2046Attribute(),
                BaseCloudEventValidator::isRfc2046Value,
                " MUST adhere to the format specified in RFC 2046 (https://datatracker.ietf.org/doc/html/rfc2046).",
                errors);
    }

    private static boolean isRfc3339Value(String value) {
        try {
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isRfc2046Value(String value) {
        return RFC2046.matcher(value).matches();
    }

    private void validateNonEmptyAttributes(Map<String, Object> cloudEvent, List<String> errors) {
        for (String attribute : getNonEmptyAttributes()) {
            validateAttribute(cloudEvent, attribute, not(""::equals), " must be a non-empty String.", errors);
        }
    }

    protected abstract List<String> getNonEmptyAttributes();
}
