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
package org.kie.kogito.addon.quarkus.messaging.common;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.AbstractMultiOperator;
import io.smallrye.mutiny.operators.multi.MultiOperatorProcessor;
import io.smallrye.mutiny.subscription.MultiSubscriber;
import io.smallrye.reactive.messaging.providers.PublisherDecorator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BackpressurePublisherDecorator implements PublisherDecorator {

    private static final Logger logger = LoggerFactory.getLogger(BackpressurePublisherDecorator.class);

    @Inject
    BackpressureKogitoEmitter emitter;

    @Override
    public Multi<? extends Message<?>> decorate(Multi<? extends Message<?>> publisher, String channelName) {
        return publisher.plug(upstream -> new BackpressureOperator(publisher, channelName));
    }

    private class BackpressureOperator extends AbstractMultiOperator<Message<?>, Message<?>> {

        private String channelName;

        public BackpressureOperator(Multi<? extends Message<?>> upstream, String channelName) {
            super(upstream);
            this.channelName = channelName;
        }

        @Override
        public void subscribe(MultiSubscriber<? super Message<?>> downstream) {
            upstream.subscribe().withSubscriber(new BackpressureProcessor(downstream, channelName));
        }
    }

    private class BackpressureProcessor extends MultiOperatorProcessor<Message<?>, Message<?>> {

        private String channelName;

        public BackpressureProcessor(MultiSubscriber<? super Message<?>> downstream, String channelName) {
            super(downstream);
            this.channelName = channelName;
            emitter.registerHandler(channelName, () -> super.request(1));
        }

        @Override
        public void request(final long n) {
            if (emitter.isEnabled(channelName)) {
                logger.trace("Requesting {} elements", n);
                super.request(n);
            } else {
                logger.trace("Blocking {} elements", n);
            }
        }
    }
}
