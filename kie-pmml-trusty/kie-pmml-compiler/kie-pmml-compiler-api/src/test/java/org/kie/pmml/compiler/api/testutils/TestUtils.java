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
package org.kie.pmml.compiler.api.testutils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.dmg.pmml.PMML;
import org.drools.util.FileUtils;

public class TestUtils {

    /**
     * Load a <code>PMML</code> from the given <b>file</b>
     * @param fileName
     * @return
     * @throws IOException
     */
    public static PMML loadFromFile(String fileName) throws IOException {
        return loadFromInputStream(FileUtils.getFileInputStream(fileName));
    }

    /**
     * Load a <code>PMML</code> from the given <b>xml source</b>
     * @param xmlSource
     * @return
     */
    public static PMML loadFromSource(String xmlSource) {
        return loadFromInputStream(new ByteArrayInputStream(xmlSource.getBytes()));
    }

    /**
     * Load a <code>PMML</code> from the given <code>InputStream</code>
     * @param is
     * @return
     * @see org.jpmml.model.PMMLUtil#unmarshal(InputStream)
     */
    public static PMML loadFromInputStream(InputStream is) {
        try {
            return org.jpmml.model.PMMLUtil.unmarshal(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
