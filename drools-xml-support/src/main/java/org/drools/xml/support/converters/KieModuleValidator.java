/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.xml.support.converters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.drools.util.IoUtils;
import org.kie.api.builder.model.KieModuleModel;
import org.xml.sax.SAXException;

public class KieModuleValidator {
    private static final Schema schema = loadSchema("org/kie/api/kmodule.xsd");
    private static final Schema oldSchema = loadSchema("org/kie/api/old-kmodule.xsd");

    private static Schema loadSchema(String xsd) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
            SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema",
                    "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory", ClassLoader.getSystemClassLoader());
            URL url = KieModuleModel.class.getClassLoader().getResource(xsd);
            return url != null ? factory.newSchema(url) : null;
        } catch (SAXException ex ) {
            throw new RuntimeException( "Unable to load XSD", ex );
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    static void validate(byte[] bytes) {
        validate(new StreamSource(new ByteArrayInputStream(bytes)),
                new StreamSource(new ByteArrayInputStream(bytes)));
    }

    static void validate(java.io.File kModuleFile) {
        validate(new StreamSource(kModuleFile),
                new StreamSource(kModuleFile));
    }

    static void validate(URL kModuleUrl) {
        String urlString;
        try {
            urlString = kModuleUrl.toURI().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        validate(new StreamSource(urlString), new StreamSource(urlString));
    }

    static void validate(String kModuleString) {
        byte[] bytes = kModuleString.getBytes(IoUtils.UTF8_CHARSET);
        validate(bytes);
    }

    static void validate(Source source, Source duplicateSource) {
        try {
            validate(source, schema);
        } catch (Exception schemaException) {
            try {
                // For backwards compatibility, validate against the old namespace (which has 6.0.0 hardcoded)
                if (oldSchema != null) {
                    validate(duplicateSource, oldSchema);
                }
            } catch (Exception oldSchemaException) {
                // Throw the original exception, as we want them to use that
                throw new RuntimeException(
                        "XSD validation failed against the new schema (" + schemaException.getMessage()
                                + ") and against the old schema (" + oldSchemaException.getMessage() + ").",
                        schemaException);
            }
        }
    }

    private static void validate(Source source, Schema schema) throws SAXException, IOException {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
            Validator validator = schema.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.validate(source);
        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }
}