/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.common.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.drools.planner.core.solution.Solution;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public abstract class AbstractXmlSolutionImporter extends AbstractSolutionImporter {

    private static final String DEFAULT_INPUT_FILE_SUFFIX = ".xml";

    protected AbstractXmlSolutionImporter(SolutionDao solutionDao) {
        super(solutionDao);
    }

    protected String getInputFileSuffix() {
        return DEFAULT_INPUT_FILE_SUFFIX;
    }

    public abstract XmlInputBuilder createXmlInputBuilder();

    public Solution readSolution(File inputFile) {
        InputStream in = null;
        try {
            in = new FileInputStream(inputFile);
            SAXBuilder builder = new SAXBuilder(false);
            Document document = builder.build(in);
            XmlInputBuilder txtInputBuilder = createXmlInputBuilder();
            txtInputBuilder.setDocument(document);
            return txtInputBuilder.readSolution();
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the file (" + inputFile.getName() + ").", e);
        } catch (JDOMException e) {
            throw new IllegalArgumentException("Could not parse the XML file (" + inputFile.getName() + ").", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public abstract class XmlInputBuilder {

        protected Document document;

        public void setDocument(Document document) {
            this.document = document;
        }

        public abstract Solution readSolution() throws IOException, JDOMException;

        // ************************************************************************
        // Helper methods
        // ************************************************************************

        protected void assertElementName(Element element, String name) {
            if (!element.getName().equals(name)) {
                throw new IllegalStateException("Element name (" + element.getName()
                        + ") should be " + name + ".");
            }
        }

    }

}
