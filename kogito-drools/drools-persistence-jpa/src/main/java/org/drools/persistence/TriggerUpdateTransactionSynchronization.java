package org.drools.persistence;

import java.util.Set;
import javax.persistence.EntityManager;

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
        // does the work only if it's valid for jpa persistence
        if ( !isValid() ) {
            return;
        }

        Set<Transformable> toBeUpdated = TransactionManagerHelper.getUpdateableSet(txm);
        EntityManager appScopedEM = ((EntityManager)environment.get(EnvironmentName.APP_SCOPED_ENTITY_MANAGER));
        EntityManager cmdScopedEM = (EntityManager) txm.getResource(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);

        boolean flushApp = false;
        boolean flushCmd = false;

        for (Transformable transformable : toBeUpdated) {
            if (transformable != null) {
                transformable.transform();
                if (appScopedEM != null && appScopedEM.contains(transformable)) {
                    appScopedEM.merge(transformable);
                    flushApp = true;
                } else if (cmdScopedEM != null &&cmdScopedEM.contains(transformable)) {
                    cmdScopedEM.merge(transformable);
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

        if (appScopedEM != null && appScopedEM instanceof EntityManager) {
            return true;
        }

        return false;
    }
}
