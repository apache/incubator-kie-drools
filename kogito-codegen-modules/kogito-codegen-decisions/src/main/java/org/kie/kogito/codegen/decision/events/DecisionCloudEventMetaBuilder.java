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
package org.kie.kogito.codegen.decision.events;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.codegen.core.events.CloudEventMetaBuilder;
import org.kie.kogito.event.EventKind;

public class DecisionCloudEventMetaBuilder implements CloudEventMetaBuilder<DecisionCloudEventMeta, List<DMNModel>> {

    public static final String RESPONSE_EVENT_TYPE = "DecisionResponse";
    public static final String RESPONSE_FULL_EVENT_TYPE = "DecisionResponseFull";
    public static final String RESPONSE_ERROR_EVENT_TYPE = "DecisionResponseError";

    @Override
    public Set<DecisionCloudEventMeta> build(List<DMNModel> sourceModel) {
        return sourceModel.stream()
                .flatMap(DecisionCloudEventMetaBuilder::buildMethodDataStreamFromModel)
                .collect(Collectors.toSet());
    }

    private static Stream<DecisionCloudEventMeta> buildMethodDataStreamFromModel(DMNModel model) {
        String source = Optional.of(model.getName())
                .filter(s -> !s.isEmpty())
                .map(DecisionCloudEventMetaBuilder::urlEncodedStringFrom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse("");

        Stream<DecisionCloudEventMeta> modelStream = Stream.of(
                buildMethodDataFromModel(RESPONSE_EVENT_TYPE, source, model.getName()),
                buildMethodDataFromModel(RESPONSE_FULL_EVENT_TYPE, source, model.getName()),
                buildMethodDataFromModel(RESPONSE_ERROR_EVENT_TYPE, source, model.getName()));

        Stream<DecisionCloudEventMeta> decisionServiceStream = model.getDecisionServices().stream()
                .flatMap(ds -> buildMethodDataStreamFromDecisionService(model, ds.getName()));

        return Stream.concat(modelStream, decisionServiceStream);
    }

    private static DecisionCloudEventMeta buildMethodDataFromModel(String type, String source, String modelName) {
        return new DecisionCloudEventMeta(type, source, EventKind.PRODUCED, buildMethodNameChunk(type, modelName, null));
    }

    private static Stream<DecisionCloudEventMeta> buildMethodDataStreamFromDecisionService(DMNModel model, String decisionServiceName) {
        String source = Stream.of(model.getName(), decisionServiceName)
                .filter(s -> s != null && !s.isEmpty())
                .map(DecisionCloudEventMetaBuilder::urlEncodedStringFrom)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("/"));

        return Stream.of(
                buildMethodDataFromDecisionService(RESPONSE_EVENT_TYPE, source, model.getName(), decisionServiceName),
                buildMethodDataFromDecisionService(RESPONSE_FULL_EVENT_TYPE, source, model.getName(), decisionServiceName),
                buildMethodDataFromDecisionService(RESPONSE_ERROR_EVENT_TYPE, source, model.getName(), decisionServiceName));
    }

    private static DecisionCloudEventMeta buildMethodDataFromDecisionService(String type, String source, String modelName, String decisionServiceName) {
        return new DecisionCloudEventMeta(type, source, EventKind.PRODUCED, buildMethodNameChunk(type, modelName, decisionServiceName));
    }

    private static String buildMethodNameChunk(String type, String modelName, String decisionServiceName) {
        return Stream.of(EventKind.PRODUCED.name(), type, modelName, decisionServiceName)
                .filter(s -> s != null && !s.isEmpty())
                .map(DecisionCloudEventMetaFactoryGenerator::toValidJavaIdentifier)
                .collect(Collectors.joining("_"));
    }

    private static Optional<String> urlEncodedStringFrom(String input) {
        return Optional.ofNullable(input)
                .map(i -> {
                    try {
                        return URLEncoder.encode(i, StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
