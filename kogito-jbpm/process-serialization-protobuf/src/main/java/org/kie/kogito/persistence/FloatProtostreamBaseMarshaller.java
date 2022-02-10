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
package org.kie.kogito.persistence;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;

public class FloatProtostreamBaseMarshaller implements MessageMarshaller<Float> {

    @Override
    public Class<Float> getJavaClass() {
        return Float.class;
    }

    @Override
    public String getTypeName() {
        return "kogito.Float";
    }

    @Override
    public Float readFrom(ProtoStreamReader reader) throws IOException {
        return reader.readFloat("data");
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Float data) throws IOException {
        writer.writeFloat("data", data);
    }
}
