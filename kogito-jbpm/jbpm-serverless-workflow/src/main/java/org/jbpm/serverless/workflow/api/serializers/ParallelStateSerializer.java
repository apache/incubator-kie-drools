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
import org.jbpm.serverless.workflow.api.states.ParallelState;

import java.io.IOException;

public class ParallelStateSerializer extends StdSerializer<ParallelState> {

    public ParallelStateSerializer() {
        this(ParallelState.class);
    }

    protected ParallelStateSerializer(Class<ParallelState> t) {
        super(t);
    }

    @Override
    public void serialize(ParallelState parallelState,
                          JsonGenerator gen,
                          SerializerProvider provider) throws IOException {

        // set defaults for end state
        parallelState.setType(DefaultState.Type.PARALLEL);

        // serialize after setting default bean values...
        BeanSerializerFactory.instance.createSerializer(provider,
                TypeFactory.defaultInstance().constructType(ParallelState.class)).serialize(parallelState,
                gen,
                provider);
    }
}