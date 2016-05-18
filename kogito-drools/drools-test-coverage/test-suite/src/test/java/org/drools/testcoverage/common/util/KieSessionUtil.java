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

import java.util.Properties;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;

/**
 * Util class that provides various methods related to KieSession.
 */
public final class KieSessionUtil {

    public static final KieSessionConfiguration getKieSessionConfigurationWithClock(final ClockTypeOption clockType,
            final Properties sessionProperties) {
        final KieSessionConfiguration conf = KieServices.Factory.get().newKieSessionConfiguration(sessionProperties);
        conf.setOption(clockType);
        return conf;
    }

    private KieSessionUtil() {
        // Creating instances of util classes should not be possible.
    }
}
