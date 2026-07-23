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
package org.kie.kogito.explainability;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.microprofile.context.ThreadContext;
import org.kie.kogito.explainability.api.HasNameValue;
import org.kie.kogito.explainability.api.ModelIdentifier;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.models.PredictInput;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.explainability.ConversionUtils.toOutputList;

public class RemotePredictionProvider implements PredictionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePredictionProvider.class);

    private final ModelIdentifier modelIdentifier;
    private final Collection<? extends HasNameValue<TypedValue>> predictionOutputs;
    private final ThreadContext threadContext;
    private final Executor asyncExecutor;
    private final WebClient client;

    public RemotePredictionProvider(String serviceUrl,
            ModelIdentifier modelIdentifier,
            Collection<? extends HasNameValue<TypedValue>> predictionOutputs,
            Vertx vertx,
            ThreadContext threadContext,
            Executor asyncExecutor) {
        this.modelIdentifier = modelIdentifier;
        this.predictionOutputs = predictionOutputs;
        URI uri = URI.create(serviceUrl);
        this.client = getClient(vertx, uri);
        this.threadContext = threadContext;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public CompletableFuture<List<PredictionOutput>> predictAsync(List<PredictionInput> inputs) {
        return sendPredictRequest(inputs, modelIdentifier);
    }

    protected WebClient getClient(Vertx vertx, URI uri) {
        int port = uri.getPort() != -1 ? uri.getPort() : 80;
        return WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost(uri.getHost())
                .setDefaultPort(port)
                .setSsl("https".equalsIgnoreCase(uri.getScheme()))
                .setLogActivity(true));
    }

    protected PredictionOutput toPredictionOutput(JsonObject mainObj) {
        if (mainObj == null || !mainObj.containsKey("result")) {
            LOG.error("Malformed json {}", mainObj);
            return null;
        }
        List<Output> resultOutputs = toOutputList(mainObj.getJsonObject("result"));
        List<String> resultOutputNames = resultOutputs.stream().map(Output::getName).collect(toList());
        Map<String, TypedValue> mappedOutputs = predictionOutputs.stream().collect(Collectors.toMap(HasNameValue::getName, HasNameValue::getValue));

        // It's possible that some outputs are missing in the response from the prediction service
        // (e.g. when the generated perturbed inputs don't make sense and a decision is skipped).
        // The explainer, however, may throw exceptions if it can't find all the inputs that were
        // specified in the execution request.
        // Here we take the outputs received from the prediction service and we fill (only if needed)
        // the missing ones with Output objects containing "null" values of type UNDEFINED, to make
        // the explainer happy.
        List<Output> outputs = Stream.concat(
                resultOutputs.stream()
                        .filter(output -> mappedOutputs.containsKey(output.getName())),
                mappedOutputs.keySet().stream()
                        .filter(key -> !resultOutputNames.contains(key))
                        .map(key -> new Output(key, Type.UNDEFINED, new Value(null), 1d)))
                .collect(toList());

        return new PredictionOutput(outputs);
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> toMap(List<Feature> features) {
        Map<String, Object> map = new HashMap<>();
        for (Feature f : features) {
            if (Type.COMPOSITE.equals(f.getType())) {
                List<Feature> compositeFeatures = (List<Feature>) f.getValue().getUnderlyingObject();
                Map<String, Object> maps = new HashMap<>();
                for (Feature cf : compositeFeatures) {
                    Map<String, Object> compositeFeatureMap = toMap(List.of(cf));
                    maps.putAll(compositeFeatureMap);
                }
                map.put(f.getName(), maps);
            } else {
                if (Type.UNDEFINED.equals(f.getType())) {
                    Feature underlyingFeature = (Feature) f.getValue().getUnderlyingObject();
                    map.put(f.getName(), toMap(List.of(underlyingFeature)));
                } else {
                    Object underlyingObject = f.getValue().getUnderlyingObject();
                    map.put(f.getName(), underlyingObject);
                }
            }
        }
        if (map.containsKey("context")) {
            map = (Map<String, Object>) map.get("context");
        }
        return map;
    }

    protected CompletableFuture<List<PredictionOutput>> sendPredictRequest(List<PredictionInput> inputs,
            ModelIdentifier modelIdentifier) {
        List<PredictInput> piList = inputs.stream()
                .map(input -> new PredictInput(modelIdentifier, toMap(input.getFeatures())))
                .collect(toList());

        return threadContext.withContextCapture(client.post("/predict")
                .sendJson(piList)
                .subscribeAsCompletionStage())
                .thenApplyAsync(r -> parseRawResult(r.bodyAsJsonArray()), asyncExecutor);
    }

    protected List<PredictionOutput> parseRawResult(JsonArray jsonArray) {
        return jsonArray.stream()
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(this::toPredictionOutput)
                .filter(Objects::nonNull)
                .collect(toList());
    }
}
