package org.drools.marshalling.util;

import static org.drools.marshalling.util.EntityManagerFactoryProxy.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

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

        logger.info(methodName);
        if( "commit".equals(methodName) && args == null) { 
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
        }
        return result;
    }
}
