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

package org.drools.persistence.mapdb;

import java.util.Set;

import javax.transaction.Status;

import org.drools.persistence.TransactionManager;
import org.drools.persistence.TransactionManagerHelper;
import org.drools.persistence.TransactionSynchronization;
import org.drools.persistence.Transformable;
import org.kie.api.persistence.ObjectStoringStrategy;
import org.kie.api.runtime.Environment;
import org.mapdb.DB;

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

        DB db = (DB) environment.get(MapDBEnvironmentName.DB_OBJECT);
        ObjectStoringStrategy[] strategies = (ObjectStoringStrategy[]) environment.get(MapDBEnvironmentName.OBJECT_STORING_STRATEGIES);
        for (Transformable transformable : toBeUpdated) {
            if (transformable != null) {
            	MapDBTransformable trans = (MapDBTransformable) transformable;
            	trans.setEnvironment(environment);
                trans.transform();
                trans.updateOnMap(db, strategies);
            }
        }
    }

    private boolean isRolledBack(int st) {
    	return st == Status.STATUS_MARKED_ROLLBACK || st == Status.STATUS_ROLLEDBACK || st == Status.STATUS_ROLLING_BACK;
	}

	@Override
    public void afterCompletion(int status) {
        if ( !isValid() ) {
            return;
        }

        DB db = (DB) environment.get(MapDBEnvironmentName.DB_OBJECT);
        if (isRolledBack(txm.getStatus() /*status*/)) {
        	db.rollback();
        } else {
        	db.commit();
        }
    }

    private boolean isValid() {
        Object db = environment.get(MapDBEnvironmentName.DB_OBJECT);

        if (db == null || db instanceof DB) {
            return true;
        }

        return false;
    }
}
