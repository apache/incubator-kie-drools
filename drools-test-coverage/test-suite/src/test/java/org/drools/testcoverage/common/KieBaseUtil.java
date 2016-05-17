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

package org.drools.testcoverage.common;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;

/**
 * Util class that provides KieBase from various sources.
 */
public final class KieBaseUtil {

    public static final KieBase getDefaultKieBaseFromKieModule(final KieModule kmodule) {
        return getDefaultKieBaseFromReleaseId(kmodule.getReleaseId());
    }

    public static final KieBase getDefaultKieBaseFromReleaseId(final ReleaseId id) {
        return getKieBaseFromReleaseIdByName(id, null);
    }

    public static final KieBase getKieBaseFromKieBuilderByName(final KieBuilder kbuilder, final String name) {
        return getKieBaseFromKieModuleByName(kbuilder.getKieModule(), name);
    }

    public static final KieBase getKieBaseFromKieModuleByName(final KieModule kmodule, final String name) {
        return getKieBaseFromReleaseIdByName(kmodule.getReleaseId(), name);
    }

    public static final KieBase getKieBaseFromReleaseIdByName(final ReleaseId id, final String name) {
        final KieContainer container = getKieServices().newKieContainer(id);
        if (name == null) {
            return container.getKieBase();
        } else {
            return container.getKieBase(name);
        }
    }

    public static final KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    private KieBaseUtil() {
        // Creating instances of util classes should not be possible.
    }
}
