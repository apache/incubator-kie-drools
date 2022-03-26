/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.events.mongodb.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.kie.kogito.event.process.ProcessInstanceDataEvent;
import org.kie.kogito.event.process.UserTaskInstanceDataEvent;
import org.kie.kogito.event.process.VariableInstanceDataEvent;

public class EventMongoDBCodecProvider implements CodecProvider {

    private static final ProcessInstanceDataEventCodec PROCESS_INSTANCE_DATA_EVENT_CODEC = new ProcessInstanceDataEventCodec();
    private static final UserTaskInstanceDataEventCodec USER_TASK_INSTANCE_DATA_EVENT_CODEC = new UserTaskInstanceDataEventCodec();
    private static final VariableInstanceDataEventCodec VARIABLE_INSTANCE_DATA_EVENT_CODEC = new VariableInstanceDataEventCodec();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        if (aClass == ProcessInstanceDataEvent.class) {
            return (Codec<T>) PROCESS_INSTANCE_DATA_EVENT_CODEC;
        }
        if (aClass == UserTaskInstanceDataEvent.class) {
            return (Codec<T>) USER_TASK_INSTANCE_DATA_EVENT_CODEC;
        }
        if (aClass == VariableInstanceDataEvent.class) {
            return (Codec<T>) VARIABLE_INSTANCE_DATA_EVENT_CODEC;
        }
        return null;
    }
}
