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
package org.drools.grpc.security;

import java.io.File;

import javax.net.ssl.SSLException;

import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for TLS and mutual TLS (mTLS) on a gRPC server.
 *
 * <p>Usage:
 * <pre>{@code
 *   TlsConfig tls = TlsConfig.builder()
 *       .certChainFile(new File("server.crt"))
 *       .privateKeyFile(new File("server.key"))
 *       .trustCertFile(new File("ca.crt"))
 *       .clientAuth(TlsConfig.ClientAuthMode.REQUIRE)
 *       .build();
 *
 *   NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(50051);
 *   tls.applySslContext(serverBuilder);
 * }</pre>
 */
public class TlsConfig {

    private static final Logger log = LoggerFactory.getLogger(TlsConfig.class);

    public enum ClientAuthMode {
        NONE,
        OPTIONAL,
        REQUIRE
    }

    private final File certChainFile;
    private final File privateKeyFile;
    private final File trustCertFile;
    private final ClientAuthMode clientAuth;

    private TlsConfig(Builder builder) {
        this.certChainFile = builder.certChainFile;
        this.privateKeyFile = builder.privateKeyFile;
        this.trustCertFile = builder.trustCertFile;
        this.clientAuth = builder.clientAuth;
    }

    public static Builder builder() {
        return new Builder();
    }

    public File getCertChainFile() {
        return certChainFile;
    }

    public File getPrivateKeyFile() {
        return privateKeyFile;
    }

    public File getTrustCertFile() {
        return trustCertFile;
    }

    public ClientAuthMode getClientAuth() {
        return clientAuth;
    }

    /**
     * Returns {@code true} if a trust certificate is configured, indicating
     * that mutual TLS client verification is enabled.
     */
    public boolean isMtlsEnabled() {
        return trustCertFile != null;
    }

    /**
     * Applies this TLS configuration to the given server builder.
     * The builder must be a {@link NettyServerBuilder} instance.
     *
     * @throws IllegalArgumentException if the builder is not a {@link NettyServerBuilder}
     * @throws SSLException if the SSL context cannot be constructed
     */
    public void applySslContext(ServerBuilder<?> serverBuilder) throws SSLException {
        if (!(serverBuilder instanceof NettyServerBuilder)) {
            throw new IllegalArgumentException(
                    "TLS configuration requires a NettyServerBuilder, got " + serverBuilder.getClass().getName());
        }

        NettyServerBuilder nettyBuilder = (NettyServerBuilder) serverBuilder;

        SslContextBuilder sslBuilder = SslContextBuilder.forServer(certChainFile, privateKeyFile);

        if (trustCertFile != null) {
            sslBuilder.trustManager(trustCertFile);
            sslBuilder.clientAuth(toNettyClientAuth(clientAuth));
            log.info("Configuring mTLS with client auth={}", clientAuth);
        } else {
            sslBuilder.clientAuth(ClientAuth.NONE);
            log.info("Configuring TLS (server-side only)");
        }

        nettyBuilder.sslContext(GrpcSslContexts.configure(sslBuilder).build());
    }

    private static ClientAuth toNettyClientAuth(ClientAuthMode mode) {
        switch (mode) {
            case OPTIONAL:
                return ClientAuth.OPTIONAL;
            case REQUIRE:
                return ClientAuth.REQUIRE;
            case NONE:
            default:
                return ClientAuth.NONE;
        }
    }

    public static class Builder {

        private File certChainFile;
        private File privateKeyFile;
        private File trustCertFile;
        private ClientAuthMode clientAuth = ClientAuthMode.NONE;

        private Builder() {}

        public Builder certChainFile(File certChainFile) {
            this.certChainFile = certChainFile;
            return this;
        }

        public Builder privateKeyFile(File privateKeyFile) {
            this.privateKeyFile = privateKeyFile;
            return this;
        }

        /**
         * Sets the trusted CA certificate for verifying client certificates (mTLS).
         */
        public Builder trustCertFile(File trustCertFile) {
            this.trustCertFile = trustCertFile;
            return this;
        }

        public Builder clientAuth(ClientAuthMode clientAuth) {
            this.clientAuth = clientAuth;
            return this;
        }

        public TlsConfig build() {
            if (certChainFile == null) {
                throw new IllegalStateException("certChainFile is required");
            }
            if (privateKeyFile == null) {
                throw new IllegalStateException("privateKeyFile is required");
            }
            return new TlsConfig(this);
        }
    }
}
