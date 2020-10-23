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
import org.jbpm.serverless.workflow.api.states.SubflowState;

import java.io.IOException;

public class SubflowStateSerializer extends StdSerializer<SubflowState> {

    public SubflowStateSerializer() {
        this(SubflowState.class);
    }

    protected SubflowStateSerializer(Class<SubflowState> t) {
        super(t);
    }

    @Override
    public void serialize(SubflowState subflowState,
                          JsonGenerator gen,
                          SerializerProvider provider) throws IOException {

        // set defaults for end state
        subflowState.setType(DefaultState.Type.SUBFLOW);

        // serialize after setting default bean values...
        BeanSerializerFactory.instance.createSerializer(provider,
                TypeFactory.defaultInstance().constructType(SubflowState.class)).serialize(subflowState,
                gen,
                provider);
    }
}