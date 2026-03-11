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

import java.util.function.Function;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A gRPC {@link ServerInterceptor} that enforces token-based authentication.
 *
 * <p>Extracts a Bearer token from the {@code authorization} metadata header,
 * validates it via a configurable function, and optionally resolves the
 * authenticated user identity for downstream handlers.
 *
 * <p>Usage:
 * <pre>{@code
 *   ServerBuilder.forPort(50051)
 *       .intercept(AuthInterceptor.bearerToken(token -> myValidator.isValid(token)))
 *       .addService(myService)
 *       .build();
 * }</pre>
 */
public class AuthInterceptor implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    private static final String BEARER_PREFIX = "Bearer ";

    public static final Context.Key<String> AUTHENTICATED_USER =
            Context.key("authenticated-user");

    static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final Function<String, Boolean> tokenValidator;
    private final Function<String, String> identityExtractor;

    /**
     * @param tokenValidator    returns {@code true} if the token is valid
     * @param identityExtractor extracts a user identity string from a valid token, or {@code null} to skip
     */
    public AuthInterceptor(Function<String, Boolean> tokenValidator,
                           Function<String, String> identityExtractor) {
        this.tokenValidator = tokenValidator;
        this.identityExtractor = identityExtractor;
    }

    public AuthInterceptor(Function<String, Boolean> tokenValidator) {
        this(tokenValidator, null);
    }

    /**
     * Creates an interceptor that validates tokens with the supplied function.
     */
    public static AuthInterceptor bearerToken(Function<String, Boolean> validator) {
        return new AuthInterceptor(validator);
    }

    /**
     * Creates an interceptor that accepts only a single static token value.
     */
    public static AuthInterceptor staticToken(String expectedToken) {
        return new AuthInterceptor(token -> expectedToken.equals(token));
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String authHeader = headers.get(AUTHORIZATION_KEY);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.debug("Rejecting call to {}: missing or malformed Bearer token",
                    call.getMethodDescriptor().getFullMethodName());
            call.close(Status.UNAUTHENTICATED.withDescription("Missing or invalid Bearer token"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        Boolean valid;
        try {
            valid = tokenValidator.apply(token);
        } catch (Exception e) {
            log.warn("Token validation threw an exception", e);
            call.close(Status.UNAUTHENTICATED.withDescription("Token validation failed"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        if (valid == null || !valid) {
            log.debug("Rejecting call to {}: token validation failed",
                    call.getMethodDescriptor().getFullMethodName());
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid token"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        Context ctx = Context.current();
        if (identityExtractor != null) {
            try {
                String identity = identityExtractor.apply(token);
                if (identity != null) {
                    ctx = ctx.withValue(AUTHENTICATED_USER, identity);
                }
            } catch (Exception e) {
                log.warn("Identity extraction failed, proceeding without user context", e);
            }
        }

        return Contexts.interceptCall(ctx, call, headers, next);
    }
}
