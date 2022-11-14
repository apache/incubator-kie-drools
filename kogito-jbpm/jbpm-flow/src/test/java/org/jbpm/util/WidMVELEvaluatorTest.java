/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.jbpm.process.core.datatype.DataType;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Test;
import org.mvel2.CompileException;

import static org.assertj.core.api.Assertions.assertThat;

public class WidMVELEvaluatorTest extends AbstractBaseTest {

    public void addLogger() {
        logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testWidNoImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResourceContent("/wids/test-noimports.wid")));
    }

    @Test
    public void testWidBackwardsCompatImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResourceContent("/wids/test-backwardscompat.wid")));
    }

    @Test
    public void testWidBackwardsCompatPkgImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResourceContent("/wids/test-backwardscompatpkgimport.wid")));
    }

    @Test
    public void testWidBackwardsCompatMixedAndMissingImports() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResourceContent("/wids/test-backwardscompatmixedandmissingimports.wid")));
    }

    @Test
    public void testWidCustomDataType() throws Exception {
        assertCorrectWids(WidMVELEvaluator.eval(getResourceContent("/wids/test-customdatatype.wid")));
    }

    @Test
    public void testWidCustomDataTypeNoImport() {
        try {
            assertCorrectWids(WidMVELEvaluator.eval(getResourceContent("/wids/test-customdatatypenoimport.wid")));
        } catch (Throwable t) {
            assertThat(t).isInstanceOf(CompileException.class);
        }
    }

    protected String getResourceContent(String path) throws Exception {
        return new String(Files.readAllBytes(Paths.get(this.getClass().getResource(path).getPath())), "UTF-8");
    }

    protected String getResouce(String path) throws Exception {
        return this.getClass().getResource(path).getFile();
    }

    private void assertCorrectWids(Object wids) {
        assertThat(wids).isNotNull();
        List<Map<String, Object>> widsMap = (List<Map<String, Object>>) wids;
        assertThat(widsMap).hasSize(2);

        Map<String, Object> firstWid = widsMap.get(0);
        assertThat(firstWid).isNotNull().containsEntry("name", "MyFirstWorkItem");

        Map<String, DataType> firstWidParams = (Map<String, DataType>) firstWid.get("parameters");
        assertThat(firstWidParams).isNotNull().hasSize(6);

        Map<String, Object> firstWidParamValues = (Map<String, Object>) firstWid.get("parameterValues");
        assertThat(firstWidParamValues).isNotNull().hasSize(1);

        Map<String, Object> secondWid = widsMap.get(1);
        assertThat(secondWid).isNotNull().containsEntry("name", "MySecondWorkItem");

        Map<String, DataType> secondWidParams = (Map<String, DataType>) secondWid.get("parameters");
        assertThat(secondWidParams).isNotNull().hasSize(6);

        Map<String, Object> secondWidParamValues = (Map<String, Object>) secondWid.get("parameterValues");
        assertThat(secondWidParamValues).isNotNull().hasSize(1);
    }
}
