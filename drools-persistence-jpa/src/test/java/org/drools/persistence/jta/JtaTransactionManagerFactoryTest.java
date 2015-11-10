package org.drools.persistence.jta;

import static org.junit.Assert.assertEquals;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.drools.core.impl.EnvironmentFactory;
import org.junit.Test;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

import bitronix.tm.BitronixTransactionManager;

public class JtaTransactionManagerFactoryTest {

    @Test
    public void usesEnvironmentToCreateTransactionManager() throws Exception {
        Environment env = EnvironmentFactory.newEnvironment();
        
        env.set(EnvironmentName.TRANSACTION, DUMMY_UT);
        JtaTransactionManager txm = (JtaTransactionManager) new JtaTransactionManagerFactory().newTransactionManager(env);
        assertEquals(DUMMY_UT, txm.ut);
    }
    
    @Test
    public void createsWithoutEnvironment() throws Exception {
        JtaTransactionManager txm = (JtaTransactionManager) new JtaTransactionManagerFactory().newTransactionManager();
        // Should be BitronixTransactionManager since Bitronix InitialContext is bootstrapped from src/test/resources/jndi.properties
        assertEquals(BitronixTransactionManager.class, txm.ut.getClass());
    }
    
    private static final UserTransaction DUMMY_UT = new UserTransaction() {
        
        @Override
        public void setTransactionTimeout(int arg0) throws SystemException {}
        
        @Override
        public void setRollbackOnly() throws IllegalStateException, SystemException {}
        
        @Override
        public void rollback() throws IllegalStateException, SecurityException, SystemException {}
        
        @Override
        public int getStatus() throws SystemException {return 0;}
        
        @Override
        public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {}
        
        @Override
        public void begin() throws NotSupportedException, SystemException {}
        
        @Override
        public boolean equals(Object obj) { return this==obj; };
    };
}
