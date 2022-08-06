/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.health.knative;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.function.UnaryOperator;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Liveness
@ApplicationScoped
public class KSinkInjectionHealthCheck implements HealthCheck {

    private static final Logger LOGGER = LoggerFactory.getLogger(KSinkInjectionHealthCheck.class);
    public static final String K_SINK = "K_SINK";

    private final UnaryOperator<String> envReader;

    public KSinkInjectionHealthCheck() {
        this(System::getenv);
    }

    // facilitates testing.
    KSinkInjectionHealthCheck(UnaryOperator<String> envReader) {
        this.envReader = envReader;
    }

    @Override
    public HealthCheckResponse call() {
        final HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("K_SINK environment variable injection check");
        final String sinkURL = envReader.apply(K_SINK);
        String reason;
        Exception cause = null;
        if (sinkURL == null || sinkURL.isEmpty()) {
            reason = K_SINK + " variable not set in this environment";
            LOGGER.warn("{}. Returning not healthy.", reason);
        } else {
            try {
                final URI uri = new URI(sinkURL);
                final InetAddress address = InetAddress.getByName(uri.getHost());
                if (address != null) {
                    responseBuilder.up();
                    return responseBuilder.build();
                } else {
                    reason = "Impossible to resolve host: " + uri.getHost() + " for URL: " + sinkURL;
                    LOGGER.warn("{}. Check if this host can be resolved from this environment. Returning not healthy.", reason);
                }
            } catch (UnknownHostException e) {
                reason = "Failed to lookup address: " + sinkURL;
                cause = e;
                LOGGER.warn("{}. Returning not healthy.", reason);
            } catch (URISyntaxException e) {
                reason = "The " + K_SINK + " URL syntax is invalid: " + sinkURL;
                cause = e;
                LOGGER.warn(reason + ". Returning not healthy.", e);
            }
        }
        return responseBuilder.withData("reason", buildMessage(reason, cause)).down().build();
    }

    private static String buildMessage(String reason, Exception cause) {
        if (cause != null) {
            return reason + ". " + cause.getMessage();
        }
        return reason + ".";
    }
}
