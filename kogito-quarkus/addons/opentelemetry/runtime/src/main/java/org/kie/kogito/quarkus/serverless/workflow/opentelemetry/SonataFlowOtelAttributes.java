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
package org.kie.kogito.quarkus.serverless.workflow.opentelemetry;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.semconv.ResourceAttributes;

/**
 * Centralized constants for OpenTelemetry attribute keys used in SonataFlow.
 * This class provides type-safe access to all attribute keys and prevents
 * duplication of string literals across the codebase.
 */
public final class SonataFlowOtelAttributes {

    private SonataFlowOtelAttributes() {
        // Utility class
    }

    // Span Attributes - SonataFlow specific
    public static final AttributeKey<String> SONATAFLOW_PROCESS_INSTANCE_ID =
            AttributeKey.stringKey("sonataflow.process.instance.id");

    public static final AttributeKey<String> SONATAFLOW_PROCESS_ID =
            AttributeKey.stringKey("sonataflow.process.id");

    public static final AttributeKey<String> SONATAFLOW_PROCESS_VERSION =
            AttributeKey.stringKey("sonataflow.process.version");

    public static final AttributeKey<String> SONATAFLOW_PROCESS_INSTANCE_STATE =
            AttributeKey.stringKey("sonataflow.process.instance.state");

    public static final AttributeKey<String> SONATAFLOW_WORKFLOW_STATE =
            AttributeKey.stringKey("sonataflow.workflow.state");

    public static final AttributeKey<String> SONATAFLOW_TRANSACTION_ID =
            AttributeKey.stringKey("sonataflow.transaction.id");

    // Standard OpenTelemetry Service Attributes (using semantic conventions)
    public static final AttributeKey<String> SERVICE_NAME = ResourceAttributes.SERVICE_NAME;
    public static final AttributeKey<String> SERVICE_VERSION = ResourceAttributes.SERVICE_VERSION;

    // Event Attributes - Process lifecycle
    public static final AttributeKey<String> PROCESS_INSTANCE_ID =
            AttributeKey.stringKey("process.instance.id");

    public static final AttributeKey<String> TRIGGER =
            AttributeKey.stringKey("trigger");

    public static final AttributeKey<String> REFERENCE_ID =
            AttributeKey.stringKey("reference.id");

    public static final AttributeKey<String> OUTCOME =
            AttributeKey.stringKey("outcome");

    public static final AttributeKey<Long> DURATION_MS =
            AttributeKey.longKey("duration.ms");

    public static final AttributeKey<String> EVENT_DESCRIPTION =
            AttributeKey.stringKey("event.description");

    // Error Event Attributes
    public static final AttributeKey<String> ERROR_MESSAGE =
            AttributeKey.stringKey("error.message");

    public static final AttributeKey<String> ERROR_TYPE =
            AttributeKey.stringKey("error.type");

    // Log Event Attributes
    public static final AttributeKey<String> LOG_LEVEL =
            AttributeKey.stringKey("level");

    public static final AttributeKey<String> LOG_LOGGER =
            AttributeKey.stringKey("logger");

    public static final AttributeKey<String> LOG_MESSAGE =
            AttributeKey.stringKey("message");

    public static final AttributeKey<String> LOG_THREAD_NAME =
            AttributeKey.stringKey("thread.name");

    public static final AttributeKey<Long> LOG_THREAD_ID =
            AttributeKey.longKey("thread.id");

    // Event Names
    public static final class Events {
        private Events() {
        }

        public static final String STATE_STARTED = "state.started";
        public static final String STATE_COMPLETED = "state.completed";
        public static final String PROCESS_INSTANCE_START = "process.instance.start";
        public static final String PROCESS_INSTANCE_COMPLETE = "process.instance.complete";
        public static final String PROCESS_INSTANCE_ERROR = "process.instance.error";
        public static final String LOG_MESSAGE = "log.message";
    }

    // Span Name Patterns
    public static final class SpanNames {
        private SpanNames() {
        }

        public static final String SONATAFLOW_PROCESS_PREFIX = "sonataflow.process.";
        public static final String SONATAFLOW_PROCESS_SUFFIX = ".execute";

        public static String createProcessSpanName(String processId) {
            return SONATAFLOW_PROCESS_PREFIX + processId + SONATAFLOW_PROCESS_SUFFIX;
        }
    }

    // Tracker Attribute Helper
    public static final class TrackerAttributes {
        private TrackerAttributes() {
        }

        public static final String SONATAFLOW_TRACKER_PREFIX = "sonataflow.tracker.";

        public static String createTrackerAttributeKey(String trackerKey) {
            if (trackerKey.startsWith("tracker.")) {
                return "sonataflow." + trackerKey;
            }
            return SONATAFLOW_TRACKER_PREFIX + trackerKey;
        }
    }

    // Context Keys - Keys used for storing context in request properties
    public static final class ContextKeys {
        private ContextKeys() {
        }

        public static final String EXTRACTED_CONTEXT = "otel.extracted.context";
        public static final String TRANSACTION_ID = "otel.transaction.id";
    }

    // Request Properties - Keys used for HTTP header context extraction
    public static final class RequestProperties {
        private RequestProperties() {
        }

        public static final String TRANSACTION_ID = "transaction.id";
        public static final String TRACKER_PREFIX = "tracker.";
    }

    // MDC Keys - Keys used in SLF4J MDC for context propagation
    public static final class MDCKeys {
        private MDCKeys() {
        }

        public static final String TRANSACTION_ID = "otel.transaction.id";
        public static final String TRACKER_PREFIX = "otel.tracker.";
    }

    // Headers - HTTP header names for context extraction
    public static final class Headers {
        private Headers() {
        }

        public static final String TRANSACTION_ID = "X-TRANSACTION-ID";
        public static final String TRACKER_PREFIX = "X-TRACKER-";
    }

    // Process States - Process instance state names
    public static final class ProcessStates {
        private ProcessStates() {
        }

        public static final String PENDING = "PENDING";
        public static final String ACTIVE = "ACTIVE";
        public static final String COMPLETED = "COMPLETED";
        public static final String ABORTED = "ABORTED";
        public static final String SUSPENDED = "SUSPENDED";
        public static final String ERROR = "ERROR";
        public static final String UNKNOWN = "UNKNOWN";
    }

    // Error Constants - Default error messages and types
    public static final class ErrorConstants {
        private ErrorConstants() {
        }

        public static final String UNKNOWN_ERROR = "Unknown error";
        public static final String PROCESS_EXECUTION_ERROR = "ProcessExecutionError";
        public static final String WORKFLOW_EXECUTION_EXCEPTION = "WorkflowExecutionException";
        public static final String PROCESS_EXECUTION_FAILED = "Process execution failed";
        public static final String PROCESS_EXECUTION_FAILED_UNCAUGHT = "Process execution failed with uncaught error";
        public static final String SPAN_ERROR_DESCRIPTION = "Process execution failed";
    }

    // Event Descriptions - Standard event description templates
    public static final class EventDescriptions {
        private EventDescriptions() {
        }

        public static final String STATE_STARTED_PREFIX = "State execution started: ";
        public static final String STATE_COMPLETED_PREFIX = "State execution completed: ";
    }

    // Variable Names - Process variable names
    public static final class VariableNames {
        private VariableNames() {
        }

        public static final String ERROR = "error";
    }

    // Trigger Types - Process trigger type values
    public static final class TriggerTypes {
        private TriggerTypes() {
        }

        public static final String HTTP = "http";
    }

    // Process Context Storage - Keys for storing process-specific context
    public static final class ProcessContextStorage {
        private ProcessContextStorage() {
        }

        public static final String ADDED = "added";
    }
}