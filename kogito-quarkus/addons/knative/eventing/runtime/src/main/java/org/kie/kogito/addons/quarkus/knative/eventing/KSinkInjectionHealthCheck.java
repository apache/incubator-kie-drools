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
package org.kie.kogito.addons.quarkus.knative.eventing;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;

import static org.kie.kogito.addons.quarkus.knative.eventing.KnativeEventingConfigSource.K_SINK;

/**
 * Checks if the K_SINK variable is injected in the environment.
 * In the cloud this is relevant for the underlying HTTP connector to send events to a Knative Sink.
 * Having a health check, would hold the pod to start until the Sink is injected, avoiding the application to produce events to nowhere.
 *
 * @see <a href="https://knative.dev/docs/eventing/sinks/">Knative - About Sinks</a>
 */
@Liveness
@ApplicationScoped
public class KSinkInjectionHealthCheck implements HealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(KSinkInjectionHealthCheck.class);

    public static final String CONFIG_ALIAS = "org.kie.kogito.addons.knative.eventing.health-enabled";

    public static final String NAME = "K_SINK environment variable injection check";

    @Override
    public HealthCheckResponse call() {
        final HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named(NAME);
        final String sinkURL = System.getenv(K_SINK);
        if ("".equals(sinkURL) || sinkURL == null) {
            LOGGER.warn(K_SINK + " variable not set in this environment. Returning not healthy.");
        } else {
            try {
                final URI uri = new URI(sinkURL);
                final InetAddress address = InetAddress.getByName(uri.getHost());
                if (address != null) {
                    responseBuilder.up();
                    return responseBuilder.build();
                } else {
                    LOGGER.warn("Impossible to resolve host " + uri.getHost() + " for URL " + sinkURL + ". Check if this host can resolve from this environment. Returning not healthy.");
                }
            } catch (UnknownHostException e) {
                LOGGER.warn("Failed to lookup address " + sinkURL + ". Returning not healthy.");
            } catch (URISyntaxException e) {
                LOGGER.warn("The " + K_SINK + " URL syntax is invalid: " + sinkURL + ". Returning not healthy.", e);
            }
        }

        responseBuilder.down();
        return responseBuilder.build();
    }

}
