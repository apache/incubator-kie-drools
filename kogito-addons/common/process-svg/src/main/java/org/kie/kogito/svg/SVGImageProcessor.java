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
package org.kie.kogito.svg;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kie.kogito.svg.processor.SVGProcessor;
import org.kie.kogito.svg.processor.SVGProcessorFactory;
import org.w3c.dom.Document;

import static org.kie.kogito.svg.processor.SVGProcessor.ACTIVE_BORDER_COLOR;
import static org.kie.kogito.svg.processor.SVGProcessor.COMPLETED_BORDER_COLOR;
import static org.kie.kogito.svg.processor.SVGProcessor.COMPLETED_COLOR;

public class SVGImageProcessor {

    private SVGProcessor svgProcessor;

    public SVGImageProcessor(InputStream svg) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // to be compliant, completely disable DOCTYPE declaration:
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // or completely disable external entities declarations:
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // or prohibit the use of all protocols by external entities:
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            // or disable entity expansion but keep in mind that this doesn't prevent fetching external entities
            // and this solution is not correct for OpenJDK < 13 due to a bug: https://bugs.openjdk.java.net/browse/JDK-8206132
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(svg);

            svgProcessor = new SVGProcessorFactory().create(doc);
            svgProcessor.processNodes(doc.getChildNodes());
        } catch (Exception e) {
            throw new ProcessSVGException("Could not parse svg", e);
        }
    }

    public static String transform(InputStream svg, List<String> completed, List<String> active) {
        return transform(svg, completed, active, null, COMPLETED_COLOR, COMPLETED_BORDER_COLOR, ACTIVE_BORDER_COLOR);
    }

    public static String transform(InputStream svg, List<String> completed, List<String> active, Map<String, String> subProcessLinks) {
        return transform(svg, completed, active, subProcessLinks, COMPLETED_COLOR, COMPLETED_BORDER_COLOR, ACTIVE_BORDER_COLOR);
    }

    public static String transform(InputStream svg, List<String> completed, List<String> active,
            Map<String, String> subProcessLinks, String completedNodeColor,
            String completedNodeBorderColor, String activeNodeBorderColor) {
        SVGProcessor processor = new SVGImageProcessor(svg).getProcessor();

        for (String nodeId : completed) {
            if (!active.contains(nodeId)) {
                processor.defaultCompletedTransformation(nodeId, completedNodeColor, completedNodeBorderColor);
            }
        }
        for (String nodeId : active) {
            processor.defaultActiveTransformation(nodeId, activeNodeBorderColor);
        }

        if (subProcessLinks != null) {

            for (Map.Entry<String, String> subProcessLink : subProcessLinks.entrySet()) {
                processor.defaultSubProcessLinkTransformation(subProcessLink.getKey(), subProcessLink.getValue());
            }
        }
        return processor.getSVG();
    }

    public SVGProcessor getProcessor() {
        return svgProcessor;
    }

}
