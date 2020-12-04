package org.kie.kogito.app;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.event.Topic;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;

import org.kie.kogito.event.CloudEventMeta;
import org.kie.kogito.services.event.TopicDiscovery;

@RestController
@RequestMapping("/messaging/topics")
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<List<Topic>>  getTopics() {
        return ResponseEntity.ok(discovery.getTopics(eventsMeta));
    }
}