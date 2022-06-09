package org.optaplanner.spring.boot.autoconfigure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.calculator.IncrementalScoreCalculator;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.persistence.jackson.api.OptaPlannerJacksonModule;
import org.optaplanner.spring.boot.autoconfigure.config.OptaPlannerProperties;
import org.optaplanner.spring.boot.autoconfigure.config.SolverManagerProperties;
import org.optaplanner.spring.boot.autoconfigure.config.SolverProperties;
import org.optaplanner.spring.boot.autoconfigure.config.TerminationProperties;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.optaplanner.test.api.score.stream.MultiConstraintVerification;
import org.optaplanner.test.api.score.stream.SingleConstraintVerification;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.Module;

@Configuration
@ConditionalOnClass({ SolverConfig.class, SolverFactory.class, ScoreManager.class, SolverManager.class })
@ConditionalOnMissingBean({ SolverConfig.class, SolverFactory.class, ScoreManager.class, SolverManager.class })
@EnableConfigurationProperties({ OptaPlannerProperties.class })
public class OptaPlannerAutoConfiguration implements BeanClassLoaderAware {

    private final ApplicationContext context;
    private final OptaPlannerProperties optaPlannerProperties;
    private ClassLoader beanClassLoader;

    protected OptaPlannerAutoConfiguration(ApplicationContext context,
            OptaPlannerProperties optaPlannerProperties) {
        this.context = context;
        this.optaPlannerProperties = optaPlannerProperties;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Bean
    @ConditionalOnMissingBean
    public <Solution_, ProblemId_> SolverManager<Solution_, ProblemId_> solverManager(SolverFactory solverFactory) {
        // TODO supply ThreadFactory
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();
        SolverManagerProperties solverManagerProperties = optaPlannerProperties.getSolverManager();
        if (solverManagerProperties != null) {
            if (solverManagerProperties.getParallelSolverCount() != null) {
                solverManagerConfig.setParallelSolverCount(solverManagerProperties.getParallelSolverCount());
            }
        }
        return SolverManager.create(solverFactory, solverManagerConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public <Solution_, Score_ extends Score<Score_>> ScoreManager<Solution_, Score_> scoreManager(
            SolverFactory solverFactory) {
        return ScoreManager.create(solverFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public <Solution_> SolverFactory<Solution_> solverFactory(SolverConfig solverConfig) {
        return SolverFactory.create(solverConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public SolverConfig solverConfig() {
        String solverConfigXml = optaPlannerProperties.getSolverConfigXml();
        SolverConfig solverConfig;
        if (solverConfigXml != null) {
            if (beanClassLoader.getResource(solverConfigXml) == null) {
                throw new IllegalStateException("Invalid optaplanner.solverConfigXml property (" + solverConfigXml
                        + "): that classpath resource does not exist.");
            }
            solverConfig = SolverConfig.createFromXmlResource(solverConfigXml, beanClassLoader);
        } else if (beanClassLoader.getResource(OptaPlannerProperties.DEFAULT_SOLVER_CONFIG_URL) != null) {
            solverConfig = SolverConfig.createFromXmlResource(
                    OptaPlannerProperties.DEFAULT_SOLVER_CONFIG_URL, beanClassLoader);
        } else {
            solverConfig = new SolverConfig(beanClassLoader);
        }

        applySolverProperties(solverConfig);
        return solverConfig;
    }

    // @Bean wrapped by static class to avoid classloading issues if dependencies are absent
    @ConditionalOnClass({ ConstraintVerifier.class })
    @ConditionalOnMissingBean({ ConstraintVerifier.class })
    static class OptaPlannerConstraintVerifierConfiguration {

        @Bean
        @SuppressWarnings("unchecked")
        <ConstraintProvider_ extends ConstraintProvider, SolutionClass_>
                ConstraintVerifier<ConstraintProvider_, SolutionClass_> constraintVerifier(SolverConfig solverConfig) {
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
            if (scoreDirectorFactoryConfig.getConstraintProviderClass() == null) {
                // Return a mock ConstraintVerifier so not having ConstraintProvider doesn't crash tests
                // (Cannot create custom condition that checks SolverConfig, since that
                //  requires OptaPlannerAutoConfiguration to have a no-args constructor)
                final String noConstraintProviderErrorMsg =
                        "Cannot provision a ConstraintVerifier because there is no ConstraintProvider class.";
                return new ConstraintVerifier<ConstraintProvider_, SolutionClass_>() {
                    @Override
                    public ConstraintVerifier<ConstraintProvider_, SolutionClass_>
                            withConstraintStreamImplType(ConstraintStreamImplType constraintStreamImplType) {
                        throw new UnsupportedOperationException(noConstraintProviderErrorMsg);
                    }

                    @Override
                    public ConstraintVerifier<ConstraintProvider_, SolutionClass_>
                            withDroolsAlphaNetworkCompilationEnabled(boolean droolsAlphaNetworkCompilationEnabled) {
                        throw new UnsupportedOperationException(noConstraintProviderErrorMsg);
                    }

                    @Override
                    public SingleConstraintVerification<SolutionClass_>
                            verifyThat(BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
                        throw new UnsupportedOperationException(noConstraintProviderErrorMsg);
                    }

                    @Override
                    public MultiConstraintVerification<SolutionClass_> verifyThat() {
                        throw new UnsupportedOperationException(noConstraintProviderErrorMsg);
                    }
                };
            }

            return (ConstraintVerifier<ConstraintProvider_, SolutionClass_>) ConstraintVerifier.create(solverConfig);
        }
    }

    private void applySolverProperties(SolverConfig solverConfig) {
        IncludeAbstractClassesEntityScanner entityScanner = new IncludeAbstractClassesEntityScanner(this.context);
        if (solverConfig.getSolutionClass() == null) {
            solverConfig.setSolutionClass(findSolutionClass(entityScanner));
        }
        if (solverConfig.getEntityClassList() == null) {
            solverConfig.setEntityClassList(findEntityClassList(entityScanner));
        }
        applyScoreDirectorFactoryProperties(solverConfig);
        SolverProperties solverProperties = optaPlannerProperties.getSolver();
        if (solverProperties != null) {
            if (solverProperties.getEnvironmentMode() != null) {
                solverConfig.setEnvironmentMode(solverProperties.getEnvironmentMode());
            }
            if (solverProperties.getDomainAccessType() != null) {
                solverConfig.setDomainAccessType(solverProperties.getDomainAccessType());
            }
            if (solverProperties.getDaemon() != null) {
                solverConfig.setDaemon(solverProperties.getDaemon());
            }
            if (solverProperties.getMoveThreadCount() != null) {
                solverConfig.setMoveThreadCount(solverProperties.getMoveThreadCount());
            }
            applyTerminationProperties(solverConfig, solverProperties.getTermination());
        }
    }

    private Class<?> findSolutionClass(IncludeAbstractClassesEntityScanner entityScanner) {
        Set<Class<?>> solutionClassSet;
        try {
            solutionClassSet = entityScanner.scan(PlanningSolution.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Scanning for @" + PlanningSolution.class.getSimpleName()
                    + " annotations failed.", e);
        }
        if (solutionClassSet.size() > 1) {
            throw new IllegalStateException("Multiple classes (" + solutionClassSet
                    + ") found with a @" + PlanningSolution.class.getSimpleName() + " annotation.");
        }
        if (solutionClassSet.isEmpty()) {
            throw new IllegalStateException("No classes (" + solutionClassSet
                    + ") found with a @" + PlanningSolution.class.getSimpleName() + " annotation.\n"
                    + "Maybe your @" + PlanningSolution.class.getSimpleName() + " annotated class "
                    + " is not in a subpackage of your @" + SpringBootApplication.class.getSimpleName()
                    + " annotated class's package.\n"
                    + "Maybe move your planning solution class to your application class's (sub)package"
                    + " (or use @" + EntityScan.class.getSimpleName() + ").");
        }
        return solutionClassSet.iterator().next();
    }

    private List<Class<?>> findEntityClassList(IncludeAbstractClassesEntityScanner entityScanner) {
        Set<Class<?>> entityClassSet;
        try {
            entityClassSet = entityScanner.scan(PlanningEntity.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Scanning for @" + PlanningEntity.class.getSimpleName() + " failed.", e);
        }
        if (entityClassSet.isEmpty()) {
            throw new IllegalStateException("No classes (" + entityClassSet
                    + ") found with a @" + PlanningEntity.class.getSimpleName() + " annotation.\n"
                    + "Maybe your @" + PlanningEntity.class.getSimpleName() + " annotated class(es) "
                    + " are not in a subpackage of your @" + SpringBootApplication.class.getSimpleName()
                    + " annotated class's package.\n"
                    + "Maybe move your planning entity classes to your application class's (sub)package"
                    + " (or use @" + EntityScan.class.getSimpleName() + ").");
        }
        return new ArrayList<>(entityClassSet);
    }

    protected void applyScoreDirectorFactoryProperties(SolverConfig solverConfig) {
        String constraintsDrlFromProperty = constraintsDrl();
        String defaultConstraintsDrl = defaultConstraintsDrl();
        String effectiveConstraintsDrl =
                constraintsDrlFromProperty != null ? constraintsDrlFromProperty : defaultConstraintsDrl;
        if (solverConfig.getScoreDirectorFactoryConfig() == null) {
            solverConfig.setScoreDirectorFactoryConfig(defaultScoreDirectoryFactoryConfig(effectiveConstraintsDrl));
        } else {
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = solverConfig.getScoreDirectorFactoryConfig();
            if (constraintsDrlFromProperty != null) {
                scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(constraintsDrlFromProperty));
            } else {
                if (scoreDirectorFactoryConfig.getScoreDrlList() == null && defaultConstraintsDrl != null) {
                    scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(defaultConstraintsDrl));
                }
            }
        }
    }

    protected String constraintsDrl() {
        String constraintsDrl = optaPlannerProperties.getScoreDrl();

        if (constraintsDrl != null) {
            if (beanClassLoader.getResource(constraintsDrl) == null) {
                throw new IllegalStateException("Invalid " + OptaPlannerProperties.SCORE_DRL_PROPERTY
                        + " property (" + constraintsDrl + "): that classpath resource does not exist.");
            }
        }
        return constraintsDrl;
    }

    protected String defaultConstraintsDrl() {
        return beanClassLoader.getResource(OptaPlannerProperties.DEFAULT_CONSTRAINTS_DRL_URL) != null
                ? OptaPlannerProperties.DEFAULT_CONSTRAINTS_DRL_URL
                : null;
    }

    private ScoreDirectorFactoryConfig defaultScoreDirectoryFactoryConfig(String constraintsDrl) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setEasyScoreCalculatorClass(findImplementingClass(EasyScoreCalculator.class));
        scoreDirectorFactoryConfig.setConstraintProviderClass(findImplementingClass(ConstraintProvider.class));
        scoreDirectorFactoryConfig
                .setIncrementalScoreCalculatorClass(findImplementingClass(IncrementalScoreCalculator.class));
        if (constraintsDrl != null) {
            scoreDirectorFactoryConfig.setScoreDrlList(Collections.singletonList(constraintsDrl));
        }

        if (scoreDirectorFactoryConfig.getEasyScoreCalculatorClass() == null
                && scoreDirectorFactoryConfig.getConstraintProviderClass() == null
                && scoreDirectorFactoryConfig.getIncrementalScoreCalculatorClass() == null
                && scoreDirectorFactoryConfig.getScoreDrlList() == null) {
            throw new IllegalStateException("No classes found that implement "
                    + EasyScoreCalculator.class.getSimpleName() + ", "
                    + ConstraintProvider.class.getSimpleName() + " or "
                    + IncrementalScoreCalculator.class.getSimpleName() + ".\n"
                    + "Neither was a property " + OptaPlannerProperties.SCORE_DRL_PROPERTY + " defined, nor a "
                    + OptaPlannerProperties.DEFAULT_CONSTRAINTS_DRL_URL + " resource found.\n"
                    + "Maybe your " + ConstraintProvider.class.getSimpleName() + " class "
                    + " is not in a subpackage of your @" + SpringBootApplication.class.getSimpleName()
                    + " annotated class's package.\n"
                    + "Maybe move your " + ConstraintProvider.class.getSimpleName()
                    + " class to your application class's (sub)package"
                    + " (or use @" + EntityScan.class.getSimpleName() + ").");
        }
        return scoreDirectorFactoryConfig;
    }

    private <T> Class<? extends T> findImplementingClass(Class<T> targetClass) {
        if (!AutoConfigurationPackages.has(context)) {
            return null;
        }
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.setEnvironment(context.getEnvironment());
        scanner.setResourceLoader(context);
        scanner.addIncludeFilter(new AssignableTypeFilter(targetClass));

        EntityScanPackages entityScanPackages = EntityScanPackages.get(context);

        Set<String> packages = new HashSet<>();
        packages.addAll(AutoConfigurationPackages.get(context));
        packages.addAll(entityScanPackages.getPackageNames());
        List<Class<? extends T>> classList = packages.stream()
                .flatMap(basePackage -> scanner.findCandidateComponents(basePackage).stream())
                // findCandidateComponents can return the same package for different base packages
                .distinct()
                .sorted(Comparator.comparing(BeanDefinition::getBeanClassName))
                .map(candidate -> {
                    try {
                        Class<? extends T> clazz = ClassUtils.forName(candidate.getBeanClassName(), context.getClassLoader())
                                .asSubclass(targetClass);
                        return clazz;
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("The " + targetClass.getSimpleName() + " class ("
                                + candidate.getBeanClassName() + ") cannot be found.", e);
                    }
                })
                .collect(Collectors.toList());
        if (classList.size() > 1) {
            throw new IllegalStateException("Multiple classes (" + classList
                    + ") found that implement the interface " + targetClass.getSimpleName() + ".");
        }
        if (classList.isEmpty()) {
            return null;
        }
        return classList.get(0);
    }

    static void applyTerminationProperties(SolverConfig solverConfig, TerminationProperties terminationProperties) {
        TerminationConfig terminationConfig = solverConfig.getTerminationConfig();
        if (terminationConfig == null) {
            terminationConfig = new TerminationConfig();
            solverConfig.setTerminationConfig(terminationConfig);
        }
        if (terminationProperties != null) {
            if (terminationProperties.getSpentLimit() != null) {
                terminationConfig.overwriteSpentLimit(terminationProperties.getSpentLimit());
            }
            if (terminationProperties.getUnimprovedSpentLimit() != null) {
                terminationConfig.overwriteUnimprovedSpentLimit(terminationProperties.getUnimprovedSpentLimit());
            }
            if (terminationProperties.getBestScoreLimit() != null) {
                terminationConfig.setBestScoreLimit(terminationProperties.getBestScoreLimit());
            }
        }
    }

    // @Bean wrapped by static class to avoid classloading issues if dependencies are absent
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({ Jackson2ObjectMapperBuilder.class, Score.class })
    static class OptaPlannerJacksonConfiguration {

        @Bean
        Module jacksonModule() {
            return OptaPlannerJacksonModule.createModule();
        }

    }

    private static class IncludeAbstractClassesEntityScanner extends EntityScanner {

        public IncludeAbstractClassesEntityScanner(ApplicationContext context) {
            super(context);
        }

        @Override
        protected ClassPathScanningCandidateComponentProvider
                createClassPathScanningCandidateComponentProvider(ApplicationContext context) {
            return new ClassPathScanningCandidateComponentProvider(false) {
                @Override
                protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                    AnnotationMetadata metadata = beanDefinition.getMetadata();
                    // Do not exclude abstract classes nor interfaces
                    return metadata.isIndependent();
                }
            };
        }

    }

}
