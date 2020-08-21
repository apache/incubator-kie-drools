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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import org.junit.jupiter.api.Test;
import org.kie.kogito.mongodb.marshalling.DocumentUnmarshallingException;
import org.kie.kogito.mongodb.model.ProcessInstanceDocument;
import org.kie.kogito.mongodb.utils.DocumentUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentUtilsTest extends TestHelper {

    @Test
    void testGetCollection() {
        MongoCollection<ProcessInstanceDocument> collection = DocumentUtils.getCollection(getMongoClient(), "testCollection", DB_NAME);
        assertThat(collection.getDocumentClass().getSimpleName()).isEqualTo(ProcessInstanceDocument.class.getSimpleName());
        assertEquals(DB_NAME, collection.getNamespace().getDatabaseName());
        assertEquals("testCollection", collection.getNamespace().getCollectionName());
    }

    @Test
    void testToByteArray() throws JsonProcessingException {
        assertArrayEquals(getTestByteArrays(), DocumentUtils.toByteArray(getTestObject()));
    }

    @Test
    void testFromByteArray() throws JsonProcessingException {
        Object value = DocumentUtils.fromByteArray(getTestObject().getClass().getCanonicalName(), getTestByteArrays());
        assertNotNull(value, "Unmarshalled value should not be null");
        assertEquals(getTestObject().getClass().getCanonicalName(), value.getClass().getCanonicalName(), "Object should be same.");
    }

    @Test
    void testFromByteArrayException() throws JsonProcessingException {
        byte[] data = getTestByteArrays();
        assertThrows(DocumentUnmarshallingException.class, () -> DocumentUtils.fromByteArray("test", data));
    }
}
