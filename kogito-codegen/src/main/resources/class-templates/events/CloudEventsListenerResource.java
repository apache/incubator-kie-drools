package org.kie.kogito.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.spi.HttpRequest;
import org.kie.kogito.events.knative.ce.Printer;
import org.kie.kogito.events.knative.ce.http.Responses;
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
    private Map<String, Emitter<String>> emitters;

    @javax.inject.Inject
    ObjectMapper objectMapper;

    @javax.annotation.PostConstruct
    public void setup() {
        emitters = new HashMap<>();
        objectMapper.registerModule(JsonFormat.getCloudEventJacksonModule());
        /*
         * $repeat$
         * emitters.put("$channel$", $emitter$);
         * $end_repeat$
         */
    }

    @POST()
    @Consumes({MediaType.APPLICATION_JSON, JsonFormat.CONTENT_TYPE})
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response cloudEventListener(CloudEvent event) {
        try {
            LOGGER.debug("CloudEvent received: {}", Printer.beautify(event));
            if (emitters.get(event.getType()) != null) {
                // convert CloudEvent to JSON and send to internal channels
                emitters.get(event.getType()).send(objectMapper.writeValueAsString(event));
                return javax.ws.rs.core.Response.ok().build();
            } else if (emitters.get(event.getSource().toString()) != null) { // try the source instead
                emitters.get(event.getSource().toString()).send(objectMapper.writeValueAsString(event));
                return javax.ws.rs.core.Response.ok().build();
            } else {
                return Responses.channelNotBound(event.getType(), event);
            }
        } catch (Exception ex) {
            return Responses.errorProcessingCloudEvent(ex);
        }
    }

}