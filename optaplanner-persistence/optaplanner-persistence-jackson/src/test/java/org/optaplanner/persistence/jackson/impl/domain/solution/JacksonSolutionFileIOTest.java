/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.persistence.jackson.impl.domain.solution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllCodesOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertCode;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.persistence.jackson.impl.testdata.domain.JacksonTestdataEntity;
import org.optaplanner.persistence.jackson.impl.testdata.domain.JacksonTestdataSolution;
import org.optaplanner.persistence.jackson.impl.testdata.domain.JacksonTestdataValue;

class JacksonSolutionFileIOTest {

    private static File solutionTestDir;

    @BeforeAll
    static void setup() {
        solutionTestDir = new File("target/solutionTest/");
        solutionTestDir.mkdirs();
    }

    @Test
    void readAndWrite() {
        JacksonSolutionFileIO<JacksonTestdataSolution> solutionFileIO =
                new JacksonSolutionFileIO<>(JacksonTestdataSolution.class);
        File file = new File(solutionTestDir, "testdataSolution.json");

        JacksonTestdataSolution original = new JacksonTestdataSolution("s1");
        JacksonTestdataValue originalV1 = new JacksonTestdataValue("v1");
        original.setValueList(Arrays.asList(originalV1, new JacksonTestdataValue("v2")));
        original.setEntityList(Arrays.asList(
                new JacksonTestdataEntity("e1"), new JacksonTestdataEntity("e2", originalV1), new JacksonTestdataEntity("e3")));
        original.setScore(SimpleScore.of(-321));
        solutionFileIO.write(original, file);
        JacksonTestdataSolution copy = solutionFileIO.read(file);

        assertThat(copy).isNotSameAs(original);
        assertCode("s1", copy);
        assertAllCodesOfIterator(copy.getValueList().iterator(), "v1", "v2");
        assertAllCodesOfIterator(copy.getEntityList().iterator(), "e1", "e2", "e3");
        JacksonTestdataValue copyV1 = copy.getValueList().get(0);
        JacksonTestdataEntity copyE2 = copy.getEntityList().get(1);
        assertCode("v1", copyE2.getValue());
        assertThat(copyE2.getValue()).isSameAs(copyV1);
        assertThat(copy.getScore()).isEqualTo(SimpleScore.of(-321));
    }

}
