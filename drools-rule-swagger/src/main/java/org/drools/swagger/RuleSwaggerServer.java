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
package org.drools.swagger;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import org.drools.swagger.handler.ApiHandler;
import org.drools.swagger.handler.StaticResourceHandler;
import org.drools.swagger.service.RuleExecutionService;
import org.drools.swagger.service.RuleIntrospectionService;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Embedded HTTP server that provides a Swagger-like UI for browsing
 * and test-firing Drools rules.
 *
 * <p>Usage:
 * <pre>
 * KieBase kieBase = ...; // your KieBase
 * RuleSwaggerServer server = new RuleSwaggerServer(kieBase, 8080);
 * server.start();
 * // browse to http://localhost:8080
 * server.stop();
 * </pre>
 */
public class RuleSwaggerServer {

    private static final Logger LOG = LoggerFactory.getLogger(RuleSwaggerServer.class);

    private final HttpServer httpServer;
    private final int port;

    public RuleSwaggerServer(KieBase kieBase, int port) throws IOException {
        this.port = port;
        RuleIntrospectionService introspectionService = new RuleIntrospectionService(kieBase);
        RuleExecutionService executionService = new RuleExecutionService(kieBase);

        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.createContext("/api", new ApiHandler(introspectionService, executionService));
        this.httpServer.createContext("/", new StaticResourceHandler());
    }

    public void start() {
        httpServer.start();
        LOG.info("Drools Rule Swagger UI started at http://localhost:{}", port);
    }

    public void stop() {
        httpServer.stop(1);
        LOG.info("Drools Rule Swagger UI stopped");
    }

    public int getPort() {
        return port;
    }

    /**
     * Standalone launcher. Loads rules from the classpath KieContainer.
     */
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        KieBase kieBase = kieContainer.getKieBase();

        RuleSwaggerServer server = new RuleSwaggerServer(kieBase, port);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        LOG.info("Press Ctrl+C to stop.");
        Thread.currentThread().join();
    }
}
