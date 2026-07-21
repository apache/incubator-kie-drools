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
package org.kie.kogito.event.impl;

import java.util.Map;

import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageProducer<D> implements MessageProducerWithContext<D> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageProducer.class);

    private String trigger;
    private EventEmitter emitter;

    // in general, we should favor the non-empty constructor
    // but there is an issue with Quarkus https://github.com/quarkusio/quarkus/issues/2949#issuecomment-513017781
    // use this in conjunction with setParams()
    public AbstractMessageProducer() {
    }

    public AbstractMessageProducer(EventEmitter emitter, String trigger) {
        init(emitter, trigger);
    }

    protected void init(EventEmitter emitter, String trigger) {
        this.emitter = emitter;
        this.trigger = trigger;

    }

    @Override
    public void produce(KogitoProcessInstance pi, D eventData, Map<String, Object> contextAttrs) {
        emitter.emit(DataEventFactory.from(eventData, trigger, pi, contextAttrs));
    }
}
