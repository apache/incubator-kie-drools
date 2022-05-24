/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.drl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

class DrlScoreDirectorFactoryServiceTest {

    private ScoreDirectorFactory<TestdataSolution> buildScoreDirectoryFactory(ScoreDirectorFactoryConfig config) {
        Supplier<AbstractScoreDirectorFactory<TestdataSolution, SimpleScore>> supplier =
                new DrlScoreDirectorFactoryService<TestdataSolution, SimpleScore>()
                        .buildScoreDirectorFactory(getClass().getClassLoader(),
                                TestdataSolution.buildSolutionDescriptor(), config);
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    @Test
    void invalidDrlResource_throwsException() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withScoreDrls("org/optaplanner/constraint/drl/invalidDroolsConstraints.drl");
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> buildScoreDirectoryFactory(config))
                .withMessageContaining("scoreDrl")
                .withRootCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void nonExistingDrlResource_throwsException() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withScoreDrls("nonExisting.drl");
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> buildScoreDirectoryFactory(config))
                .withMessageContaining("scoreDrl");
    }

    @Test
    void nonExistingDrlFile_throwsException() {
        ScoreDirectorFactoryConfig config = new ScoreDirectorFactoryConfig()
                .withScoreDrlFiles(new File("nonExisting.drl"));
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> buildScoreDirectoryFactory(config))
                .withMessageContaining("scoreDrl");
    }

}
