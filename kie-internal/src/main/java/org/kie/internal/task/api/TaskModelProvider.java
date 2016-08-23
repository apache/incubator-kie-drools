/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.internal.task.api;

import org.kie.internal.utils.ServiceRegistryImpl;

public class TaskModelProvider {

    private static final String PROVIDER_CLASS = "org.jbpm.services.task.persistence.TaskModelProviderImpl";

    private static TaskModelProviderService provider;

    public static TaskModelFactory getFactory() {
        return getTaskModelProviderService().getTaskModelFactory();
    }

    public static synchronized void setTaskModelProviderService(TaskModelProviderService provider) {
        TaskModelProvider.provider = provider;
    }

    public static synchronized TaskModelProviderService getTaskModelProviderService() {
        if (provider == null) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        ServiceRegistryImpl.getInstance().addDefault( TaskModelProviderService.class, PROVIDER_CLASS );
        setTaskModelProviderService(ServiceRegistryImpl.getInstance().get( TaskModelProviderService.class ) );
    }


    public static synchronized void loadProvider(ClassLoader cl) {
        if (provider == null) {
            try {
                provider = (TaskModelProviderService) Class.forName(PROVIDER_CLASS, true, cl).newInstance();
            } catch (Exception e) { }
        }
    }

}
