/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.xstream.impl.domain.solution;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.xstream.impl.testdata.domain.XStreamTestdataEntity;
import org.optaplanner.persistence.xstream.impl.testdata.domain.XStreamTestdataSolution;
import org.optaplanner.persistence.xstream.impl.testdata.domain.XStreamTestdataValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class XStreamSolutionFileIOTest {

    private static File solutionTestDir;

    @BeforeClass
    public static void setup() throws IOException {
        solutionTestDir = new File("target/solutionTest/");
        solutionTestDir.mkdirs();
    }

    @Test
    public void readAndWrite() {
        XStreamSolutionFileIO<XStreamTestdataSolution> solutionFileIO = new XStreamSolutionFileIO<>(XStreamTestdataSolution.class);
        File file = new File(solutionTestDir, "testdataSolution.xml");

        XStreamTestdataSolution original = new XStreamTestdataSolution("s1");
        XStreamTestdataValue originalV1 = new XStreamTestdataValue("v1");
        original.setValueList(Arrays.asList(originalV1, new XStreamTestdataValue("v2")));
        original.setEntityList(Arrays.asList(
                new XStreamTestdataEntity("e1"), new XStreamTestdataEntity("e2", originalV1), new XStreamTestdataEntity("e3")));
        original.setScore(SimpleScore.of(-123));
        solutionFileIO.write(original, file);
        XStreamTestdataSolution copy = solutionFileIO.read(file);

        assertNotSame(original, copy);
        assertCode("s1", copy);
        assertAllCodesOfIterator(copy.getValueList().iterator(), "v1", "v2");
        assertAllCodesOfIterator(copy.getEntityList().iterator(), "e1", "e2", "e3");
        XStreamTestdataValue copyV1 = copy.getValueList().get(0);
        XStreamTestdataEntity copyE2 = copy.getEntityList().get(1);
        assertCode("v1", copyE2.getValue());
        assertSame(copyV1, copyE2.getValue());
        assertEquals(SimpleScore.of(-123), copy.getScore());
    }

}
