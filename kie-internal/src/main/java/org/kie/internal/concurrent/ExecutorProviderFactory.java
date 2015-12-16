/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.concurrent;

import org.kie.api.concurrent.KieExecutors;
import org.kie.internal.utils.ServiceRegistryImpl;

public class ExecutorProviderFactory {

    private static class ExecutorProviderHolder {
        private static final KieExecutors executorProvider = ServiceRegistryImpl.getInstance().get( KieExecutors.class );
    }

    public static KieExecutors getExecutorProvider() {
        return ExecutorProviderHolder.executorProvider;
    }
}
