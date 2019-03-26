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

package org.drools.persistence;

import java.util.Set;
import javax.persistence.EntityManager;

import org.drools.persistence.api.TransactionManager;
import org.drools.persistence.api.TransactionManagerHelper;
import org.drools.persistence.api.TransactionSynchronization;
import org.drools.persistence.api.Transformable;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

public class TriggerUpdateTransactionSynchronization implements TransactionSynchronization {

    private TransactionManager txm;
    private Environment environment;

    public TriggerUpdateTransactionSynchronization(TransactionManager txm, Environment environment) {
        this.txm = txm;
        this.environment = environment;
    }

    @Override
    public void beforeCompletion() {
        Set<Transformable> toBeUpdated = TransactionManagerHelper.getUpdateableSet(txm);
        // does the work only if it's valid for jpa persistence
        if ( !isValid() || toBeUpdated == null || toBeUpdated.isEmpty()) {
            return;
        }


        EntityManager appScopedEM = ((EntityManager)environment.get(EnvironmentName.APP_SCOPED_ENTITY_MANAGER));
        if (appScopedEM == null) {
            appScopedEM = (EntityManager) txm.getResource(EnvironmentName.APP_SCOPED_ENTITY_MANAGER);
        }
        EntityManager cmdScopedEM = (EntityManager) txm.getResource(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        if (cmdScopedEM == null) {
            cmdScopedEM = ((EntityManager)environment.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER));
        }

        boolean flushApp = false;
        boolean flushCmd = false;

        for (Transformable transformable : toBeUpdated) {
            if (transformable != null) {
                transformable.transform();
                if (appScopedEM != null && appScopedEM.contains(transformable)) {

                    appScopedEM.merge(transformable);
                    TransactionManagerHelper.removeFromUpdatableSet(txm, transformable);
                    flushApp = true;
                } else if (cmdScopedEM != null &&cmdScopedEM.contains(transformable)) {

                    cmdScopedEM.merge(transformable);
                    TransactionManagerHelper.removeFromUpdatableSet(txm, transformable);
                    flushCmd = true;
                }
            }
        }
        if (flushApp) {
            appScopedEM.flush();
        }

        if (flushCmd) {
            cmdScopedEM.flush();
        }
    }

    @Override
    public void afterCompletion(int status) {

    }

    private boolean isValid() {
        Object appScopedEM = environment.get(EnvironmentName.APP_SCOPED_ENTITY_MANAGER);

        if (appScopedEM == null || appScopedEM instanceof EntityManager) {
            return true;
        }

        return false;
    }
}
