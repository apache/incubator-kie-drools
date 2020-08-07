package org.kie.kogito.app;

import io.cloudevents.CloudEvent;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.spi.HttpRequest;
import org.kie.kogito.events.knative.ce.CloudEventConverter;
import org.kie.kogito.events.knative.ce.Printer;
import org.kie.kogito.events.knative.ce.http.ExtMediaType;
import org.kie.kogito.events.knative.ce.http.Responses;
import org.kie.kogito.events.knative.ce.http.RestEasyHttpRequestConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class CloudEventListenerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger("CloudEventListenerResource");
    private RestEasyHttpRequestConverter httpRequestConverter = new RestEasyHttpRequestConverter();
    private Map<String, Emitter<String>> emitters;

    @javax.annotation.PostConstruct
    public void setup() {
        emitters = new HashMap<>();
        /*
         * $repeat$
         * emitters.put("$channel$", $emitter$);
         * $end_repeat$
         */
    }

    @POST()
    @Consumes({MediaType.APPLICATION_JSON, ExtMediaType.CLOUDEVENTS_JSON, MediaType.TEXT_PLAIN})
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response cloudEventListener(@Context HttpRequest request) {
        try {
            final CloudEvent cloudEvent = httpRequestConverter.from(request);
            LOGGER.debug("CloudEvent received: {}", Printer.beautify(cloudEvent));
            if (emitters.get(cloudEvent.getType()) != null) {
                // convert CloudEvent to JSON and send to internal channels
                emitters.get(cloudEvent.getType()).send(CloudEventConverter.toJson(cloudEvent));
                return javax.ws.rs.core.Response.ok().build();
            } else {
                return Responses.channelNotBound(cloudEvent.getType(), cloudEvent);
            }
        } catch (Exception ex) {
            return Responses.errorProcessingCloudEvent(ex);
        }
    }

}