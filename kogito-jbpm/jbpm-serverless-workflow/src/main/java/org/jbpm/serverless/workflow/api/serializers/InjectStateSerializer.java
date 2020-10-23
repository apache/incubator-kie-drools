/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.serverless.workflow.api.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.jbpm.serverless.workflow.api.states.DefaultState;
import org.jbpm.serverless.workflow.api.states.InjectState;

import java.io.IOException;

public class InjectStateSerializer extends StdSerializer<InjectState> {

    public InjectStateSerializer() {
        this(InjectState.class);
    }

    protected InjectStateSerializer(Class<InjectState> t) {
        super(t);
    }

    @Override
    public void serialize(InjectState relayState,
                          JsonGenerator gen,
                          SerializerProvider provider) throws IOException {

        // set defaults for relay state
        relayState.setType(DefaultState.Type.INJECT);

        // serialize after setting default bean values...
        BeanSerializerFactory.instance.createSerializer(provider,
                TypeFactory.defaultInstance().constructType(InjectState.class)).serialize(relayState,
                gen,
                provider);
    }
}