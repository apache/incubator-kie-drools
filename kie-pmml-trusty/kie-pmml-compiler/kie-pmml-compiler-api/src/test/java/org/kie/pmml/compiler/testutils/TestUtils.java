/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compiler.testutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.PMML;
import org.kie.test.util.filesystem.FileUtils;
import org.xml.sax.SAXException;

public class TestUtils {

    /**
     * Load a <code>PMML</code> from the given <b>file</b>
     * @param fileName
     * @return
     * @throws SAXException
     * @throws JAXBException
     * @throws IOException
     */
    public static PMML loadFromFile(String fileName) throws SAXException, JAXBException, IOException {
        return loadFromInputStream(FileUtils.getFileInputStream(fileName));
    }

    /**
     * Load a <code>PMML</code> from the given <b>xml source</b>
     * @param xmlSource
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public static PMML loadFromSource(String xmlSource) throws SAXException, JAXBException {
        return loadFromInputStream(new ByteArrayInputStream(xmlSource.getBytes()));
    }

    /**
     * Load a <code>PMML</code> from the given <code>InputStream</code>
     * @param is
     * @return
     * @throws SAXException
     * @throws JAXBException
     * @see org.jpmml.model.PMMLUtil#unmarshal(InputStream)
     */
    public static PMML loadFromInputStream(InputStream is) throws SAXException, JAXBException {
        return org.jpmml.model.PMMLUtil.unmarshal(is);
    }


}
