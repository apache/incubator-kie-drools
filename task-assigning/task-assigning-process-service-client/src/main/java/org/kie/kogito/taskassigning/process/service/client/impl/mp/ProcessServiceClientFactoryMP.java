/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.kie.kogito.taskassigning.process.service.client.AuthenticationCredentials;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClient;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientConfig;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClientFactory;

@ApplicationScoped
public class ProcessServiceClientFactoryMP implements ProcessServiceClientFactory {

    private AuthenticationFilterFactory filterFactory;

    public ProcessServiceClientFactoryMP() {
        //CDI proxying
    }

    @Inject
    public ProcessServiceClientFactoryMP(AuthenticationFilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    @Override
    public ProcessServiceClient newClient(ProcessServiceClientConfig config, AuthenticationCredentials credentials) {
        URL url;
        try {
            url = new URL(config.getServiceUrl());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid serviceUrl: " + config.getServiceUrl(), e);
        }
        ProcessServiceClientRest restClient = RestClientBuilder.newBuilder()
                .baseUrl(url)
                .register(filterFactory.newAuthenticationFilter(credentials))
                .connectTimeout(config.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
                .readTimeout(config.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
                .build(ProcessServiceClientRest.class);
        return new ProcessServiceClientMP(restClient);
    }
}