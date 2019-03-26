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

import javax.ejb.Stateful;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.kie.api.runtime.KieSession;

/**
 * Stateful session bean configured with bean managed transactions.
 */
@Stateful
@TransactionManagement(TransactionManagementType.BEAN)
public class StatefulBMT extends BeanWithBMT {
    
    private KieSession ksession;

    public String info() {
        return "Stateful bean with bean managed transactions";
    }
    
    public void startProcess(ProcessScenario scenario) {
        ksession = createNewSession(scenario.getKbase());

        begin();
        scenario.runProcess(ksession);
        commit();
        
    }
    
    public void dispose() {
        if (ksession != null) {
            begin();
            ksession.dispose();
            commit();
            ksession = null;
        }
    }
}
