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
package org.kie.pmml.models.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.PMML;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestUtils {

    /**
     * Load a <code>PMML</code> from the given <b>file</b>
     * @param fileName
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public static PMML loadFromFile(String fileName) throws SAXException, JAXBException, IOException {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String filePath = ResourceHelper.getResourcesByExtension(extension)
                .filter(path -> path.endsWith(fileName))
                .findFirst()
                .orElse(null);
        assertNotNull(filePath);
        File sourceFile = new File(filePath);
        assertTrue(sourceFile.exists());
        return loadFromInputStream(new FileInputStream(sourceFile));
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
     * oad a <code>PMML</code> from the given <code>InputStream</code>
     * @param is
     * @return
     * @throws SAXException
     * @throws JAXBException
     *
     * @see org.jpmml.model.PMMLUtil#unmarshal(InputStream)
     */
    public static PMML loadFromInputStream(InputStream is) throws SAXException, JAXBException {
        return org.jpmml.model.PMMLUtil.unmarshal(is);
    }

}
