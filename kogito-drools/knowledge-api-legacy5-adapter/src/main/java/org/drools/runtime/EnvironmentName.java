/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.runtime;

public class EnvironmentName {
    public static final String TRANSACTION_MANAGER                  = org.kie.api.runtime.EnvironmentName.TRANSACTION_MANAGER;
    public static final String TRANSACTION_SYNCHRONIZATION_REGISTRY = org.kie.api.runtime.EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY;
    public static final String TRANSACTION                          = org.kie.api.runtime.EnvironmentName.TRANSACTION;

    public static final String ENTITY_MANAGER_FACTORY               = org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;
    public static final String CMD_SCOPED_ENTITY_MANAGER            = org.kie.api.runtime.EnvironmentName.CMD_SCOPED_ENTITY_MANAGER;
    public static final String APP_SCOPED_ENTITY_MANAGER            = org.kie.api.runtime.EnvironmentName.APP_SCOPED_ENTITY_MANAGER;
    public static final String PERSISTENCE_CONTEXT_MANAGER          = org.kie.api.runtime.EnvironmentName.PERSISTENCE_CONTEXT_MANAGER;

    public static final String OBJECT_MARSHALLING_STRATEGIES        = org.kie.api.runtime.EnvironmentName.OBJECT_MARSHALLING_STRATEGIES;
    public static final String GLOBALS                              = org.kie.api.runtime.EnvironmentName.GLOBALS;
    public static final String CALENDARS                            = org.kie.api.runtime.EnvironmentName.CALENDARS;
    public static final String DATE_FORMATS                         = org.kie.api.runtime.EnvironmentName.DATE_FORMATS;
}
