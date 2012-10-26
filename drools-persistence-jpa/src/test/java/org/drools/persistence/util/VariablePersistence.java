package org.drools.persistence.util;


public interface VariablePersistence {
    
    static String PERSISTENCE_UNIT = PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
    
    public void setPersistenceUnitToDroolsLocal();
   
    public void setPersistenceUnitToDroolsJTA();
    
    public void setPersistenceUnitToJbpmLocal();
   
    public void setPersistenceUnitToJbpmJTA();
    
    public String getPersistenceUnitName();
    
}
