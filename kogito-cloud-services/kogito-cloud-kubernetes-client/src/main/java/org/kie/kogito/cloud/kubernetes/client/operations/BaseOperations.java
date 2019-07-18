/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.kubernetes.client.operations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.Request;
import okhttp3.Response;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeClientException;
import org.kie.kogito.cloud.kubernetes.client.KogitoKubeConfig;
import org.kie.kogito.cloud.kubernetes.client.OperationsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all operations
 */
public abstract class BaseOperations implements Operations {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseOperations.class);
    private static final String EMPTY_JSON = "{}";

    private final KogitoKubeConfig clientConfig;

    public BaseOperations(final KogitoKubeConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public final KogitoKubeConfig getClientConfig() {
        return clientConfig;
    }

    private URL doBuildUrl(final String namespace, final Map<String, String> labels) {
        try {
            StringBuilder sb = new StringBuilder(this.buildBaseUrl(namespace));
            if (labels != null) {
                sb.append("?").append(OperationsUtils.LABEL_SELECTOR_PARAM).append("=");
                sb.append(this.buildLabelSelectorParam(labels));
            }
            return new URL(sb.toString());
        } catch (Exception e) {
            throw new KogitoKubeClientException(String.format("Error while trying to build URL for the Service API: '%s'", e.getMessage()), e);
        }
    }

    private String buildLabelSelectorParam(final Map<String, String> labels) {
        if (labels != null) {
            return labels.entrySet()
                         .stream()
                         .map(label -> String.format(label.getValue() == null || label.getValue().isEmpty() ? "%s" : "%s=%s", label.getKey(), label.getValue()))
                         .collect(Collectors.joining(","));
        }
        return "";
    }

    private Response doExecute(final String namespace, final Map<String, String> labels) throws IOException {
        final URL url = this.doBuildUrl(namespace, labels);
        final Request request = new Request.Builder().url(url).build();

        LOGGER.debug("About to query the Kubernetes API with url {} with label selector {} in namespace  '{}'", url, labels, namespace);

        return clientConfig.getHttpClient().newCall(request).execute();
    }

    protected OperationsResponseParser execute(final String namespace, final Map<String, String> labels) {
        try (Response response = this.doExecute(namespace, labels)) {
            LOGGER.debug("Response Headers received from the Kube cluster: {}", response.headers());
            if (response.isSuccessful()) {
                final String data = response.body().string();
                LOGGER.debug("Received response data from Kube API: {}", data);
                return new OperationsResponseParser(data);
            }
            if (response.code() == HttpURLConnection.HTTP_NOT_FOUND) {
                LOGGER.debug("No resources found in namespace '{}' with labels {}", namespace, labels);
                return new OperationsResponseParser(EMPTY_JSON);
            }
            if (response.code() == HttpURLConnection.HTTP_FORBIDDEN || response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                throw new KogitoKubeClientException(String.format("Tried to fetch for resources, got unauthorized/forbidden response: %s. Make sure to correctly set a Service Account with permissions to fetch the resource.",
                                                                  response));
            }
            throw new KogitoKubeClientException(String.format("Error trying to fetch the Kubernetes API. Response is: %s", response));
        } catch (KogitoKubeClientException e) {
            throw e;
        } catch (Exception e) {
            throw new KogitoKubeClientException(String.format("Error trying to fetch the Kubernetes API - '%s: %s'", e.getClass(), e.getMessage()), e);
        }
    }

    /**
     * URL builder for the API calls. Normally composed by the Master URL + Resource Path.
     * 
     * @param namespace
     * @return
     * @throws MalformedURLException
     * @see {@link ServiceOperations#buildBaseUrl(String)}
     */
    protected abstract String buildBaseUrl(final String namespace) throws MalformedURLException;
}
