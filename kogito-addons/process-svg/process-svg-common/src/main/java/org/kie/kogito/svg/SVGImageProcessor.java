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

package org.kie.kogito.svg;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
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
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            factory.setValidating(false);
            Document svgDocument = factory.createDocument("http://jbpm.org", svg);

            svgProcessor = new SVGProcessorFactory().create(svgDocument);
            svgProcessor.processNodes(svgDocument.getChildNodes());
        } catch (IOException e) {
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