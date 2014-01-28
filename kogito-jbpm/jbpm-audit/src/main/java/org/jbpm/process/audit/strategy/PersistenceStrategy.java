package org.jbpm.process.audit.strategy;

import javax.persistence.EntityManager;

public interface PersistenceStrategy {

   public EntityManager getEntityManager();
   
   public Object joinTransaction(EntityManager em);
  
   public void leaveTransaction(EntityManager em, Object transaction);
 
   public void dispose();
}
