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

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResponsesTest {

    @Test
    void errorProcessingCloudEvent() {
        final Response response = Responses.errorProcessingCloudEvent(new IllegalArgumentException("Fail!"));
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(ResponseError.class);
    }

    @Test
    void channelNotBound() {
        final Response response = Responses.channelNotBound("MyChannel", null);
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(ResponseError.class);
    }
}