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
package org.kie.kogito.events.mongodb.codec;

import org.junit.jupiter.api.Test;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.usertask.UserTaskInstanceDataEvent;

import static org.assertj.core.api.Assertions.assertThat;

class EventMongoDBCodecProviderTest {

    @Test
    void get() {
        EventMongoDBCodecProvider provider = new EventMongoDBCodecProvider();

        assertThat(provider.get(ProcessInstanceDataEvent.class, null).getClass()).isEqualTo(ProcessInstanceDataEventCodec.class);
        assertThat(provider.get(UserTaskInstanceDataEvent.class, null).getClass()).isEqualTo(UserTaskInstanceDataEventCodec.class);
        assertThat(provider.get(String.class, null)).isNull();
    }
}