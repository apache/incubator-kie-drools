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

package org.kie.api.builder.model;

import java.util.List;

import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;

/**
 * KieSessionModel is a model allowing to programmatically define a KieSession
 * @see org.kie.api.runtime.KieSession
 */
public interface KieSessionModel {

    /**
     * Returns the name of the KieSession defined by this KieSessionModel
     */
    String getName();

    /**
     * Returns the type of this KieSessionModel
     */
    KieSessionType getType();

    /**
     * Sets the type for this KieSessionModel. Default is STATEFUL
     */
    KieSessionModel setType(KieSessionType type);

    /**
     * Returns the ClockType of this KieSessionModel
     */
    ClockTypeOption getClockType();

    /**
     * Sets the ClockType for this KieSessionModel. Default is realtime
     */
    KieSessionModel setClockType(ClockTypeOption clockType);

    /**
     * Returns the BeliefSystemType of this KieSessionModel
     */
    BeliefSystemTypeOption getBeliefSystem();

    /**
     * Sets the BeliefSystem for this KieSessionModel. Default is simple
     */
    KieSessionModel setBeliefSystem(BeliefSystemTypeOption beliefSystem);

    /**
     * Creates a new ListenerModel of the given type (i.e. the name of the class implementing it)
     * and kind and add it to this KieSessionModel
     */
    ListenerModel newListenerModel(String type, ListenerModel.Kind kind);

    /**
     * Returns all the ListenerModels defined for this KieSessionModel
     */
    List<ListenerModel> getListenerModels();

    /**
     * Creates a new WorkItemHandlerModel of the given type (i.e. the name of the class implementing it)
     * and add it to this KieSessionModel
     */
    WorkItemHandlerModel newWorkItemHandlerModel(String name, String type);

    /**
     * Returns all the WorkItemHandlerModels defined for this KieSessionModel
     */
    List<WorkItemHandlerModel> getWorkItemHandlerModels();

    /**
     * Sets the CDI scope for this KieSessionModel
     * Default is javax.enterprise.context.ApplicationScoped
     */
    KieSessionModel setScope(String scope);

    /**
     * Returns the CDI scope of this KieSessionModel
     */
    String getScope();

    /**
     * Returns the name of the ConsoleLogger if any
     */
    String getConsoleLogger();

    /**
     * Sets a ConsoleLogger with the given name
     */
    KieSessionModel setConsoleLogger(String consoleLogger);

    /**
     * Returns the FileLoggerModel registered on this KieSessionModel if any
     */
    FileLoggerModel getFileLogger();

    /**
     * Sets a non-threaded FileLogger with the given name and 30 seconds of logging interval
     */
    KieSessionModel setFileLogger(String fileName);

    /**
     * Sets a FileLogger with the given name, threaded behavior and logging interval
     */
    KieSessionModel setFileLogger(String fileName, int interval, boolean threaded);

    /**
     * Returns true if this KieSessionModel is the default one
     */
    boolean isDefault();

    /**
     * Sets the KieSession generated from this KieSessionModel as the default one,
     * i.e. the one that can be loaded from the KieContainer without having to pass its name.
     * Note that only one default KieSessionModel of type STATEFUL and one of type STATELESS
     * are allowed in a given KieContainer so if more than one is found
     * (maybe because a given KieContainer includes many KieModules) a warning is emitted
     * and all the defaults are disabled so all the KieSessions will be accessible only by name
     */
    KieSessionModel setDefault(boolean isDefault);

    /**
     * Returns the KieBaseModel which this KieSessionModel belongs to
     */
    KieBaseModel getKieBaseModel();

    enum KieSessionType {
        STATEFUL, STATELESS
    }
}
