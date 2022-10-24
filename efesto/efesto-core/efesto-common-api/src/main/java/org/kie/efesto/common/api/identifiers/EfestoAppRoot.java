/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.efesto.common.api.identifiers;

import java.util.Map;

import static org.kie.efesto.common.api.utils.EfestoAppRootHelper.getEfestoComponentRootBySPI;

/**
 * Efesto-specific root path of an application.
 * <p>
 * Its top-level children are <code>EfestoComponentRoot</code> instances, the efesto-specific subclass of
 * <code>ComponentRoot</code>
 *
 * It also implements <code>ComponentRoot</code> so that it can be used as top-level <code>path</code> inside another <code>AppRoot</code>
 */
public final class EfestoAppRoot extends AppRoot implements ComponentRoot {

    public static final String EGESTO_ENGINES = "engines";

    private static final Map<Class<? extends EfestoComponentRoot>, EfestoComponentRoot> INSTANCES;

    static {
        INSTANCES = getEfestoComponentRootBySPI(EfestoComponentRoot.class);
    }

    public EfestoAppRoot() {
        super(EGESTO_ENGINES);
    }

    @Override
    public <T extends ComponentRoot> T get(Class<T> providerId) {
        return (T) INSTANCES.get(providerId);
    }

}
