/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.util.Objects;
import java.util.Optional;

final class KnativeServiceIdentifier {

    private final String namespace;

    private final String name;

    KnativeServiceIdentifier(String url) {
        validateUrl(url);

        String[] splitUrl = url.split("/");

        if (splitUrl.length == 1) {
            namespace = null;
            name = splitUrl[0];
        } else {
            namespace = splitUrl[0];
            name = splitUrl[1];
        }
    }

    private static void validateUrl(String url) {
        Objects.requireNonNull(url, "The Knative service URL is required.");

        if (!containsNamespaceAndService(url) && !containsOnlyService(url)) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
    }

    private static boolean containsNamespaceAndService(String url) {
        if (url.contains("/")) {
            String[] split = url.split("/");
            return split.length == 2 && split[0].trim().length() > 0 && split[1].length() > 0;
        }
        return false;
    }

    private static boolean containsOnlyService(String url) {
        return !url.contains("/") && url.trim().length() > 0;
    }

    String getName() {
        return name;
    }

    Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }
}
