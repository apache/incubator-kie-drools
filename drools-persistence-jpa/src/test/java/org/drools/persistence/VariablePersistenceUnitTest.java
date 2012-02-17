package org.drools.persistence;

import static org.drools.persistence.util.PersistenceUtil.*;

import org.drools.persistence.util.PersistenceUtil;
import org.drools.persistence.util.RerunWithLocalTransactions;
import org.junit.Rule;

public abstract class VariablePersistenceUnitTest {

    @Rule
    public RerunWithLocalTransactions rerunWithLocalTx = new RerunWithLocalTransactions();
    
    private String PERSISTENCE_UNIT = PersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
    
    public void setPersistenceUnitToDroolsLocal() { 
        PERSISTENCE_UNIT = DROOLS_LOCAL_PERSISTENCE_UNIT_NAME;
    }
   
    public void setPersistenceUnitToDroolsJTA() { 
        PERSISTENCE_UNIT = DROOLS_PERSISTENCE_UNIT_NAME;
    }
    
    public void setPersistenceUnitToJbpmLocal() { 
        PERSISTENCE_UNIT = JBPM_LOCAL_PERSISTENCE_UNIT_NAME;
    }
   
    public void setPersistenceUnitToJbpmJTA() { 
        PERSISTENCE_UNIT = JBPM_PERSISTENCE_UNIT_NAME;
    }
    
    public String getPersistenceUnitName() { 
        return PERSISTENCE_UNIT;
    }
    
    public boolean usingJTATransactions() { 
        return (JBPM_PERSISTENCE_UNIT_NAME.equals(getPersistenceUnitName()) || 
                DROOLS_PERSISTENCE_UNIT_NAME.equals(getPersistenceUnitName()));
    }
    
    public boolean usingLocalTransactions() { 
        return (DROOLS_LOCAL_PERSISTENCE_UNIT_NAME.equals(getPersistenceUnitName()) || 
                JBPM_LOCAL_PERSISTENCE_UNIT_NAME.equals(getPersistenceUnitName()));
    }
}
