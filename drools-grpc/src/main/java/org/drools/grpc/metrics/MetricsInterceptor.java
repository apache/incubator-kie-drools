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
package org.drools.grpc.metrics;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import io.grpc.ForwardingServerCall.SimpleForwardingServerCall;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MetricsInterceptor.class);

    private final ConcurrentHashMap<String, LongAdder> callCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> failedCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> totalDurationNanos = new ConcurrentHashMap<>();
    private final AtomicLong activeCalls = new AtomicLong();

    private final MetricsListener listener;

    public interface MetricsListener {
        void onCallStarted(String method);
        void onCallCompleted(String method, Status status, long durationNanos);
    }

    public static class MetricsSnapshot {
        private final Map<String, Long> callCounts;
        private final Map<String, Long> failedCounts;
        private final Map<String, Long> totalDurationNanos;
        private final long activeCalls;

        public MetricsSnapshot(Map<String, Long> callCounts,
                               Map<String, Long> failedCounts,
                               Map<String, Long> totalDurationNanos,
                               long activeCalls) {
            this.callCounts = Collections.unmodifiableMap(callCounts);
            this.failedCounts = Collections.unmodifiableMap(failedCounts);
            this.totalDurationNanos = Collections.unmodifiableMap(totalDurationNanos);
            this.activeCalls = activeCalls;
        }

        public Map<String, Long> getCallCounts() {
            return callCounts;
        }

        public Map<String, Long> getFailedCounts() {
            return failedCounts;
        }

        public Map<String, Long> getTotalDurationNanos() {
            return totalDurationNanos;
        }

        public long getActiveCalls() {
            return activeCalls;
        }
    }

    public MetricsInterceptor() {
        this(null);
    }

    public MetricsInterceptor(MetricsListener listener) {
        this.listener = listener;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        String method = call.getMethodDescriptor().getFullMethodName();
        long startNanos = System.nanoTime();

        callCounts.computeIfAbsent(method, k -> new LongAdder()).increment();
        activeCalls.incrementAndGet();
        logger.debug("gRPC call started: {}", method);

        if (listener != null) {
            listener.onCallStarted(method);
        }

        ServerCall<ReqT, RespT> monitoredCall = new SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void close(Status status, Metadata trailers) {
                if (!status.isOk()) {
                    failedCounts.computeIfAbsent(method, k -> new LongAdder()).increment();
                }
                super.close(status, trailers);
            }
        };

        ServerCall.Listener<ReqT> originalListener = next.startCall(monitoredCall, headers);

        return new SimpleForwardingServerCallListener<ReqT>(originalListener) {
            @Override
            public void onComplete() {
                try {
                    super.onComplete();
                } finally {
                    recordCompletion(method, startNanos, Status.OK);
                }
            }

            @Override
            public void onCancel() {
                try {
                    super.onCancel();
                } finally {
                    recordCompletion(method, startNanos, Status.CANCELLED);
                }
            }
        };
    }

    private void recordCompletion(String method, long startNanos, Status status) {
        long durationNanos = System.nanoTime() - startNanos;
        activeCalls.decrementAndGet();
        totalDurationNanos.computeIfAbsent(method, k -> new LongAdder()).add(durationNanos);
        logger.debug("gRPC call completed: {} in {}ms (status={})",
                method, durationNanos / 1_000_000, status.getCode());

        if (listener != null) {
            listener.onCallCompleted(method, status, durationNanos);
        }
    }

    public MetricsSnapshot getSnapshot() {
        return new MetricsSnapshot(
                snapshotAdderMap(callCounts),
                snapshotAdderMap(failedCounts),
                snapshotAdderMap(totalDurationNanos),
                activeCalls.get());
    }

    public void resetMetrics() {
        callCounts.clear();
        failedCounts.clear();
        totalDurationNanos.clear();
        activeCalls.set(0);
        logger.info("Metrics reset");
    }

    private static Map<String, Long> snapshotAdderMap(ConcurrentHashMap<String, LongAdder> source) {
        return source.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().sum()));
    }
}
