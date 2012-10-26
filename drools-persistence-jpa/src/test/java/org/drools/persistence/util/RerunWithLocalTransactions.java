package org.drools.persistence.util;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RerunWithLocalTransactions implements MethodRule {

    private static Logger logger = LoggerFactory.getLogger(RerunWithLocalTransactions.class);

    private Boolean useDrools;
    
    public Statement apply(final Statement base, FrameworkMethod method, final Object target) {
        return new Statement() {
            
            @Override
            public void evaluate() throws Throwable {
                
                // Run once with JTA
                base.evaluate();
                
                if( target instanceof VariablePersistence ) { 
                    VariablePersistence test = (VariablePersistence) target;
                    
                    if( useDrools == null && target.getClass().getPackage().getName().startsWith("org.jbpm") ) { 
                        test.setPersistenceUnitToJbpmLocal();
                        useDrools = false;
                    }
                    else { 
                        useDrools = true;
                        test.setPersistenceUnitToDroolsLocal();
                    }
                
                    // Run once with local tx's
                    try { 
                        logger.info("[Rerunning with local transactions]");
                        base.evaluate();
                    }
                    catch(Throwable t ) { 
                       throw new Throwable("Exception with local transactions: " + t.getMessage(), t);     
                    }
                    finally {
                        // Reset for next method
                        if( useDrools ) { 
                            test.setPersistenceUnitToDroolsJTA();
                        }
                        else { 
                            test.setPersistenceUnitToJbpmJTA();
                        }
                    }
                }
            }
        };
    }
    

}
