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
package org.kie.kogito.quarkus.workflows;

import java.util.Map;

import org.apache.groovy.util.Maps;
import org.kie.kogito.test.utils.SocketUtils;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class GrpcServerPortResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        String port = Integer.toString(SocketUtils.findAvailablePort());
        return Maps.of("quarkus.grpc.clients.Greeter.port", port,
                "quarkus.grpc.server.port", port,
                "quarkus.grpc.server.test-port", port);
    }

    @Override
    public void stop() {
    }
}
