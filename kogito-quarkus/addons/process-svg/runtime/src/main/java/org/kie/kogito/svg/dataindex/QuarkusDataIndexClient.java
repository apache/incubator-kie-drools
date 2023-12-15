/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.svg.dataindex;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.svg.ProcessSVGException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;

@ApplicationScoped
public class QuarkusDataIndexClient implements DataIndexClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusDataIndexClient.class);

    private Vertx vertx;
    private WebClient client;
    private String dataIndexHttpURL;

    @Inject
    public QuarkusDataIndexClient(@ConfigProperty(name = "kogito.dataindex.http.url", defaultValue = "http://localhost:8180") String dataIndexHttpURL,
            Vertx vertx) {
        this.dataIndexHttpURL = dataIndexHttpURL;
        this.vertx = vertx;
    }

    @PostConstruct
    protected void setup() throws MalformedURLException {
        client = WebClient.create(vertx, getWebClientToURLOptions(this.dataIndexHttpURL));
        LOGGER.debug("Creating new instance of web client");
    }

    protected WebClientOptions getWebClientToURLOptions(String targetHttpURL) throws MalformedURLException {
        URL dataIndexURL = new URL(targetHttpURL);
        return new WebClientOptions()
                .setDefaultHost(dataIndexURL.getHost())
                .setDefaultPort((dataIndexURL.getPort() != -1 ? dataIndexURL.getPort() : dataIndexURL.getDefaultPort()))
                .setSsl(dataIndexURL.getProtocol().compareToIgnoreCase("https") == 0);
    }

    @Override
    public List<NodeInstance> getNodeInstancesFromProcessInstance(String processInstanceId, String authHeader) {
        String query = getNodeInstancesQuery(processInstanceId);
        Future<List<NodeInstance>> future = client.post("/graphql")
                .putHeader("Authorization", authHeader)
                .putHeader("content-type", "application/json")
                .sendJson(JsonObject.mapFrom(singletonMap("query", query)))
                .map(response -> {
                    if (response.statusCode() == 200) {
                        return getNodeInstancesFromResponse(response.bodyAsJsonObject());
                    } else {
                        return null;
                    }
                });
        try {
            return future.toCompletionStage().toCompletableFuture().get();
        } catch (Exception e) {
            throw new ProcessSVGException("Exception while trying to get data from Data Index service", e);
        }
    }

    protected List<NodeInstance> getNodeInstancesFromResponse(JsonObject response) {
        JsonArray pInstancesArray = response.getJsonObject("data").getJsonArray("ProcessInstances");
        if (pInstancesArray != null && !pInstancesArray.isEmpty()) {
            List<NodeInstance> nodes = new ArrayList<>();
            JsonArray nodesArray = pInstancesArray.getJsonObject(0).getJsonArray("nodes");
            nodesArray.forEach(node -> {
                JsonObject json = (JsonObject) node;
                nodes.add(new NodeInstance(nonNull(json.getString("exit")), json.getString("definitionId")));
            });
            return nodes;
        } else {
            return emptyList();
        }
    }

}
