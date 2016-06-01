/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

    public static Collection<Object[]> getKieBaseConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_IDENTITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.CLOUD_EQUALITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY});
        return parameters;
    }

    public static Collection<Object[]> getKieBaseStreamConfigurations() {
        final Collection<Object[]> parameters = new ArrayList<>();
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_IDENTITY});
        parameters.add(new Object[]{KieBaseTestConfiguration.STREAM_EQUALITY});
        return parameters;
    }

    private TestParametersUtil() {
        // Creating instances of util classes should not be possible.
    }
}
