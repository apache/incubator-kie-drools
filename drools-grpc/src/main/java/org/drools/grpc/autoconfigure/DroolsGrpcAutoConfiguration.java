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

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.grpc.DroolsGrpcServer;
import org.drools.grpc.DroolsRuleServiceImpl;
import org.drools.grpc.security.AuthInterceptor;
import org.drools.grpc.security.TlsConfig;
import org.drools.grpc.session.SessionManager;
import org.drools.grpc.util.FactConverter;
import org.kie.api.KieBase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot auto-configuration that wires up the Drools gRPC server.
 *
 * <p>Activated when {@link KieBase} is on the classpath. All beans are guarded
 * by {@link ConditionalOnMissingBean} so applications can provide their own
 * implementations to override the defaults.
 *
 * <p>Configuration is driven by {@link DroolsGrpcProperties} bound to the
 * {@code drools.grpc.*} namespace.
 */
@Configuration
@ConditionalOnClass(KieBase.class)
@EnableConfigurationProperties(DroolsGrpcProperties.class)
public class DroolsGrpcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SessionManager droolsSessionManager(Map<String, KieBase> kieBases,
                                               DroolsGrpcProperties properties) {
        Map<String, KieBase> resolved = new LinkedHashMap<>();
        if (kieBases.size() == 1) {
            resolved.put(SessionManager.DEFAULT_KIEBASE_NAME, kieBases.values().iterator().next());
        } else {
            resolved.putAll(kieBases);
        }
        return new SessionManager(resolved, properties.getSessionPoolSize());
    }

    @Bean
    @ConditionalOnMissingBean
    public FactConverter droolsFactConverter() {
        return new FactConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    public DroolsRuleServiceImpl droolsRuleService(SessionManager sessionManager,
                                                   FactConverter factConverter) {
        return new DroolsRuleServiceImpl(sessionManager, factConverter);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public DroolsGrpcServer droolsGrpcServer(Map<String, KieBase> kieBases,
                                             DroolsGrpcProperties properties,
                                             SessionManager sessionManager,
                                             FactConverter factConverter) {
        KieBase defaultKieBase = kieBases.values().iterator().next();

        DroolsGrpcServer.Builder builder = DroolsGrpcServer.builder(defaultKieBase)
                .port(properties.getPort())
                .sessionPoolSize(properties.getSessionPoolSize())
                .enableReflection(properties.isReflectionEnabled())
                .enableMetrics(properties.isMetricsEnabled())
                .sessionManager(sessionManager)
                .factConverter(factConverter);

        kieBases.forEach((name, kieBase) -> {
            if (kieBase != defaultKieBase) {
                builder.addKieBase(name, kieBase);
            }
        });

        if (properties.getAuth().isEnabled() && properties.getAuth().getStaticToken() != null) {
            builder.authInterceptor(AuthInterceptor.staticToken(properties.getAuth().getStaticToken()));
        }

        if (properties.getTls().isEnabled()) {
            DroolsGrpcProperties.Tls tls = properties.getTls();
            TlsConfig.Builder tlsBuilder = TlsConfig.builder()
                    .certChainFile(new File(tls.getCertChainPath()))
                    .privateKeyFile(new File(tls.getPrivateKeyPath()));

            if (tls.getTrustCertPath() != null) {
                tlsBuilder.trustCertFile(new File(tls.getTrustCertPath()));
            }

            tlsBuilder.clientAuth(TlsConfig.ClientAuthMode.valueOf(tls.getClientAuth()));
            builder.tlsConfig(tlsBuilder.build());
        }

        return builder.build();
    }
}
