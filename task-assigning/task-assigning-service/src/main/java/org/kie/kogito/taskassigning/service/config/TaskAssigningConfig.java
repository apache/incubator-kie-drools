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

package org.kie.kogito.taskassigning.service.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.CLIENT_AUTH_PASSWORD;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.CLIENT_AUTH_USER;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_INDEX_SERVER_URL;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_LOADER_PAGE_SIZE;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_LOADER_RETRIES;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.DATA_LOADER_RETRY_INTERVAL_DURATION;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.PUBLISH_WINDOW_SIZE;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.QUARKUS_OIDC_AUTH_SERVER_URL;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.QUARKUS_OIDC_CLIENT_ID;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.QUARKUS_OIDC_CREDENTIALS_SECRET;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties.QUARKUS_OIDC_TENANT_ENABLED;

@ApplicationScoped
public class TaskAssigningConfig {

    private static final String KEYCLOAK_REALMS_SUB_PATH = "/realms/";
    private static final String KEYCLOAK_AUTH_REALMS_SUB_PATH = "/auth/realms/";
    private static final String KEYCLOAK_SERVER_URL_UNEXPECTED_FORMAT_ERROR = "The configuration value for property: " + QUARKUS_OIDC_AUTH_SERVER_URL +
            ", %s doesn't look to be a valid Keycloak authentication domain configuration in the form " +
            "\'https://host:port/auth/realms/{realm}\' where '{realm}' has to be replaced by the name of the Keycloak realm.";

    @Inject
    @ConfigProperty(name = QUARKUS_OIDC_TENANT_ENABLED)
    boolean oidcTenantEnabled;

    @Inject
    @ConfigProperty(name = QUARKUS_OIDC_AUTH_SERVER_URL)
    Optional<URL> oidcAuthServerUrl;

    @Inject
    @ConfigProperty(name = QUARKUS_OIDC_CLIENT_ID)
    Optional<String> oidcClientId;

    @Inject
    @ConfigProperty(name = QUARKUS_OIDC_CREDENTIALS_SECRET)
    Optional<String> oidcCredentialsSecret;

    @Inject
    @ConfigProperty(name = CLIENT_AUTH_USER)
    Optional<String> clientAuthUser;

    @Inject
    @ConfigProperty(name = CLIENT_AUTH_PASSWORD)
    Optional<String> clientAuthPassword;

    @Inject
    @ConfigProperty(name = DATA_INDEX_SERVER_URL)
    URL dataIndexServerUrl;

    @Inject
    @ConfigProperty(name = DATA_LOADER_RETRY_INTERVAL_DURATION, defaultValue = "PT1S")
    Duration dataLoaderRetryInterval;

    @Inject
    @ConfigProperty(name = DATA_LOADER_RETRIES, defaultValue = "5")
    int dataLoaderRetries;

    @Inject
    @ConfigProperty(name = DATA_LOADER_PAGE_SIZE, defaultValue = "3000")
    int dataLoaderPageSize;

    @Inject
    @ConfigProperty(name = PUBLISH_WINDOW_SIZE, defaultValue = "2")
    int publishWindowSize;

    public boolean isOidcTenantEnabled() {
        return oidcTenantEnabled;
    }

    public Optional<URL> getOidcAuthServerUrl() {
        return oidcAuthServerUrl;
    }

    public URL getOidcAuthServerCanonicUrl() {
        String oidcAuthServerUrlString = getOidcAuthServerUrlString();
        String[] splittedValues = getOidcAuthServerUrlString().split(KEYCLOAK_REALMS_SUB_PATH);
        if (splittedValues.length != 2 || splittedValues[1] == null || splittedValues[1].isEmpty() || splittedValues[1].contains("/")) {
            throw new IllegalArgumentException(String.format(KEYCLOAK_SERVER_URL_UNEXPECTED_FORMAT_ERROR, oidcAuthServerUrlString));
        }
        try {
            return new URL(splittedValues[0]);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format(KEYCLOAK_SERVER_URL_UNEXPECTED_FORMAT_ERROR, oidcAuthServerUrlString));
        }
    }

    public String getOidcAuthServerRealm() {
        String oidcAuthServerUrlString = getOidcAuthServerUrlString();
        String[] splittedValues = getOidcAuthServerUrlString().split(KEYCLOAK_AUTH_REALMS_SUB_PATH);
        if (splittedValues.length != 2 || splittedValues[1] == null || splittedValues[1].contains("/")
                || splittedValues[0] == null || splittedValues[0].isEmpty()) {
            throw new IllegalArgumentException(String.format(KEYCLOAK_SERVER_URL_UNEXPECTED_FORMAT_ERROR, oidcAuthServerUrlString));
        }
        return splittedValues[1];
    }

    private String getOidcAuthServerUrlString() {
        return getOidcAuthServerUrl()
                .orElseThrow(() -> new IllegalArgumentException("A configuration value must be set for the property: "
                        + QUARKUS_OIDC_AUTH_SERVER_URL))
                .toString();
    }

    public Optional<String> getOidcClientId() {
        return oidcClientId;
    }

    public Optional<String> getOidcCredentialsSecret() {
        return oidcCredentialsSecret;
    }

    public Optional<String> getClientAuthUser() {
        return clientAuthUser;
    }

    public Optional<String> getClientAuthPassword() {
        return clientAuthPassword;
    }

    public URL getDataIndexServerUrl() {
        return dataIndexServerUrl;
    }

    public boolean isKeycloakSet() {
        return isOidcTenantEnabled();
    }

    public boolean isBasicAuthSet() {
        return !isKeycloakSet() && clientAuthUser.isPresent();
    }

    public Duration getDataLoaderRetryInterval() {
        return dataLoaderRetryInterval;
    }

    public int getDataLoaderRetries() {
        return dataLoaderRetries;
    }

    public int getDataLoaderPageSize() {
        return dataLoaderPageSize;
    }

    public int getPublishWindowSize() {
        return publishWindowSize;
    }

    @Override
    public String toString() {
        return "TaskAssigningConfig{" +
                "oidcTenantEnabled=" + oidcTenantEnabled +
                ", oidcAuthServerUrl=" + oidcAuthServerUrl +
                ", oidcClientId=" + oidcClientId +
                ", oidcCredentialsSecret=" + (oidcCredentialsSecret.isEmpty() ? null : "*****") +
                ", clientAuthUser=" + clientAuthUser +
                ", clientAuthPassword=" + (clientAuthPassword.isEmpty() ? null : "*****") +
                ", dataIndexServerUrl=" + dataIndexServerUrl +
                ", dataLoaderRetryInterval=" + dataLoaderRetryInterval +
                ", dataLoaderRetries=" + dataLoaderRetries +
                ", dataLoaderPageSize=" + dataLoaderPageSize +
                ", publishWindowSize=" + publishWindowSize +
                '}';
    }
}
