/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.events.knative.ce.http;

import io.cloudevents.CloudEvent;
import org.kie.kogito.events.knative.ce.Printer;

import javax.ws.rs.core.Response;

/**
 * Utility class to create responses for CloudEvent processing over HTTP
 */
public final class Responses {

    private static final String ERROR_PROCESSING = "Failed to process HttpRequest into a CloudEvent format";
    private static final String ERROR_CHANNEL_NOT_BOUND = "Channel '%s' not bound, impossible to retransmit CloudEvent internally: %s";

    private Responses() {
    }

    public static Response errorProcessingCloudEvent(Throwable cause) {
        return Response.
                status(Response.Status.BAD_REQUEST).
                entity(new ResponseError(ERROR_PROCESSING, cause)).
                build();
    }

    public static Response channelNotBound(String channelName, CloudEvent cloudEvent) {
        return Response.
                serverError().
                entity(new ResponseError(String.format(ERROR_CHANNEL_NOT_BOUND, channelName, Printer.beautify(cloudEvent))))
                .build();
    }

}
