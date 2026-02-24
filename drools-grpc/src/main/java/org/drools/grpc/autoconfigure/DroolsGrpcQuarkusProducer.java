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
package org.drools.grpc.autoconfigure;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.drools.grpc.DroolsGrpcServer;
import org.drools.grpc.DroolsRuleServiceImpl;
import org.drools.grpc.session.SessionManager;
import org.drools.grpc.util.FactConverter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quarkus CDI producer that wires up the Drools gRPC server.
 *
 * <p>This class relies on Quarkus and Jakarta CDI annotations that are optional
 * dependencies. If Quarkus is not on the classpath, the class simply won't load.
 */
@ApplicationScoped
public class DroolsGrpcQuarkusProducer {

    private static final Logger log = LoggerFactory.getLogger(DroolsGrpcQuarkusProducer.class);

    @Inject
    KieBase kieBase;

    @ConfigProperty(name = "drools.grpc.port", defaultValue = "50051")
    int port;

    @ConfigProperty(name = "drools.grpc.session-pool-size", defaultValue = "10")
    int sessionPoolSize;

    @ConfigProperty(name = "drools.grpc.reflection-enabled", defaultValue = "true")
    boolean reflectionEnabled;

    @ConfigProperty(name = "drools.grpc.metrics-enabled", defaultValue = "true")
    boolean metricsEnabled;

    private volatile DroolsGrpcServer grpcServer;

    @Produces
    @ApplicationScoped
    public SessionManager sessionManager() {
        return new SessionManager(kieBase, sessionPoolSize);
    }

    @Produces
    @ApplicationScoped
    public FactConverter factConverter() {
        return new FactConverter();
    }

    @Produces
    @ApplicationScoped
    public DroolsRuleServiceImpl droolsRuleService(SessionManager sessionManager, FactConverter factConverter) {
        return new DroolsRuleServiceImpl(sessionManager, factConverter);
    }

    @Produces
    @ApplicationScoped
    public DroolsGrpcServer droolsGrpcServer(SessionManager sessionManager, FactConverter factConverter) {
        return DroolsGrpcServer.builder(kieBase)
                .port(port)
                .sessionPoolSize(sessionPoolSize)
                .sessionManager(sessionManager)
                .factConverter(factConverter)
                .enableReflection(reflectionEnabled)
                .enableMetrics(metricsEnabled)
                .build();
    }

    void onStart(@Observes StartupEvent event) {
        try {
            grpcServer = droolsGrpcServer(sessionManager(), factConverter());
            grpcServer.start();
            log.info("Drools gRPC server started on port {} (reflection={}, metrics={})",
                    port, reflectionEnabled, metricsEnabled);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start Drools gRPC server", e);
        }
    }

    void onStop(@Observes ShutdownEvent event) {
        if (grpcServer != null) {
            try {
                grpcServer.stop();
                log.info("Drools gRPC server stopped");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted while stopping Drools gRPC server");
            }
        }
    }
}
