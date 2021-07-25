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
package org.kie.kogito.codegen.openapi.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents an OpenAPISpec resource defined in the given file.
 * For example, for Serverless Workflows, this resource file can be found in the Function definitions.
 *
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#Using-Functions-For-RESTful-Service-Invocations">Using Functions For RESTful Service Invocations</a>
 */
public class OpenApiSpecDescriptor {

    private static final String REGEX_NO_EXT = "[.][^.]+$";

    private final String resourceName;
    private final String id;
    private final URI uri;
    private final Set<OpenApiClientOperation> requiredOperations;

    public OpenApiSpecDescriptor(final String resource) {
        try {
            this.uri = new URI(resource);
            this.resourceName = Paths.get(this.uri.getPath()).getFileName().toString();
            this.id = generateId(this.uri.toString(), resourceName);
            this.requiredOperations = new HashSet<>();
        } catch (URISyntaxException e) {
            throw new OpenApiClientException("Fail to parse given resource into URI" + resource, e);
        }
    }

    public OpenApiSpecDescriptor(final String resource, final String requiredOperationId) {
        this(resource);
        this.requiredOperations.add(new OpenApiClientOperation(requiredOperationId));
    }

    /**
     * Generates the same id for the same resource
     */
    private static String generateId(final String resourceUri, final String resourceName) {
        return Base64.getEncoder().encodeToString(resourceUri.getBytes(StandardCharsets.UTF_8)) + "_" +
                resourceName.replaceFirst(REGEX_NO_EXT, "");
    }

    /**
     * Gets the resolved resource name as defined in the source. It defaults to the file name (includes extension).
     *
     * @return the given resource name as defined in the source.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * The parsed URI file as given by the definition source.
     * For example, in Serveless Workflow this is the URI defined in Functions#operation field.
     *
     * @return the given resolved URI as defined in the source.
     */
    public URI getURI() {
        return uri;
    }

    /**
     * Identification for the specification file.
     *
     * @return the given id
     */
    public String getId() {
        return id;
    }

    public Set<OpenApiClientOperation> getRequiredOperations() {
        return Collections.unmodifiableSet(this.requiredOperations);
    }

    public void addRequiredOperations(final Set<OpenApiClientOperation> operations) {
        this.requiredOperations.addAll(operations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OpenApiSpecDescriptor that = (OpenApiSpecDescriptor) o;
        return Objects.equals(resourceName, that.resourceName) && Objects.equals(uri, that.uri) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceName, uri, id);
    }

    @Override
    public String toString() {
        return "OpenAPISpecResource{" +
                "resourceName='" + resourceName + '\'' +
                ", uri=" + uri +
                ", id='" + id + '\'' +
                '}';
    }
}
