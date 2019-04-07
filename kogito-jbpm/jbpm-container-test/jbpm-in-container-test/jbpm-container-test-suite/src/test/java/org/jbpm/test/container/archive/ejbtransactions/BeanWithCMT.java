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

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;

/**
 * Common base for EJBs configured with container managed transactions.
 */
public abstract class BeanWithCMT extends BeanWithPersistence {

    protected KieSession createNewSession(KieBase kbase) {
        dispose();
        return KieServices.Factory.get().getStoreServices().newKieSession(kbase, null, getEnvironment());
    }
    
}
