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
import java.util.Collection;

/**
 * Utility class that holds default tests parameters.
 */
public final class TestParametersUtil {

    public static boolean TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL = true;
    public static boolean TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL = true;
    public static boolean TEST_WITH_ALPHA_NETWORK = Boolean.valueOf(System.getProperty("alphanetworkCompilerEnabled"));

    /**
     * Prepares collection of KieBaseTestConfiguration.
     * @return Collection of KieBaseTestConfiguration for parameterized tests.
     */
    public static Collection<Object[]> getKieBaseConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY});

        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_ALPHA_NETWORK});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_ALPHA_NETWORK});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK});
        }

        if ( TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW});

            if(TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK});

            }
        }

        if( TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK});
            }
        }

        return parameters;
    }

    /**
     * Prepares collection of stream KieBaseTestConfiguration.
     * @return Collection of KieBaseTestConfiguration for parameterized tests.
     * @param testAlsoExecutableModel If true, the configurations returned contain configurations with executable model.
     */
    public static Collection<Object[]> getKieBaseStreamConfigurations(final boolean testAlsoExecutableModel) {
        final Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY});

        if(TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK});
        }

        if ( testAlsoExecutableModel ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK});
            }
        }

        if( testAlsoExecutableModel ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK});
            }

        }
        return parameters;
    }

    /**
     * Prepares collection of stream KieBaseTestConfiguration.
     * @return Collection of KieBaseTestConfiguration for parameterized tests.
     * @param testAlsoExecutableModel If true, the configurations returned contain configurations with executable model.
     */
    public static Collection<Object[]> getKieBaseCloudConfigurations(final boolean testAlsoExecutableModel) {
        final Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY});

        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_ALPHA_NETWORK});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_ALPHA_NETWORK});
        }


        if ( testAlsoExecutableModel ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW_ALPHA_NETWORK});
            }

        }

        if( testAlsoExecutableModel ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK});
            }
        }

        return parameters;
    }

    /**
     * Prepares collection of KieSessionTestConfiguration.
     * @return Collection of KieSessionTestConfiguration for parameterized tests.
     */
    public static Collection<Object[]> getKieSessionConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieSessionTestConfiguration.STATEFUL_PSEUDO});
        parameters.add(new Object[]{KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieSessionTestConfiguration.STATELESS_REALTIME});
        return parameters;
    }

    /**
     * Prepares various combinations of KieBaseTestConfiguration and KieSessionTestConfiguration.
     * @return Collection of combinations for parameterized tests.
     */
    public static Collection<Object[]> getKieBaseAndKieSessionConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY, KieSessionTestConfiguration.STATELESS_REALTIME});

        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATELESS_REALTIME});
        }

        if ( TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATELESS_REALTIME});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATELESS_REALTIME});
            }

        }

        if( TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATELESS_REALTIME});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATELESS_REALTIME});
            }
        }

        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY, KieSessionTestConfiguration.STATELESS_REALTIME});

        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATELESS_REALTIME});
        }

        if ( TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATELESS_REALTIME});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATELESS_REALTIME});
            }

        }

        if ( TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATELESS_REALTIME});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATELESS_REALTIME});
            }

        }

        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});


        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
        }

        if ( TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }

        }

        if ( TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }
        }

        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});

        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
        }

        if ( TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }

        }
        if ( TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});


            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }
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
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});


        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
        }

        if ( TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }

        }

        if ( TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
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
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY, KieSessionTestConfiguration.STATEFUL_REALTIME});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY, KieSessionTestConfiguration.STATEFUL_PSEUDO});

        if (TEST_WITH_ALPHA_NETWORK) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});

        }

        if ( TEST_EXECUTABLE_MODEL_WITH_FLOW_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW, KieSessionTestConfiguration.STATEFUL_PSEUDO});

            if (TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_FLOW_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }


        }

        if( TEST_EXECUTABLE_MODEL_WITH_PATTERN_DSL ) {
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_REALTIME});
            parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN, KieSessionTestConfiguration.STATEFUL_PSEUDO});


            if(TEST_WITH_ALPHA_NETWORK) {
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_REALTIME});
                parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY_MODEL_PATTERN_ALPHA_NETWORK, KieSessionTestConfiguration.STATEFUL_PSEUDO});
            }
        }
        return parameters;
    }

    private TestParametersUtil() {
        // Creating instances of util classes should not be possible.
    }
}
