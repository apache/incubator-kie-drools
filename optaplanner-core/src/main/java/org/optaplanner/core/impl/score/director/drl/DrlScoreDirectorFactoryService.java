package org.optaplanner.core.impl.score.director.drl;

import java.io.File;
import java.util.Map;

import org.drools.ancompiler.KieBaseUpdaterANC;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.KieBaseMutabilityOption;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.utils.KieHelper;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;
import org.optaplanner.core.impl.score.director.drl.testgen.TestGenDrlScoreDirectorFactory;

public final class DrlScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        implements ScoreDirectorFactoryService<Solution_, Score_> {

    private static final String GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME = "optaplanner.drools.generateTestOnError";

    @Override
    public ScoreDirectorType getSupportedScoreDirectorType() {
        return ScoreDirectorType.DRL;
    }

    @Override
    public DrlScoreDirectorFactory<Solution_, Score_> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config) {
        boolean generateDroolsTestOnError =
                Boolean.parseBoolean(System.getProperty(GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME, "false"));

        if (ConfigUtils.isEmptyCollection(config.getScoreDrlList())
                && ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
            if (config.getKieBaseConfigurationProperties() != null) {
                throw new IllegalArgumentException(
                        "If kieBaseConfigurationProperties (" + config.getKieBaseConfigurationProperties()
                                + ") is not null, the scoreDrlList (" + config.getScoreDrlList()
                                + ") or the scoreDrlFileList (" + config.getScoreDrlFileList() + ") must not be empty.");
            }
            if (generateDroolsTestOnError) {
                throw new IllegalArgumentException(
                        "If " + GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME + " system property (" +
                                generateDroolsTestOnError + ") is set, the scoreDrlList (" + config.getScoreDrlList()
                                + ") or the scoreDrlFileList (" + config.getScoreDrlFileList() + ") must not be empty.");
            }
            return null;
        }

        try {
            KieBase kieBase;
            if (config.getGizmoKieBaseSupplier() != null) {
                kieBase = config.getGizmoKieBaseSupplier().get();
            } else {
                // Can't put this code in KieBaseExtractor since it reference
                // KieRuntimeBuilder, which is an optional dependency
                KieHelper kieHelper = new KieHelper(PropertySpecificOption.ALLOWED)
                        .setClassLoader(classLoader);
                if (!ConfigUtils.isEmptyCollection(config.getScoreDrlList())) {
                    for (String scoreDrl : config.getScoreDrlList()) {
                        if (scoreDrl == null) {
                            throw new IllegalArgumentException("The scoreDrl (" + scoreDrl + ") cannot be null.");
                        }
                        kieHelper.addResource(new ClassPathResource(scoreDrl, classLoader));
                    }
                }
                if (!ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
                    for (File scoreDrlFile : config.getScoreDrlFileList()) {
                        kieHelper.addResource(new FileSystemResource(scoreDrlFile));
                    }
                }
                KieBaseConfiguration kieBaseConfiguration = buildKieBaseConfiguration(config, KieServices.get());
                kieBaseConfiguration.setOption(KieBaseMutabilityOption.DISABLED); // Performance improvement.
                kieBase = kieHelper.build(ExecutableModelProject.class, kieBaseConfiguration);
            }

            if (config.isDroolsAlphaNetworkCompilationEnabled()) {
                KieBaseUpdaterANC.generateAndSetInMemoryANC(kieBase); // Enable Alpha Network Compiler for performance.
            }
            if (generateDroolsTestOnError) {
                return new TestGenDrlScoreDirectorFactory<>(solutionDescriptor, kieBase, config.getScoreDrlList(),
                        config.getScoreDrlFileList());
            } else {
                return new DrlScoreDirectorFactory<>(solutionDescriptor, kieBase);
            }
        } catch (Exception ex) {
            throw new IllegalStateException("There is an error in a scoreDrl or scoreDrlFile.", ex);
        }
    }

    private KieBaseConfiguration buildKieBaseConfiguration(ScoreDirectorFactoryConfig config,
            KieServices kieServices) {
        KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        if (config.getKieBaseConfigurationProperties() != null) {
            for (Map.Entry<String, String> entry : config.getKieBaseConfigurationProperties().entrySet()) {
                kieBaseConfiguration.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return kieBaseConfiguration;
    }

}
