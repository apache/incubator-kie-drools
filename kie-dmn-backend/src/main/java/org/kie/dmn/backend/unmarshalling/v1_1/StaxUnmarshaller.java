/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.backend.unmarshalling.v1_1;

import org.kie.dmn.feel.model.v1_1.Definitions;
import org.kie.dmn.feel.model.v1_1.InputData;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.Reader;
import java.util.Iterator;

public class StaxUnmarshaller {
    public static final String INPUT_DATA          = "inputData";
    public static final String DEFINITIONS         = "definitions";
    public static final String ID                  = "id";
    public static final String LABEL               = "label";
    public static final String NAME                = "name";
    public static final String EXPRESSION_LANGUAGE = "expressionLanguage";
    public static final String TYPE_LANGUAGE       = "typeLanguage";
    public static final String NAMESPACE           = "namespace";
    public static final String EXPORTER            = "exporter";
    public static final String EXPORTER_VERSION    = "exporterVersion";

    public StaxUnmarshaller() {
    }

    public Definitions unmarshalSTAX(Reader isr) {
        try {
            // First, create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            XMLEventReader eventReader = inputFactory.createXMLEventReader( isr );
            // read the XML document
            Definitions def = parseModel( eventReader );
            return def;
        } catch ( XMLStreamException e ) {
            e.printStackTrace();
        }
        return null;
    }

    Definitions parseModel(XMLEventReader eventReader)
            throws XMLStreamException {
        Definitions def = null;

        while ( eventReader.hasNext() ) {
            XMLEvent event = eventReader.nextEvent();

            if ( event.isStartElement() ) {
                StartElement startElement = event.asStartElement();
                if ( startElement.getName().getLocalPart().equals( DEFINITIONS ) ) {
                    def = parseDefinitions( eventReader, startElement );
                }
            }
        }
        return def;
    }

    Definitions parseDefinitions(XMLEventReader eventReader, StartElement definitionsStartElement)
            throws XMLStreamException {
        Definitions def = new Definitions();
        // parse attributes
        parseDefinitionAttributes( def, definitionsStartElement );

        // then parse nested elements
        while ( eventReader.hasNext() ) {
            XMLEvent event = eventReader.nextEvent();
            if ( event.isStartElement() ) {
                StartElement startElement = event.asStartElement();
                switch ( startElement.getName().getLocalPart() ) {
                    case INPUT_DATA:
                        InputData input = parseInputData( eventReader, startElement );
                        def.getDrgElement().add( input );
                        break;
                }

            } else if ( event.isEndElement() && event.asEndElement().getName().getLocalPart().equals( DEFINITIONS ) ) {
                break;
            }
        }
        return def;
    }

    InputData parseInputData(XMLEventReader eventReader, StartElement inputStartElement) {
        InputData input = new InputData();
        parseInputDataAttributes( input, inputStartElement );

        return null;
    }

    void parseInputDataAttributes(InputData input, StartElement startElement) {
        Iterator<Attribute> attributes = startElement.getAttributes();
        while ( attributes.hasNext() ) {
            Attribute attribute = attributes.next();
            String name = attribute.getName().getLocalPart();
            switch ( name ) {
                case ID:
                    input.setId( attribute.getValue() );
                    break;
                case LABEL:
                    input.setLabel( attribute.getValue() );
                    break;
                case NAME:
                    input.setName( attribute.getValue() );
                    break;
            }
        }
    }

    void parseDefinitionAttributes(Definitions def, StartElement startElement) {
        Iterator<Attribute> attributes = startElement.getAttributes();
        while ( attributes.hasNext() ) {
            Attribute attribute = attributes.next();
            String name = attribute.getName().getLocalPart();
            switch ( name ) {
                case ID:
                    def.setId( attribute.getValue() );
                    break;
                case LABEL:
                    def.setLabel( attribute.getValue() );
                    break;
                case NAME:
                    def.setName( attribute.getValue() );
                    break;
                case EXPRESSION_LANGUAGE:
                    def.setExpressionLanguage( attribute.getValue() );
                    break;
                case TYPE_LANGUAGE:
                    def.setTypeLanguage( attribute.getValue() );
                    break;
                case NAMESPACE:
                    def.setNamespace( attribute.getValue() );
                    break;
                case EXPORTER:
                    def.setExporter( attribute.getValue() );
                    break;
                case EXPORTER_VERSION:
                    def.setExporterVersion( attribute.getValue() );
                    break;
            }
        }
    }
}