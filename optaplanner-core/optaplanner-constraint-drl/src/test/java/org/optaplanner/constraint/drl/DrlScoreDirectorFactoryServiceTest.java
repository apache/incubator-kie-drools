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
