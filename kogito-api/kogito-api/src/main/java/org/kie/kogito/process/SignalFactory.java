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
package org.kie.kogito.process;

public final class SignalFactory {

    public static <T> org.kie.kogito.process.Signal<T> of(String channel) {
        return new SignalImpl<>(channel, null);
    }

    public static <T> org.kie.kogito.process.Signal<T> of(String channel, T payload) {
        return new SignalImpl<>(channel, payload);
    }

    public static <T> org.kie.kogito.process.Signal<T> of(String channel, T payload, String referenceId) {
        return new SignalImpl<>(channel, payload, referenceId);
    }

}

class SignalImpl<T> implements org.kie.kogito.process.Signal<T> {

    private String channel;
    private T payload;
    private String referenceId;

    protected SignalImpl(String channel, T payload) {
        this.channel = channel;
        this.payload = payload;
    }

    protected SignalImpl(String channel, T payload, String referenceId) {
        this.channel = channel;
        this.payload = payload;
        this.referenceId = referenceId;
    }

    @Override
    public String channel() {
        return channel;
    }

    @Override
    public T payload() {
        return payload;
    }

    @Override
    public String referenceId() {
        return referenceId;
    }

}