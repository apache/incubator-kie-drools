package org.kie.kogito.app;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.event.cloudevents.CloudEventMeta;
import org.kie.kogito.event.EventKind;

public class DecisionCloudEventMetaFactory {

    ConfigBean config;

    private CloudEventMeta buildCloudEventMeta(String type, String sourceSuffix, EventKind kind) {
        String source = kind == EventKind.PRODUCED
                ? Stream.of(config.getServiceUrl(), sourceSuffix)
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.joining("/"))
                : "";
        return new CloudEventMeta(type, source, kind);
    }

    public CloudEventMeta buildCloudEventMeta_CONSUMED_DecisionRequest() {
        return new CloudEventMeta("DecisionRequest", "", EventKind.CONSUMED);
    }

    public CloudEventMeta buildCloudEventMeta_PRODUCED_DecisionResponseError_UnknownModel() {
        String source = Optional.of(config.getServiceUrl()).filter(s -> s != null && !s.isEmpty()).orElse("__UNKNOWN_SOURCE__");
        return new CloudEventMeta("DecisionResponseError", source, EventKind.PRODUCED);
    }

    public CloudEventMeta buildCloudEventMeta_$methodName$() {
        return buildCloudEventMeta($type$, $source$, $kind$);
    }
}
