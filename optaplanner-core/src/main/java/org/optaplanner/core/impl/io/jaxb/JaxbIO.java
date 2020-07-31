/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.io.jaxb;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.optaplanner.core.impl.io.XmlUnmarshallingException;

public final class JaxbIO<T> {
    private static final int DEFAULT_INDENTATION = 2;

    private final Unmarshaller unmarshaller;
    private final Marshaller marshaller;
    private final Class<T> rootClass;
    private final int indentation;

    public JaxbIO(Class<T> rootClass) {
        this(rootClass, DEFAULT_INDENTATION);
    }

    public JaxbIO(Class<T> rootClass, int indentation) {
        Objects.requireNonNull(rootClass);
        this.rootClass = rootClass;
        this.indentation = indentation;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(rootClass);
            unmarshaller = jaxbContext.createUnmarshaller();
            marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.toString());
        } catch (JAXBException jaxbException) {
            String errMessage = String.format("Unable to create JAXB marshaller or unmarshaller for a root element class %s.",
                    rootClass.getName());
            throw new IllegalStateException(errMessage, jaxbException);
        }
    }

    public T read(Reader reader) {
        Objects.requireNonNull(reader);
        try {
            return (T) unmarshaller.unmarshal(reader);
        } catch (JAXBException jaxbException) {
            String errMessage = String.format("Unable to read the %s from XML.", rootClass.getName());
            throw new XmlUnmarshallingException(errMessage, jaxbException);
        }
    }

    public void write(T root, Writer writer) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(writer);
        DOMResult domResult = new DOMResult();
        try {
            marshaller.marshal(root, domResult);
        } catch (JAXBException jaxbException) {
            String errMessage = String.format("Unable to marshall the %s to XML.", rootClass.getName());
            throw new IllegalStateException(errMessage, jaxbException);
        }

        // See https://stackoverflow.com/questions/46708498/jaxb-marshaller-indentation.

        /*
         * The code is not vulnerable to XXE-based attacks as it does not process any external XML nor XSL input.
         * Should the transformerFactory be used for such purposes, it has to be appropriately secured:
         * https://owasp.org/www-project-top-ten/OWASP_Top_Ten_2017/Top_10-2017_A4-XML_External_Entities_(XXE)
         */
        @SuppressWarnings({ "java:S2755", "java:S4435" })
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {

            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentation));

            transformer.transform(new DOMSource(domResult.getNode()), new StreamResult(writer));
        } catch (TransformerException transformerException) {
            String errMessage = String.format("Unable to format %s XML.", rootClass.getName());
            throw new IllegalStateException(errMessage, transformerException);
        }
    }
}
