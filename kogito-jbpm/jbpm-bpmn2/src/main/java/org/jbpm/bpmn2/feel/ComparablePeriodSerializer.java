/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.feel;

import java.io.IOException;

import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ComparablePeriodSerializer extends StdSerializer<ComparablePeriod> {

    private static final long serialVersionUID = -3734521147902638828L;

    public ComparablePeriodSerializer() {
        this(null);
    }

    public ComparablePeriodSerializer(Class<ComparablePeriod> t) {
        super(t);
    }

    @Override
    public void serialize(ComparablePeriod v, JsonGenerator g, SerializerProvider sp) throws IOException {
        sp.defaultSerializeValue(v.asPeriod(), g);
    }

}