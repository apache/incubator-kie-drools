package org.optaplanner.constraint.drl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.drools.ancompiler.KieBaseUpdaterANC;
import org.drools.model.codegen.ExecutableModelProject;
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
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DrlScoreDirectorFactoryService<Solution_, Score_ extends Score<Score_>>
        implements ScoreDirectorFactoryService<Solution_, Score_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrlScoreDirectorFactoryService.class);
    private static final AtomicBoolean DRL_DEPRECATION_MESSAGE_SHOWN = new AtomicBoolean();

    @Override
    public ScoreDirectorType getSupportedScoreDirectorType() {
        return ScoreDirectorType.DRL;
    }

    @Override
    public Supplier<AbstractScoreDirectorFactory<Solution_, Score_>> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config) {
        if (ConfigUtils.isEmptyCollection(config.getScoreDrlList())
                && ConfigUtils.isEmptyCollection(config.getScoreDrlFileList())) {
            if (config.getKieBaseConfigurationProperties() != null) {
                throw new IllegalArgumentException(
                        "If kieBaseConfigurationProperties (" + config.getKieBaseConfigurationProperties()
                                + ") is not null, the scoreDrlList (" + config.getScoreDrlList()
                                + ") or the scoreDrlFileList (" + config.getScoreDrlFileList() + ") must not be empty.");
            }
            return null;
        }

        if (!DRL_DEPRECATION_MESSAGE_SHOWN.getAndSet(true)) {
            LOGGER.info("Score DRL is deprecated and will be removed in a future major version of OptaPlanner.\n" +
                    "Consider migrating to the Constraint Streams API.\n" +
                    "See migration recipe: https://www.optaplanner.org/download/upgradeRecipe/drl-to-constraint-streams-migration.html");
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

    private DrlScoreDirectorFactory<Solution_, Score_> buildScoreDirectorFactory(ClassLoader classLoader,
            SolutionDescriptor<Solution_> solutionDescriptor, ScoreDirectorFactoryConfig config,
            List<String> scoreDrlList) {
        KieBase kieBase;
        if (config.getGizmoKieBaseSupplier() != null) {
            kieBase = ((Supplier<KieBase>) config.getGizmoKieBaseSupplier()).get();
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

        boolean isDroolsAlphaNetworkEnabled =
                Objects.requireNonNullElse(config.isDroolsAlphaNetworkCompilationEnabled(), true);
        if (isDroolsAlphaNetworkEnabled) {
            KieBaseUpdaterANC.generateAndSetInMemoryANC(kieBase); // Enable Alpha Network Compiler for performance.
        }
        return new DrlScoreDirectorFactory<>(solutionDescriptor, kieBase);
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
