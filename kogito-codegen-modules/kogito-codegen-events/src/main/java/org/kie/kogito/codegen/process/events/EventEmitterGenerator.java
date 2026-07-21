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
package org.kie.kogito.codegen.process.events;

import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.EventMarshaller;

import com.github.javaparser.ast.body.MethodDeclaration;

public class EventEmitterGenerator extends EventGenerator {

    public EventEmitterGenerator(KogitoBuildContext context, ChannelInfo channelInfo, boolean isTxEnabled) {
        super(context, channelInfo, (isTxEnabled ? "Tx" : "") + "EventEmitter");
        if (channelInfo.getCloudEventMode().filter(mode -> mode == CloudEventMode.STRUCTURED).isPresent()) {
            generateMarshallerField("ceMarshaller", CloudEventMarshaller.class);
            clazz.findAll(MethodDeclaration.class).stream()
                    .filter(m -> m.getNameAsString().equals("getEventMarshaller") || m.getNameAsString().equals("toTopicTypeEvent"))
                    .forEach(clazz::remove);
            clazz.findAll(MethodDeclaration.class).stream().filter(m -> m.getNameAsString().equals("toTopicTypeCloud")).forEach(m -> m.setName("toTopicType"));
        } else {
            generateMarshallerField("eventDataMarshaller", EventMarshaller.class);
            clazz.findAll(MethodDeclaration.class).stream()
                    .filter(m -> m.getNameAsString().equals("getCloudEventMarshaller") || m.getNameAsString().equals("toTopicTypeCloud"))
                    .forEach(clazz::remove);
            clazz.findAll(MethodDeclaration.class).stream().filter(m -> m.getNameAsString().equals("toTopicTypeEvent")).forEach(m -> m.setName("toTopicType"));
        }
    }
}
