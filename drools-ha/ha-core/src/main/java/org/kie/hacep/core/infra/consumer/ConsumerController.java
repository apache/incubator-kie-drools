/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.hacep.core.infra.consumer;

import org.kie.remote.impl.producer.Producer;
import org.kie.hacep.EnvConfig;
import org.kie.hacep.consumer.DroolsConsumerHandler;
import org.kie.hacep.core.infra.election.LeadershipCallback;

public class ConsumerController {

    private EventConsumer consumer;
    private InfraCallback callback;
    private Thread thread;

    public ConsumerController( EnvConfig envConfig, Producer producer ) {
        this.callback = new InfraCallback();
        this.consumer = EventConsumer.get(envConfig);
        this.callback.setConsumer(consumer);
        this.consumer.initConsumer(new DroolsConsumerHandler(producer, envConfig));
    }

    public void start() {
        consumeEvents();
    }

    public void stop() {
        consumer.stop();
        stopConsumeEvents();
    }

    public EventConsumer getConsumer() {
        return consumer;
    }

    public LeadershipCallback getCallback() {
        return callback;
    }

    private void consumeEvents() {
        thread = new Thread(new ConsumerThread(this));
        thread.start();
    }

    private void stopConsumeEvents(){
        if ( thread != null) {
            try {
                thread.join();
            }catch (InterruptedException ex){
                throw new RuntimeException(ex);
            }
        }
    }

}
