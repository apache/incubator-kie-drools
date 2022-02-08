package org.optaplanner.constraint.drl.testgen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.kie.api.KieBase;
import org.optaplanner.constraint.drl.AbstractDrlScoreDirectorFactoryService;
import org.optaplanner.constraint.drl.DrlScoreDirectorFactory;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;

public final class TestGenDrlScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        extends AbstractDrlScoreDirectorFactoryService<Solution_, Score_> {

    @Override
    public Supplier<AbstractScoreDirectorFactory<Solution_, Score_>> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config) {
        if (!isTestGenRequested()) {
            return null; // DrlScoreDirectorFactoryService will be called.
        }

        if (ConfigUtils.isEmptyCollection(config.getScoreDrlList())
                && ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
            if (config.getKieBaseConfigurationProperties() != null) {
                throw new IllegalArgumentException(
                        "If kieBaseConfigurationProperties (" + config.getKieBaseConfigurationProperties()
                                + ") is not null, the scoreDrlList (" + config.getScoreDrlList()
                                + ") or the scoreDrlFileList (" + config.getScoreDrlFileList() + ") must not be empty.");
            }
            throw new IllegalArgumentException(
                    "If " + GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME + " system property (true) is set, "
                            + "the scoreDrlList (" + config.getScoreDrlList() + ") or the scoreDrlFileList ("
                            + config.getScoreDrlFileList() + ") must not be empty.");
        }

        List<String> scoreDrlList = new ArrayList<>();
        if (config.getGizmoKieBaseSupplier() == null) {
            if (!ConfigUtils.isEmptyCollection(config.getScoreDrlList())) {
                for (String scoreDrl : config.getScoreDrlList()) {
                    if (scoreDrl == null) {
                        throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") cannot be null.");
                    }
                    scoreDrlList.add(scoreDrl);
                }
            }
        }

        return () -> buildScoreDirectorFactory(classLoader, solutionDescriptor, config, scoreDrlList);
    }

    @Override
    protected DrlScoreDirectorFactory<Solution_, Score_> createScoreDirectorFactory(ScoreDirectorFactoryConfig config,
            SolutionDescriptor<Solution_> solutionDescriptor, KieBase kieBase) {
        return new TestGenDrlScoreDirectorFactory<>(solutionDescriptor, kieBase, config.getScoreDrlList(),
                config.getScoreDrlFileList());
    }
}
