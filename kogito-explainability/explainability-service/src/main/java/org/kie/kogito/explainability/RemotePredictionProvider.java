package org.kie.kogito.explainability;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import org.eclipse.microprofile.context.ThreadContext;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.explainability.models.ExplainabilityRequest;
import org.kie.kogito.explainability.models.ModelIdentifier;
import org.kie.kogito.explainability.models.PredictInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.explainability.ConversionUtils.toOutputList;

public class RemotePredictionProvider implements PredictionProvider {

    private static final Logger LOG = LoggerFactory.getLogger(RemotePredictionProvider.class);

    private final ExplainabilityRequest request;
    private final ThreadContext threadContext;
    private final Executor asyncExecutor;
    private final WebClient client;

    public RemotePredictionProvider(ExplainabilityRequest request, Vertx vertx, ThreadContext threadContext, Executor asyncExecutor) {
        this.request = request;
        URI uri = URI.create(request.getServiceUrl());
        this.client = getClient(vertx, uri);
        this.threadContext = threadContext;
        this.asyncExecutor = asyncExecutor;
    }

    @Override
    public CompletableFuture<List<PredictionOutput>> predictAsync(List<PredictionInput> inputs) {
        return sendPredictRequest(inputs, request.getModelIdentifier());
    }

    protected WebClient getClient(Vertx vertx, URI uri) {
        int port = uri.getPort() != -1 ? uri.getPort() : 80;
        return WebClient.create(vertx, new WebClientOptions()
                .setDefaultHost(uri.getHost())
                .setDefaultPort(port)
                .setSsl("https".equalsIgnoreCase(uri.getScheme()))
                .setLogActivity(true)
        );
    }

    protected PredictionOutput toPredictionOutput(JsonObject mainObj) {
        if (mainObj == null || !mainObj.containsKey("result")) {
            LOG.error("Malformed json {}", mainObj);
            return null;
        }
        List<Output> resultOutputs = toOutputList(mainObj.getJsonObject("result"));
        List<String> resultOutputNames = resultOutputs.stream().map(Output::getName).collect(toList());

        // It's possible that some outputs are missing in the response from the prediction service
        // (e.g. when the generated perturbed inputs don't make sense and a decision is skipped).
        // The explainer, however, may throw exceptions if it can't find all the inputs that were
        // specified in the execution request.
        // Here we take the outputs received from the prediction service and we fill (only if needed)
        // the missing ones with Output objects containing "null" values of type UNDEFINED, to make
        // the explainer happy.
        List<Output> outputs = Stream.concat(
                resultOutputs.stream()
                        .filter(output -> request.getOutputs().containsKey(output.getName())),
                request.getOutputs().keySet().stream()
                        .filter(key -> !resultOutputNames.contains(key))
                        .map(key -> new Output(key, Type.UNDEFINED, new Value<>(null), 1d))
        ).collect(toList());

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

    protected CompletableFuture<List<PredictionOutput>> sendPredictRequest(List<PredictionInput> inputs, ModelIdentifier modelIdentifier) {
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
