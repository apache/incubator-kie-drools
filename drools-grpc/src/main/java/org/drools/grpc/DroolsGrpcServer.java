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
package org.drools.grpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.drools.grpc.metrics.MetricsInterceptor;
import org.drools.grpc.security.AuthInterceptor;
import org.drools.grpc.security.TlsConfig;
import org.drools.grpc.session.SessionManager;
import org.drools.grpc.util.FactConverter;
import org.kie.api.KieBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standalone gRPC server that exposes Drools KieBases for remote rule evaluation.
 *
 * <p>Supports multiple named KieBases, TLS/mTLS, token-based authentication,
 * and pluggable metrics interceptors.
 *
 * <p>Usage:
 * <pre>{@code
 *   DroolsGrpcServer server = DroolsGrpcServer.builder(kieBase)
 *       .port(50051)
 *       .addKieBase("fraud", fraudKieBase)
 *       .tlsConfig(TlsConfig.builder()
 *           .certChainFile(new File("server.crt"))
 *           .privateKeyFile(new File("server.key"))
 *           .build())
 *       .authInterceptor(AuthInterceptor.staticToken("my-secret"))
 *       .enableMetrics(true)
 *       .build();
 *   server.start();
 * }</pre>
 */
public class DroolsGrpcServer {

    private static final Logger log = LoggerFactory.getLogger(DroolsGrpcServer.class);

    private final Server server;
    private final SessionManager sessionManager;
    private final MetricsInterceptor metricsInterceptor;

    private DroolsGrpcServer(Server server, SessionManager sessionManager,
                             MetricsInterceptor metricsInterceptor) {
        this.server = server;
        this.sessionManager = sessionManager;
        this.metricsInterceptor = metricsInterceptor;
    }

    public static Builder builder(KieBase defaultKieBase) {
        return new Builder(defaultKieBase);
    }

    public void start() throws IOException {
        server.start();
        log.info("Drools gRPC server started on port {}", server.getPort());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down Drools gRPC server...");
            try {
                DroolsGrpcServer.this.stop();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public void stop() throws InterruptedException {
        sessionManager.shutdown();
        server.shutdown();
        if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
            server.shutdownNow();
        }
        log.info("Drools gRPC server stopped");
    }

    public void blockUntilShutdown() throws InterruptedException {
        server.awaitTermination();
    }

    public int getPort() {
        return server.getPort();
    }

    public boolean isRunning() {
        return !server.isShutdown() && !server.isTerminated();
    }

    /**
     * Returns the metrics interceptor if metrics are enabled, or {@code null}.
     */
    public MetricsInterceptor getMetricsInterceptor() {
        return metricsInterceptor;
    }

    public static class Builder {

        private final Map<String, KieBase> kieBases = new LinkedHashMap<>();
        private int port = 50051;
        private int sessionPoolSize = 10;
        private boolean enableReflection = true;
        private FactConverter factConverter;
        private SessionManager sessionManager;
        private TlsConfig tlsConfig;
        private AuthInterceptor authInterceptor;
        private boolean enableMetrics = false;
        private MetricsInterceptor.MetricsListener metricsListener;
        private final List<ServerInterceptor> customInterceptors = new ArrayList<>();

        private Builder(KieBase defaultKieBase) {
            this.kieBases.put(SessionManager.DEFAULT_KIEBASE_NAME, defaultKieBase);
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Registers an additional named KieBase for multi-KieBase routing.
         * Clients route to it by setting {@code kie_base_name} in requests.
         */
        public Builder addKieBase(String name, KieBase kieBase) {
            this.kieBases.put(name, kieBase);
            return this;
        }

        public Builder sessionPoolSize(int poolSize) {
            this.sessionPoolSize = poolSize;
            return this;
        }

        public Builder enableReflection(boolean enable) {
            this.enableReflection = enable;
            return this;
        }

        public Builder factConverter(FactConverter factConverter) {
            this.factConverter = factConverter;
            return this;
        }

        public Builder sessionManager(SessionManager sessionManager) {
            this.sessionManager = sessionManager;
            return this;
        }

        public Builder tlsConfig(TlsConfig tlsConfig) {
            this.tlsConfig = tlsConfig;
            return this;
        }

        public Builder authInterceptor(AuthInterceptor authInterceptor) {
            this.authInterceptor = authInterceptor;
            return this;
        }

        public Builder enableMetrics(boolean enable) {
            this.enableMetrics = enable;
            return this;
        }

        public Builder metricsListener(MetricsInterceptor.MetricsListener listener) {
            this.metricsListener = listener;
            return this;
        }

        public Builder addInterceptor(ServerInterceptor interceptor) {
            this.customInterceptors.add(interceptor);
            return this;
        }

        public DroolsGrpcServer build() {
            SessionManager sm = sessionManager != null
                    ? sessionManager
                    : new SessionManager(kieBases, sessionPoolSize);
            FactConverter fc = factConverter != null ? factConverter : new FactConverter();
            DroolsRuleServiceImpl service = new DroolsRuleServiceImpl(sm, fc);

            ServerBuilder<?> serverBuilder = tlsConfig != null
                    ? NettyServerBuilder.forPort(port)
                    : ServerBuilder.forPort(port);

            if (tlsConfig != null) {
                try {
                    tlsConfig.applySslContext(serverBuilder);
                } catch (SSLException e) {
                    throw new RuntimeException("Failed to configure TLS", e);
                }
            }

            MetricsInterceptor metrics = null;
            if (enableMetrics) {
                metrics = new MetricsInterceptor(metricsListener);
                serverBuilder.intercept(metrics);
            }

            if (authInterceptor != null) {
                serverBuilder.intercept(authInterceptor);
            }

            for (ServerInterceptor interceptor : customInterceptors) {
                serverBuilder.intercept(interceptor);
            }

            serverBuilder.addService(service);

            if (enableReflection) {
                serverBuilder.addService(ProtoReflectionService.newInstance());
            }

            return new DroolsGrpcServer(serverBuilder.build(), sm, metrics);
        }
    }
}
