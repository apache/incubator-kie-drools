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

package org.drools.persistence.marshalling.util;

import static org.drools.persistence.marshalling.util.EntityManagerFactoryProxy.getAllInterfaces;
import static org.drools.persistence.marshalling.util.EntityManagerFactoryProxy.updateManagedObjects;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.BitronixTransaction;
import bitronix.tm.BitronixTransactionManager;

public class UserTransactionProxy implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(UserTransactionProxy.class);
    
    private UserTransaction ut;
    private EntityManagerFactory emf;
    
    /**
     * This method creates a proxy for either a {@link EntityManagerFactory} or a {@link EntityManager} instance. 
     * @param obj The original instance for which a proxy will be made.
     * @return Object a proxy instance of the given object.
     */
    public static Object newInstance( EntityManagerFactory emf) {
        UserTransaction ut = findUserTransaction();
        return Proxy.newProxyInstance(
                ut.getClass().getClassLoader(), 
                getAllInterfaces(ut),
                new UserTransactionProxy(ut, emf));
    }
    
    public static final String DEFAULT_USER_TRANSACTION_NAME = "java:comp/UserTransaction";
    
    private static UserTransaction findUserTransaction() {
        try {
            InitialContext context = new InitialContext();
            return (UserTransaction) context.lookup( DEFAULT_USER_TRANSACTION_NAME );
        } catch ( NamingException ex ) {
            logger.debug( "No UserTransaction found at JNDI location [{}]",
                          DEFAULT_USER_TRANSACTION_NAME,
                          ex );
            return null;
        }
    }
    
    /**
     * This is the constructor that follows the InvocationHandler design pattern, so to speak. <br/>
     * It saves the @{link {@link EntityManager} or {@link EntityManagerFactory} for use later. 
     * @param obj The object being proxied.
     */
    private UserTransactionProxy(UserTransaction ut, EntityManagerFactory emf ) { 
        this.ut = ut;
        this.emf = emf;
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String methodName = method.getName();

        logger.trace(methodName);
        if( "commit".equals(methodName) && args == null) { 
            BitronixTransaction bt = ((BitronixTransactionManager) ut).getCurrentTransaction();
            // Ensure that all actions have occurred so that we get what is _really_ commited to the db
            // (This code is straight from bitronix)
            Iterator<?> iter = bt.getSynchronizationScheduler().reverseIterator();
            while (iter.hasNext()) {
                Synchronization synchronization = (Synchronization) iter.next();
                try {
                    synchronization.beforeCompletion();
                } catch (RuntimeException ex) {
                    bt.setStatus(Status.STATUS_MARKED_ROLLBACK);
                    throw ex;
                }
            }
            
            String testMethodName = MarshallingTestUtil.getTestMethodName();
            if( testMethodName != null ) { 
                EntityManager em = emf.createEntityManager();
                updateManagedObjects(testMethodName, em);
                em.close();
            }
            ut.commit();
            return result;
        }
        else { 
            result = invoke(method, ut, args);
        }
        
        return result;
    }
    
    private Object invoke( Method method, Object object, Object[] args) throws Throwable { 
        Object result = null;
        try { 
            result = method.invoke(object, args);
        } catch( InvocationTargetException ite ) { 
           logger.warn(method.getName() + " threw " + ite.getClass().getSimpleName() + ": " + ite.getMessage());
           throw ite;
        }
        return result;
    }
}
