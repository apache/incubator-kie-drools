/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jbpm.process.core.datatype.DataType;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.mvel2.CompileException;

import static org.junit.Assert.*;

public class WidMVELEvaluatorTest extends AbstractBaseTest {

    public void addLogger() {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testWidNoImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResouceContent("/wids/test-noimports.wid")));
    }

    @Test
    public void testWidBackwardsCompatImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResouceContent("/wids/test-backwardscompat.wid")));
    }

    @Test
    public void testWidBackwardsCompatPkgImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResouceContent("/wids/test-backwardscompatpkgimport.wid")));
    }

    @Test
    public void testWidBackwardsCompatMixedAndMissingImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResouceContent("/wids/test-backwardscompatmixedandmissingimports.wid")));
    }

    @Test
    public void testWidCustomDataType() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResouceContent("/wids/test-customdatatype.wid")));
    }

    @Test
    public void testWidCustomDataTypeNoImport() {
        try {
            assertCorrectWids(WidMVELEvaluator.eval(getResouceContent("/wids/test-customdatatypenoimport.wid")));
        } catch(Throwable t) {
            assertTrue(t instanceof CompileException);
        }
    }

    protected String getResouceContent(String path) throws Exception {
        return IOUtils.toString(this.getClass().getResourceAsStream(path),
                                "UTF-8");
    }

    protected String getResouce(String path) throws Exception {
        return this.getClass().getResource(path).getFile();
    }

    private void assertCorrectWids(Object wids) {
        assertNotNull(wids);
        List<Map<String, Object>> widsMap = (List<Map<String, Object>>) wids;
        assertEquals(2,
                     widsMap.size());

        Map<String, Object> firstWid = widsMap.get(0);
        assertNotNull(firstWid);

        assertEquals("MyFirstWorkItem",
                     firstWid.get("name"));

        Map<String, DataType> firstWidParams = (Map<String, DataType>) firstWid.get("parameters");
        assertNotNull(firstWidParams);
        assertEquals(6,
                     firstWidParams.size());

        Map<String, Object> firstWidParamValues = (Map<String, Object>) firstWid.get("parameterValues");
        assertNotNull(firstWidParamValues);
        assertEquals(1,
                     firstWidParamValues.size());

        Map<String, Object> secondWid = widsMap.get(1);
        assertNotNull(secondWid);

        assertEquals("MySecondWorkItem",
                     secondWid.get("name"));

        Map<String, DataType> secondWidParams = (Map<String, DataType>) secondWid.get("parameters");
        assertNotNull(secondWidParams);
        assertEquals(6,
                     secondWidParams.size());

        Map<String, Object> secondWidParamValues = (Map<String, Object>) secondWid.get("parameterValues");
        assertNotNull(secondWidParamValues);
        assertEquals(1,
                     secondWidParamValues.size());
    }
}
