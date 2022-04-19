package org.optaplanner.constraint.drl;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.drools.ancompiler.KieBaseUpdaterANC;
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

public abstract class AbstractDrlScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        implements ScoreDirectorFactoryService<Solution_, Score_> {

    public static final String GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME = "optaplanner.drools.generateTestOnError";

    @Override
    public ScoreDirectorType getSupportedScoreDirectorType() {
        return ScoreDirectorType.DRL;
    }

    protected boolean isTestGenRequested() {
        return Boolean.parseBoolean(System.getProperty(GENERATE_DROOLS_TEST_ON_ERROR_PROPERTY_NAME, "false"));
    }

    protected abstract DrlScoreDirectorFactory<Solution_, Score_> createScoreDirectorFactory(
            ScoreDirectorFactoryConfig config, SolutionDescriptor<Solution_> solutionDescriptor, KieBase kieBase);

    protected DrlScoreDirectorFactory<Solution_, Score_> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config,
            List<String> scoreDrlList) {
        KieBase kieBase;
        if (config.getGizmoKieBaseSupplier() != null) {
            kieBase = config.getGizmoKieBaseSupplier().get();
        } else {
            KieHelper kieHelper = new KieHelper(PropertySpecificOption.ALLOWED)
                    .setClassLoader(classLoader);
            scoreDrlList.forEach(scoreDrl -> kieHelper
                    .addResource(KieServices.get().getResources().newClassPathResource(scoreDrl, classLoader)));
            if (!ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
                for (File scoreDrlFile : config.getScoreDrlFileList()) {
                    kieHelper.addResource(KieServices.get().getResources().newFileSystemResource(scoreDrlFile));
                }
            }
            KieBaseConfiguration kieBaseConfiguration = buildKieBaseConfiguration(config, KieServices.get());
            kieBaseConfiguration.setOption(KieBaseMutabilityOption.DISABLED); // Performance improvement.
            try {
                kieBase = kieHelper.build(ExecutableModelProject.class, kieBaseConfiguration);
            } catch (Exception ex) {
                throw new IllegalStateException("There is an error in a scoreDrl or scoreDrlFile.", ex);
            }
        }

        if (config.isDroolsAlphaNetworkCompilationEnabled()) {
            KieBaseUpdaterANC.generateAndSetInMemoryANC(kieBase); // Enable Alpha Network Compiler for performance.
        }
        return createScoreDirectorFactory(config, solutionDescriptor, kieBase);
    }

    private static KieBaseConfiguration buildKieBaseConfiguration(ScoreDirectorFactoryConfig config,
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
