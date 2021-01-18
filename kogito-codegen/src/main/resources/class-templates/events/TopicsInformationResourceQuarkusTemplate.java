package org.kie.kogito.app;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kie.kogito.event.CloudEventMeta;
import org.kie.kogito.event.TopicDiscovery;

@Path("/messaging/topics")
public class TopicsInformationResource {

    TopicDiscovery discovery;

    private List<CloudEventMeta> eventsMeta;

    public TopicsInformationResource() {
        eventsMeta = new ArrayList<>();
        /*
         * $repeat$
         * eventsMeta.add(new CloudEventMeta("$type$", "$source$", $kind$));
         * $end_repeat$
         */
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response getTopics() {
        return javax.ws.rs.core.Response.ok(discovery.getTopics(eventsMeta)).build();
    }
}