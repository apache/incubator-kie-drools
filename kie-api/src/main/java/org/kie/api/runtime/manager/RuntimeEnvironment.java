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
package org.kie.api.runtime.manager;

import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.task.UserGroupCallback;

/**
 * Definition of the runtime environment that will be used by instance of <code>RuntimeManager</code>.
 * <code>RuntimeEnvironment</code> delivers all information to the runtime manager to be able to properly
 * configure and bootstrap the manager and then runtime engine instances.<br>
 * It shall be considered as template of the configuration used by the manager that is read only and shall
 * not be changed once <code>RuntimeManager</code> has been created.
 */
public interface RuntimeEnvironment {

    /**
     * @return <code>KieBase</code> that shall be used by the manager
     */
    KieBase getKieBase();

    /**
     * @return environment that shall be used to create instances of <code>KieSession</code>
     */
    Environment getEnvironment();

    /**
     * @return KieSession configuration that shall be used to create instances of <code>KieSession</code>
     */
    KieSessionConfiguration getConfiguration();

    /**
     * @return indicates if persistence shall be used for the KieSession instances
     */
    boolean usePersistence();

    /**
     * @return concrete implementation of <code>RegisterableItemsFactory</code> to obtain handlers and listeners
     * that shall be registered on instances of <code>KieSession</code>
     */
    RegisterableItemsFactory getRegisterableItemsFactory();

    /**
     * @return concrete implementation of <code>UserGroupCallback</code> that shall be registered on instances
     * of <code>TaskService</code> for managing users and groups.
     */
    UserGroupCallback getUserGroupCallback();

    /**
     * @return custom class loader that shall be used by the process engine and task service instances
     */
    ClassLoader getClassLoader();

    /**
     * Closes the environment allowing to close all depending components such as ksession factories, etc
     */
    void close();
}
