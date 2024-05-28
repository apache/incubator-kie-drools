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
package org.jbpm.flow.serialization.impl.marshallers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jbpm.flow.serialization.ObjectMarshallerStrategy;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerException;
import org.jbpm.flow.serialization.protobuf.KogitoTypesProtobuf;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.protobuf.Any;

public class ProtobufDocumentNodeMessageMarshaller implements ObjectMarshallerStrategy {

    @Override
    public boolean acceptForMarshalling(Object value) {
        return value instanceof Document;
    }

    @Override
    public boolean acceptForUnmarshalling(Any value) {
        return value.is(KogitoTypesProtobuf.Document.class);
    }

    @Override
    public Any marshall(Object unmarshalled) {
        try {
            KogitoTypesProtobuf.Document.Builder builder = KogitoTypesProtobuf.Document.newBuilder();
            Document document = (Document) unmarshalled;
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            StringWriter sw = new StringWriter();
            trans.transform(new DOMSource(document), new StreamResult(sw));
            builder.setContent(sw.toString());
            return Any.pack(builder.build());
        } catch (TransformerException e) {
            throw new ProcessInstanceMarshallerException("Error trying to marshalling a Document Node value", e);
        }
    }

    @Override
    public Object unmarshall(Any data) {
        try {
            KogitoTypesProtobuf.Document storedValue = data.unpack(KogitoTypesProtobuf.Document.class);
            StringBuilder xmlStringBuilder = new StringBuilder(storedValue.getContent());
            ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setExpandEntityReferences(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            return factory.newDocumentBuilder().parse(input);
        } catch (IOException | SAXException | ParserConfigurationException e1) {
            throw new ProcessInstanceMarshallerException("Error trying to unmarshalling a Document Node value", e1);
        }
    }
}