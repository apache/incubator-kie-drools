/**
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
package org.kie.dmn.trisotech.validation;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kie.dmn.validation.DMNValidatorImpl;
import org.xml.sax.SAXException;

public class TrisotechSchema {
    static final Schema INSTANCEv1_3;
    static {
        try {
            INSTANCEv1_3 = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                                        .newSchema(new Source[]{new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DC.xsd")),
                                                                new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DI.xsd")),
                                                                new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DMNDI13.xsd")),
                                                                new StreamSource(DMNValidatorImpl.class.getResourceAsStream("org/omg/spec/DMN/20191111/DMN13.xsd")),
                                                                new StreamSource(TrisotechSchema.class.getResourceAsStream("extension/TrisotechDMN13.xsd"))
                                        });
        } catch (SAXException e) {
            throw new RuntimeException("Unable to initialize correctly TrisotechSchema.", e);
        }
    }
}
