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

package org.kie.kogito.mongodb.codec;

import java.util.stream.Collectors;

import com.mongodb.MongoClientSettings;
import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.CollectibleCodec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;

import static org.kie.kogito.mongodb.utils.DocumentConstants.DOCUMENT_ID;
import static org.kie.kogito.mongodb.utils.DocumentConstants.NAME;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE;
import static org.kie.kogito.mongodb.utils.DocumentConstants.PROCESS_INSTANCE_ID;
import static org.kie.kogito.mongodb.utils.DocumentConstants.STRATEGIES;
import static org.kie.kogito.mongodb.utils.DocumentConstants.VALUE;

public class ProcessInstanceDocumentCodec implements CollectibleCodec<ProcessInstanceDocument> {

    private final Codec<Document> documentCodec;

    public ProcessInstanceDocumentCodec() {
        documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    }

    @Override
    public void encode(BsonWriter writer, ProcessInstanceDocument piDoc, EncoderContext encoderContext) {
        Document doc = new Document();
        doc.put(DOCUMENT_ID, piDoc.getProcessInstance().get(PROCESS_INSTANCE_ID));
        doc.put(PROCESS_INSTANCE, piDoc.getProcessInstance());
        doc.put(STRATEGIES, piDoc.getStrategies().entrySet().stream().map(e -> new Document().append(NAME, e.getKey()).append(VALUE, e.getValue())).collect(Collectors.toList()));
        documentCodec.encode(writer, doc, encoderContext);
    }

    @Override
    public Class<ProcessInstanceDocument> getEncoderClass() {
        return ProcessInstanceDocument.class;
    }

    @Override
    public ProcessInstanceDocument generateIdIfAbsentFromDocument(ProcessInstanceDocument document) {
        if (!documentHasId(document)) {
            document.setId(document.getProcessInstance().getString(PROCESS_INSTANCE_ID));
        }
        return document;
    }

    @Override
    public boolean documentHasId(ProcessInstanceDocument document) {
        return document.getId() != null;
    }

    @Override
    public BsonValue getDocumentId(ProcessInstanceDocument document) {
        return new BsonString(document.getId());
    }

    @Override
    public ProcessInstanceDocument decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        ProcessInstanceDocument piDoc = new ProcessInstanceDocument();
        piDoc.setId(document.getString(DOCUMENT_ID));
        piDoc.setProcessInstance((Document) (document.get(PROCESS_INSTANCE)));
        piDoc.setStrategies(document.getList(STRATEGIES, Document.class).stream().collect(Collectors.toMap(d -> d.getString(NAME), d -> d.getInteger(VALUE))));
        return piDoc;
    }
}
