/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class SpringStandaloneJtaSharedEntityManagerStrategy extends StandaloneJtaStrategy {

    private final EntityManager em;
    private boolean manageTx;
    
    public SpringStandaloneJtaSharedEntityManagerStrategy(EntityManagerFactory emf) {
        super(null);
        this.em = emf.createEntityManager();
        this.manageTx = true;
    }
    
    public SpringStandaloneJtaSharedEntityManagerStrategy(EntityManager em) {
        super(null);
        this.em = em;
        this.manageTx = false;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    @Override
    public void leaveTransaction(EntityManager em, Object transaction) {
        // do not close or clear the entity manager
        if (manageTx) {
        	commitTransaction(transaction);
        }
    }

	@Override
	public Object joinTransaction(EntityManager em) {
		if (manageTx) {
			return super.joinTransaction(em);
		}
		
		return manageTx;
	}

}
