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
package org.kie.pmml.compiler.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.PMML;
import org.xml.sax.SAXException;

/**
 * Utility class to decouple <code>PMMLCompilerExecutor</code> from actual marshalling model/implementation.
 * Currently, it directly uses {@link org.jpmml.model.PMMLUtil} and {@link org.dmg.pmml.PMML}
 */
public class KiePMMLUtil {

    /**
     * @param source
     * @return
     * @throws SAXException
     * @throws JAXBException
     */
    public static PMML load(String source) throws SAXException, JAXBException {
        return load(new ByteArrayInputStream(source.getBytes()));
    }

    /**
     * @param is
     * @return
     * @throws SAXException
     * @throws JAXBException
     * @see org.jpmml.model.PMMLUtil#unmarshal(InputStream)
     */
    public static PMML load(InputStream is) throws SAXException, JAXBException {
        return org.jpmml.model.PMMLUtil.unmarshal(is);
    }
}
