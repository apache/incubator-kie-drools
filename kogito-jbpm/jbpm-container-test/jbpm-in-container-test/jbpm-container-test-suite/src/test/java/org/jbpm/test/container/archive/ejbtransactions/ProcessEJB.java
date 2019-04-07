/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.archive.ejbtransactions;

/**
 * Interface for all EJBs to enable JBPM process execution.
 */
public interface ProcessEJB {

    /**
     * just information about the EJB implementing this interface
     */
    String info();

    /**
     * starts the process scenario in EJB transaction context according to the
     * bean configuration
     */
    void startProcess(ProcessScenario scenario);

    /**
     * disposal of knowledge session and other resources
     */
    void dispose();
}
