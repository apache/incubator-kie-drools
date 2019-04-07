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

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.kie.api.runtime.KieSession;

/**
 * Stateless session bean configured with container managed transactions.
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class StatelessCMT extends BeanWithCMT {

    public String info() {
        return "Stateless bean with container managed transactions";
    }
    
    public void startProcess(ProcessScenario scenario) {
        KieSession ksession = createNewSession(scenario.getKbase());
        scenario.runProcess(ksession);
    }
    
    @Override
    public void dispose() {
        
    }
}
