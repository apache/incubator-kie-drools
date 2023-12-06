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
package org.kie.kogito.index.infinispan.protostream;

import java.io.IOException;

import org.infinispan.protostream.MessageMarshaller;
import org.kie.kogito.index.model.Entry;
import org.kie.kogito.persistence.infinispan.protostream.AbstractMarshaller;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EntryMarshaller extends AbstractMarshaller implements MessageMarshaller<Entry> {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    public EntryMarshaller(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public Entry readFrom(ProtoStreamReader reader) throws IOException {
        return new Entry(reader.readString(KEY), reader.readString(VALUE));
    }

    @Override
    public void writeTo(ProtoStreamWriter writer, Entry entry) throws IOException {
        writer.writeString(KEY, entry.getKey());
        writer.writeString(VALUE, entry.getValue());
    }

    @Override
    public Class<? extends Entry> getJavaClass() {
        return Entry.class;
    }

    @Override
    public String getTypeName() {
        return getJavaClass().getName();
    }
}
