/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process.impl;

import org.kie.kogito.process.Signal;

public final class Sig<T> implements Signal<T> {

    private final String channel;
    private final T payload;
    private String referenceId;

    public static <T> org.kie.kogito.process.Signal<T> of(String channel, T payload) {
        return new Sig<>(channel, payload);
    }

    public static <T> org.kie.kogito.process.Signal<T> of(String channel, T payload, String referenceId) {
        return new Sig<>(channel, payload, referenceId);
    }

    protected Sig(String channel, T payload) {
        this.channel = channel;
        this.payload = payload;
    }

    protected Sig(String channel, T payload, String referenceId) {
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
