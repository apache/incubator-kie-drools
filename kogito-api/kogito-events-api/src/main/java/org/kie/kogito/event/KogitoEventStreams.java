/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.event;

public class KogitoEventStreams {
    public static final String INCOMING = "kogito_incoming_stream";
    public static final String OUTGOING = "kogito_outgoing_stream";
    public static final String PUBLISHER = "kogito_event_publisher";
    public static final String WORKER_THREAD = "kogito-event-worker";
    public static final String DEFAULT_OUTGOING_BEAN_NAME = OUTGOING + "_eventEmitter";
    public static final String DEFAULT_INCOMING_BEAN_NAME = INCOMING + "_eventReceiver";

    private KogitoEventStreams() {
    }
}
