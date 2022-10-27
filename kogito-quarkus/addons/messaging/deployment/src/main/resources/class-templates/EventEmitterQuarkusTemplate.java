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
package $Package$;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import org.kie.kogito.addon.quarkus.messaging.common.AbstractQuarkusCloudEventEmitter;

import io.quarkus.runtime.Startup;

@Startup
@ApplicationScoped
public class $Trigger$EventEmitter extends AbstractQuarkusCloudEventEmitter<$Type$> {
    @Inject
    @Channel("$Trigger$")
    Emitter<$Type$> emitter;
    
    @Override
    protected void emit (Message<$Type$> message) {
        emitter.send(message);
    }
    
    @PostConstruct
    void init () {
    }
}
