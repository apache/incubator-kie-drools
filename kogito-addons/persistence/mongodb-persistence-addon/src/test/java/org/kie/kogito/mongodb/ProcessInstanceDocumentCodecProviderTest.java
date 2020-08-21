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

package org.kie.kogito.mongodb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import com.mongodb.MongoClientSettings;
import org.bson.BsonBinaryReader;
import org.bson.BsonBinaryWriter;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.io.BasicOutputBuffer;
import org.junit.jupiter.api.Test;
import org.kie.kogito.mongodb.codec.ProcessInstanceDocumentCodec;
import org.kie.kogito.mongodb.codec.ProcessInstanceDocumentCodecProvider;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessInstanceDocumentCodecProviderTest {

    @Test
    void providerTest() {
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromProviders(new ProcessInstanceDocumentCodecProvider()));
        CodecProvider provider = codecRegistry;
        Codec<ProcessInstanceDocument> codec = provider.get(ProcessInstanceDocument.class, codecRegistry);
        assertNotNull(codec, "Codec cannot be null");
        assertThat(codec.getEncoderClass().getSimpleName()).isEqualTo(ProcessInstanceDocument.class.getSimpleName());
        assertThat(codec.getClass().getCanonicalName()).isEqualTo(ProcessInstanceDocumentCodec.class.getCanonicalName());

        ProcessInstanceDocumentCodecProvider cp = new ProcessInstanceDocumentCodecProvider();
        assertNull(cp.get(Object.class, codecRegistry));
    }

    @Test
    void codecTest() throws URISyntaxException, IOException {
        ProcessInstanceDocumentCodec codec = new ProcessInstanceDocumentCodec();
        assertThat(codec.getEncoderClass()).isEqualTo(ProcessInstanceDocument.class);
        ProcessInstanceDocument doc = new ProcessInstanceDocument();
        doc.setProcessInstance((org.bson.Document) TestHelper.getProcessInstanceDocument().get("processInstance"));
        assertNull(doc.getId(), "ProcessInstanceDocument is null");
        codec.generateIdIfAbsentFromDocument(doc);
        assertTrue(codec.documentHasId(doc), "ProcessInstanceDocument has document id");
        assertNotNull(codec.getDocumentId(doc), "ProcessInstanceDocument has document id and not null");

        BasicOutputBuffer buffer = new BasicOutputBuffer();
        BsonWriter writer = new BsonBinaryWriter(buffer);
        codec.encode(writer, doc, EncoderContext.builder().isEncodingCollectibleDocument(true).build());
        BsonBinaryReader reader = new BsonBinaryReader(ByteBuffer.wrap(buffer.toByteArray()));
        ProcessInstanceDocument document = codec.decode(reader, DecoderContext.builder().build());
        assertNotNull(document, "ProcessInstanceDocument cannot be null");
        assertThat(document.getId()).isEqualTo(doc.getId());
        assertNotNull(document.getProcessInstance());
        assertThat(document.getProcessInstance().get("id")).isEqualTo(doc.getProcessInstance().get("id"));
        assertThat(document.getProcessInstance().get("processid")).isEqualTo(doc.getProcessInstance().get("processid"));
    }

}
