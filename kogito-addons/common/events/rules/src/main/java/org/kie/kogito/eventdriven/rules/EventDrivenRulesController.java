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
package org.kie.kogito.eventdriven.rules;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.cloudevents.extension.KogitoRulesExtension;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.core.provider.ExtensionProvider;

/**
 * This class must always have exact FQCN as <code>org.kie.kogito.eventdriven.rules.EventDrivenRulesController</code>
 * for code generation plugins to correctly detect if this addon is enabled.
 */
public class EventDrivenRulesController {

    private static final String REQUEST_EVENT_TYPE = "RulesRequest";
    private static final String RESPONSE_EVENT_TYPE = "RulesResponse";

    private static final Logger LOG = LoggerFactory.getLogger(EventDrivenRulesController.class);

    private ConfigBean config;
    private EventEmitter eventEmitter;
    private EventReceiver eventReceiver;

    protected EventDrivenRulesController() {
    }

    protected EventDrivenRulesController(ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        init(config, eventEmitter, eventReceiver);
    }

    protected void init(ConfigBean config, EventEmitter eventEmitter, EventReceiver eventReceiver) {
        this.config = config;
        this.eventEmitter = eventEmitter;
        this.eventReceiver = eventReceiver;
    }

    public <D> void subscribe(EventDrivenQueryExecutor<D> queryExecutor, Class<D> objectClass) {
        eventReceiver.subscribe(new RequestHandler<>(queryExecutor), objectClass);
    }

    private class RequestHandler<T> implements Consumer<DataEvent<T>> {

        private EventDrivenQueryExecutor<T> queryExecutor;

        public RequestHandler(EventDrivenQueryExecutor<T> queryExecutor) {
            this.queryExecutor = queryExecutor;
        }

        @Override
        public void accept(DataEvent<T> event) {
            KogitoRulesExtension extension = ExtensionProvider.getInstance().parseExtension(KogitoRulesExtension.class, event);
            if (CloudEventUtils.isValidRequest(event, REQUEST_EVENT_TYPE, extension)) {
                buildResponseCloudEvent(event, queryExecutor.executeQuery(event), extension).ifPresentOrElse(c -> eventEmitter.emit(c),
                        () -> LOG.info("Extension {} does not match this query executor {}", extension, queryExecutor));
            } else {
                LOG.warn("Event {} does not have expected information, discarding it", event);
            }
        }

        private Optional<DataEvent<?>> buildResponseCloudEvent(DataEvent<?> event, Object payload, KogitoRulesExtension extension) {
            return Objects.equals(queryExecutor.getRuleUnitId(), extension.getRuleUnitId()) && Objects.equals(queryExecutor.getQueryName(), extension.getRuleUnitQuery())
                    ? Optional.of(DataEventFactory.from(payload, RESPONSE_EVENT_TYPE, CloudEventUtils.buildDecisionSource(config.getServiceUrl(), toKebabCase(queryExecutor.getQueryName())),
                            Optional.ofNullable(event.getSubject()), extension))
                    : Optional.empty();
        }

        private String toKebabCase(String inputString) {
            return inputString == null ? null : inputString.replaceAll("(.)(\\p{Upper})", "$1-$2").toLowerCase();
        }
    }

}
