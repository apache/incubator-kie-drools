/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.document.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Map;

import org.drools.core.common.DroolsObjectInputStream;
import org.jbpm.document.Document;
import org.jbpm.document.service.DocumentStorageService;
import org.jbpm.document.service.DocumentStorageServiceProvider;

public class DocumentMarshallingStrategy extends AbstractDocumentMarshallingStrategy {

    private DocumentStorageService documentStorageService;

    public DocumentMarshallingStrategy() {
        this.documentStorageService = DocumentStorageServiceProvider.get().getStorageService();
    }

    public DocumentMarshallingStrategy(String path) {
        this.documentStorageService = DocumentStorageServiceProvider.get().getStorageService();
    }
    
    public DocumentMarshallingStrategy(DocumentStorageService documentStorageService) {
        this.documentStorageService = documentStorageService;
    }

    @Override
    public Document buildDocument( String name, long size, Date lastModified, Map<String, String> params ) {
        return documentStorageService.buildDocument( name, size, lastModified, params );
    }

    @Override
    public void write(ObjectOutputStream os, Object object) throws IOException {
        throw new UnsupportedOperationException("write is not supported anymore, use marshal instead");
    }

    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("read is not supported anymore, use unmarshal instead");
    }

    @Override
    public byte[] marshal(Context context, ObjectOutputStream objectOutputStream, Object o) throws IOException {
        Document document = (Document) o;
        String updatedAttribute = document.getAttribute(Document.UPDATED_ATTRIBUTE);
        if (Boolean.parseBoolean(updatedAttribute)) {
            // store via service only when it was actually updated
            documentStorageService.saveDocument(document, document.getContent());
            document.addAttribute(Document.UPDATED_ATTRIBUTE, "false");
        }
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buff);
        oos.writeUTF(document.getIdentifier());
        oos.writeUTF(document.getClass().getCanonicalName());
        oos.writeUTF(document.getLink());
        oos.close();
        return buff.toByteArray();
    }

    @SuppressWarnings({"resource", "unused"})
    @Override
    public Object unmarshal(Context context, ObjectInputStream objectInputStream, byte[] object, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        DroolsObjectInputStream is = new DroolsObjectInputStream(new ByteArrayInputStream(object), classLoader);
        // first we read out the object id and class name we stored during marshaling
        String objectId = is.readUTF();
        String canonicalName = is.readUTF();
        String link = is.readUTF();
        Document storedDoc = null;
        try {            
            storedDoc = documentStorageService.getDocument(objectId);            
            storedDoc.setLink( link );
            // when loaded, mark it as not updated to avoid not needed marshalling
            storedDoc.addAttribute(Document.UPDATED_ATTRIBUTE, "false");
        } catch (Exception e) {
            throw new RuntimeException("Cannot read document from storage service", e);
        }
        return storedDoc;
    }

    @Override
    public Context createContext() {
        return null;
    }
}