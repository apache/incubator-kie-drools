/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.compiler.commons.builders;

import java.io.IOException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.github.javaparser.utils.Pair;
import org.dmg.pmml.PMML;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.builders.KiePMMLModelCodegenUtils.getMissingValueReplacementsMap;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelCodegenUtilsTest {

    private static final String MODEL_FILE = "MissingDataRegression.pmml";

    @Test
    public void testGetMissingValueReplacementsMap() throws IOException, JAXBException, SAXException {
        PMML pmml = KiePMMLUtil.load(getFileInputStream(MODEL_FILE), MODEL_FILE);

        Map<String, Pair<DATA_TYPE, String>> output = getMissingValueReplacementsMap(pmml.getDataDictionary(), pmml.getModels().get(0));

        assertTrue(output.containsKey("x"));
        assertEquals(DATA_TYPE.DOUBLE, output.get("x").a);
        assertEquals("5", output.get("x").b);

        assertTrue(output.containsKey("y"));
        assertEquals(DATA_TYPE.STRING, output.get("y").a);
        assertEquals("classB", output.get("y").b);
    }

}
