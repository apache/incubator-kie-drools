/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.modelcompiler.ExecutableModelFlowProject;
import org.drools.modelcompiler.ExecutableModelProject;

/**
 * Utility class that holds default tests parameters.
 */
public final class TestParametersUtil {

    private static final boolean TEST_WITH_ALPHA_NETWORK = Boolean.valueOf(System.getProperty("alphanetworkCompilerEnabled"));

    /**
     * Get all KieBaseConfigurations for tests based on specified engine configurations.
     * @param engineConfigurations Engine configurations for tests. They should contain every configuration that
     * should be tested. What is not specified, is not returned as part of KieBaseConfigurations.
     * @return
     */
    public static Collection<Object[]> getKieBaseConfigurations(final EngineTestConfiguration... engineConfigurations) {
        if (engineConfigurations == null || engineConfigurations.length == 0) {
            throw new IllegalArgumentException("No parameters specified! Please provide some required parameters");
        }
        final Collection<Object[]> parameters = new ArrayList<>();
        for (final KieBaseTestConfiguration testConfiguration : KieBaseTestConfiguration.values()) {
            if (isTestConfigurationValid(testConfiguration, engineConfigurations)) {
                parameters.add(new Object[]{testConfiguration});
            }
        }
        return parameters;
    }

    /**
     * Checks, if a test configuration matches specified engine configurations. Take note that the engine configurations
     * can contain contradictory configurations on first sight. This is because they should include all
     * configurations requested for tests. E.g. a test could require both combinations with executable model on and also off.
     *
     * @param testConfiguration A configuration that is checked if it matches the specified engine configurations
     * @param engineConfigurations Specified engine configurations for tests
     * @return true, if the test configuration is valid for specified engine configurations, otherwise false
     */
    private static boolean isTestConfigurationValid(final KieBaseTestConfiguration testConfiguration,
                                                    final EngineTestConfiguration[] engineConfigurations) {
        final Set<EngineTestConfiguration> engineTestConfigurationSet = new HashSet<>(Arrays.asList(engineConfigurations));

        if (testConfiguration.isStreamMode() && !engineTestConfigurationSet.contains(EngineTestConfiguration.STREAM_MODE)) {
            return false;
        }

        if (!testConfiguration.isStreamMode() && !engineTestConfigurationSet.contains(EngineTestConfiguration.CLOUD_MODE)) {
            return false;
        }

        if (testConfiguration.isIdentity() && !engineTestConfigurationSet.contains(EngineTestConfiguration.IDENTITY_MODE)) {
            return false;
        }

        if (!testConfiguration.isIdentity() && !engineTestConfigurationSet.contains(EngineTestConfiguration.EQUALITY_MODE)) {
            return false;
        }

        if (testConfiguration.useAlphaNetworkCompiler()
                && !engineTestConfigurationSet.contains(EngineTestConfiguration.ALPHA_NETWORK_COMPILER_TRUE)) {
            return false;
        }

        if (!testConfiguration.useAlphaNetworkCompiler()
                && !engineTestConfigurationSet.contains(EngineTestConfiguration.ALPHA_NETWORK_COMPILER_FALSE)) {
            return false;
        }

        if (!testConfiguration.getExecutableModelProjectClass().isPresent()
                && !engineTestConfigurationSet.contains(EngineTestConfiguration.EXECUTABLE_MODEL_OFF)) {
            return false;
        }

        if (testConfiguration.getExecutableModelProjectClass().isPresent()) {
            if (testConfiguration.getExecutableModelProjectClass().get().equals(ExecutableModelFlowProject.class)
                    && !engineTestConfigurationSet.contains(EngineTestConfiguration.EXECUTABLE_MODEL_FLOW)) {
                return false;
            }

            if (testConfiguration.getExecutableModelProjectClass().get().equals(ExecutableModelProject.class)
                    && !engineTestConfigurationSet.contains(EngineTestConfiguration.EXECUTABLE_MODEL_PATTERN)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Prepares collection of KieBaseTestConfiguration.
     * @return Collection of KieBaseTestConfiguration for parameterized tests.
     */
    public static Collection<Object[]> getKieBaseConfigurations() {
        final List<EngineTestConfiguration> engineTestConfigurations = Arrays.stream(EngineTestConfiguration.values())
                .filter(config -> (TEST_WITH_ALPHA_NETWORK || config != EngineTestConfiguration.ALPHA_NETWORK_COMPILER_TRUE)
                        && config != EngineTestConfiguration.EQUALITY_MODE)
                .collect(Collectors.toList());
        return getKieBaseConfigurations(engineTestConfigurations.toArray(new EngineTestConfiguration[]{}));
    }

    /**
     * Prepares collection of stream KieBaseTestConfiguration.
     * @return Collection of KieBaseTestConfiguration for parameterized tests.
     * @param testAlsoExecutableModel If true, the configurations returned contain configurations with executable model.
     */
    public static Collection<Object[]> getKieBaseStreamConfigurations(final boolean testAlsoExecutableModel) {
        return getKieBaseStreamOrCloudConfigurations(EngineTestConfiguration.STREAM_MODE, testAlsoExecutableModel);
    }

    /**
     * Prepares collection of stream KieBaseTestConfiguration.
     * @return Collection of KieBaseTestConfiguration for parameterized tests.
     * @param testAlsoExecutableModel If true, the configurations returned contain configurations with executable model.
     */
    public static Collection<Object[]> getKieBaseCloudConfigurations(final boolean testAlsoExecutableModel) {
        return getKieBaseStreamOrCloudConfigurations(EngineTestConfiguration.CLOUD_MODE, testAlsoExecutableModel);
    }

    private static Collection<Object[]> getKieBaseStreamOrCloudConfigurations(final EngineTestConfiguration streamOrCloudConfig,
                                                                              final boolean testAlsoExecutableModel) {
        final List<EngineTestConfiguration> engineTestConfigurations = new ArrayList<>();
        engineTestConfigurations.add(streamOrCloudConfig);
        // Testing just IDENTITY_MODE by default, leaving EQUALITY_MODE to specialized tests.
        engineTestConfigurations.add(EngineTestConfiguration.IDENTITY_MODE);
        engineTestConfigurations.add(EngineTestConfiguration.EXECUTABLE_MODEL_OFF);
        engineTestConfigurations.add(EngineTestConfiguration.ALPHA_NETWORK_COMPILER_FALSE);

        if (TEST_WITH_ALPHA_NETWORK) {
            engineTestConfigurations.add(EngineTestConfiguration.ALPHA_NETWORK_COMPILER_TRUE);
        }

        if (testAlsoExecutableModel) {
            engineTestConfigurations.add(EngineTestConfiguration.EXECUTABLE_MODEL_FLOW);
            engineTestConfigurations.add(EngineTestConfiguration.EXECUTABLE_MODEL_PATTERN);
        }

        return getKieBaseConfigurations(engineTestConfigurations.toArray(new EngineTestConfiguration[]{}));
    }

    /**
     * Prepares various combinations of KieBaseTestConfiguration and KieSessionTestConfiguration.
     * @return Collection of combinations for parameterized tests.
     */
    public static Collection<Object[]> getKieBaseAndKieSessionConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        // These are wrapped in an array, because these are parameters for JUnit Parameterized runner.
        Collection<Object[]> kieBaseConfigurations = getKieBaseCloudConfigurations(true);
        for (final KieSessionTestConfiguration kieSessionTestConfiguration : KieSessionTestConfiguration.values()) {
            for (final Object[] kieBaseConfigWrapped : kieBaseConfigurations) {
                parameters.add(new Object[]{kieBaseConfigWrapped[0], kieSessionTestConfiguration});
            }
        }

        kieBaseConfigurations = getKieBaseStreamConfigurations(true);
        for (final Object[] kieBaseConfigWrapped : kieBaseConfigurations) {
            parameters.add(new Object[]{kieBaseConfigWrapped[0], KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{kieBaseConfigWrapped[0], KieSessionTestConfiguration.STATEFUL_PSEUDO});
        }

        return parameters;
    }

    /**
     * Prepares various combinations of KieBaseTestConfiguration and KieSessionTestConfiguration.
     * Use only stateful kie sessions.
     * @return Collection of combinations for parameterized tests.
     */
    public static Collection<Object[]> getKieBaseAndStatefulKieSessionConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        final Collection<Object[]> kieBaseConfigurations = getKieBaseConfigurations();
        for (final Object[] kieBaseConfigWrapped : kieBaseConfigurations) {
            parameters.add(new Object[]{kieBaseConfigWrapped[0], KieSessionTestConfiguration.STATEFUL_REALTIME});
            if (((KieBaseTestConfiguration) kieBaseConfigWrapped[0]).isStreamMode()) {
                parameters.add(new Object[]{kieBaseConfigWrapped[0], KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }
        }

        return parameters;
    }

    /**
     * Prepares various combinations of KieBaseTestConfiguration and KieSessionTestConfiguration.
     * Use only stream kie bases and stateful kie sessions.
     * @return Collection of combinations for parameterized tests.
     */
    public static Collection<Object[]> getStreamKieBaseAndStatefulKieSessionConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        final Collection<Object[]> kieBaseConfigurations = getKieBaseStreamConfigurations(true);
        for (final Object[] kieBaseConfigWrapped : kieBaseConfigurations) {
            parameters.add(new Object[]{kieBaseConfigWrapped[0], KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{kieBaseConfigWrapped[0], KieSessionTestConfiguration.STATEFUL_PSEUDO});
        }

        return parameters;
    }

    private TestParametersUtil() {
        // Creating instances of util classes should not be possible.
    }
}
