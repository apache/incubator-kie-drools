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

package org.kie.kogito.taskassigning.index.service.client.graphql.impl.mp.graphql;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.kie.kogito.taskassigning.auth.AuthenticationCredentials;
import org.kie.kogito.taskassigning.auth.mp.AuthenticationFilterFactory;
import org.kie.kogito.taskassigning.index.service.client.graphql.GraphQLServiceClient;
import org.kie.kogito.taskassigning.index.service.client.graphql.GraphQLServiceClientConfig;
import org.kie.kogito.taskassigning.index.service.client.graphql.GraphQLServiceClientFactory;

@ApplicationScoped
public class GraphQLServiceClientFactoryMP implements GraphQLServiceClientFactory {

    private AuthenticationFilterFactory filterFactory;

    public GraphQLServiceClientFactoryMP() {
        //CDI proxying.
    }

    @Inject
    public GraphQLServiceClientFactoryMP(AuthenticationFilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    @Override
    public GraphQLServiceClient newClient(GraphQLServiceClientConfig config, AuthenticationCredentials credentials) {
        return new GraphQLServiceClientMP(RestClientBuilder.newBuilder()
                                                  .baseUrl(config.getServiceUrl())
                                                  .register(filterFactory.newAuthenticationFilter(credentials))
                                                  .connectTimeout(config.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
                                                  .readTimeout(config.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
                                                  .build(GraphQLServiceClientRest.class));
    }
}
