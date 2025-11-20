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
package org.kie.kogito.svg.processor;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kie.kogito.svg.ProcessSVGException;
import org.kie.kogito.svg.model.SVGSummary;
import org.kie.kogito.svg.model.Transformation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractSVGProcessor implements SVGProcessor {

    protected Document svgDocument;
    protected SVGSummary summary = new SVGSummary();
    protected boolean mapById = true;
    private TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();

    public AbstractSVGProcessor(Document svgDocument, boolean mapById) {
        this.svgDocument = svgDocument;
        this.mapById = mapById;
        try {
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void transform(Transformation t) {
        t.transform(summary);
    }

    @Override
    public String getSVG() {
        try (StringWriter writer = new StringWriter()) {
            DOMSource domSource = new DOMSource(svgDocument.getFirstChild());
            StreamResult result = new StreamResult(writer);
            Transformer transformer = transformerFactory.newTransformer();
            ((Element) svgDocument.getFirstChild()).setAttribute("viewBox", "0 0 " +
                    ((Element) svgDocument.getFirstChild()).getAttribute("width") + " " +
                    ((Element) svgDocument.getFirstChild()).getAttribute("height"));
            ((Element) svgDocument.getFirstChild()).removeAttribute("width");
            ((Element) svgDocument.getFirstChild()).removeAttribute("height");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException | IOException e) {
            throw new ProcessSVGException("Could not transform svg", e);
        }
    }

    @Override
    public void defaultCompletedTransformation(String nodeId) {
        defaultCompletedTransformation(nodeId, COMPLETED_COLOR, COMPLETED_BORDER_COLOR);
    }

    @Override
    public void defaultActiveTransformation(String nodeId) {
        defaultActiveTransformation(nodeId, ACTIVE_BORDER_COLOR);
    }
}
