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

import javax.annotation.Resource;
import javax.transaction.UserTransaction;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;

/**
 * Common base for bean configured with bean managed transactions. - injects
 * user transaction and uses it to run the process scenario
 *
 * Transaction management must be configured on the ending bean; this
 * configuration is not inherited!
 */
public abstract class BeanWithBMT extends BeanWithPersistence {

    @Resource
    protected UserTransaction ut;

    protected KieSession createNewSession(KieBase kbase) {
        dispose();
        return KieServices.Factory.get().getStoreServices().newKieSession(kbase, null, getEnvironment());
    }

    protected void begin() {
        begin(ut);
    }

    protected void commit() {
        commit(ut);
    }

    protected void rollback() {
        rollback(ut);
    }

    protected void begin(UserTransaction ut) {
        try {
            ut.begin();
        } catch (Exception ex) {
            throw new RuntimeException("error during transaction begin in test", ex);
        }
    }

    protected void commit(UserTransaction ut) {
        try {
            ut.commit();
        } catch (Exception ex) {
            throw new RuntimeException("error during transaction commit in test", ex);
        }
    }

    protected void rollback(UserTransaction ut) {
        try {
            ut.rollback();
        } catch (Exception ex) {
            throw new RuntimeException("error during transaction rollback in test", ex);
        }
    }

}
