package org.drools.marshalling.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.persistence.info.SessionInfo;
import org.hibernate.ejb.EntityManagerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityManagerFactoryProxyFactory implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryProxyFactory.class);
    
    public static Object createProxy( Object obj ) {
        if( obj instanceof EntityManagerFactory  || obj instanceof EntityManager ) { 
            return Proxy.newProxyInstance(
                    obj.getClass().getClassLoader(), 
                    getAllInterfaces(obj),
                    new EntityManagerFactoryProxyFactory(obj));
        }
        else { 
            throw new UnsupportedOperationException("This proxy is only for " 
                    + EntityManagerFactory.class.getSimpleName() + " and " + EntityManager.class.getSimpleName() + " instances." );
        }
    }
  
    private static Class<?> [] getAllInterfaces( Object obj ) { 
        Class<?> [] interfaces = new Class [0];
        Class<?> superClass = obj.getClass();
        while( superClass != null ) { 
            Class<?> [] addThese = superClass.getInterfaces();
            if( addThese.length > 0 ) { 
                Class<?> [] moreinterfaces = new Class [interfaces.length + addThese.length];
                System.arraycopy(interfaces, 0, moreinterfaces, 0, interfaces.length);
                System.arraycopy(addThese, 0, moreinterfaces, interfaces.length, addThese.length);
                interfaces = moreinterfaces;
            }
            superClass = superClass.getSuperclass();
        }
        return interfaces;
    }
        
    private EntityManagerFactory emf;
    
    // OCRAM: processInstance.. and WorkItem.. 
    private EntityManager em;
    private HashMap<SessionInfo, byte []> marshalledDataMap;
    private HashMap<String, byte[]> testMethodByteArrayMap;
    
    private EntityManagerFactoryProxyFactory(Object obj ) { 
        if( obj instanceof EntityManagerFactory ) { 
            this.emf = (EntityManagerFactory) obj;
        }
        else if( obj instanceof EntityManager ) { 
            this.em = (EntityManager) obj;
            marshalledDataMap = new HashMap<SessionInfo, byte[]>();
            testMethodByteArrayMap = new HashMap<String, byte[]>();
        }
        else { 
            throw new UnsupportedOperationException("This proxy is only for " 
                    + EntityManagerFactory.class.getSimpleName() + " and " + EntityManager.class.getSimpleName() + " instances." );
        }
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String methodName = method.getName();
        
        if( "createEntityManager".equals(methodName) ) { 
            return createEntityManager(methodName, args);
        }
        else if( "persist".equals(methodName) && args.length == 1 ) {
            return persist(methodName, args);
        }
        else if( "merge".equals(methodName) && args.length == 1 ) { 
            return merge(methodName, args);
        }
        else if( "joinTransaction".equals(methodName) && args == null) { 
            return joinTransaction(); 
        }
        else { 
            String className = this.emf != null ? emf.getClass().getSimpleName() : em.getClass().getSimpleName();
            logger.trace( "><: " + className + "." + methodName );
            if( this.emf != null ) { 
                result = invoke(method, emf, args);
            }
            else if( this.em != null ) { 
                result = invoke(method, em, args);
            }
        }
        
        return result;
    }

    private Object createEntityManager(String methodName, Object [] args) { 
        EntityManager realEm = null;
        if( args == null ) { 
             realEm = (EntityManager) emf.createEntityManager();
        }
        else if( args[0] instanceof Map<?,?>){ 
            realEm = (EntityManager) emf.createEntityManager((Map<?,?>) args[0]);
        }
        else { 
            String message = "Method " + methodName + " with args (";
            for( int i = 0; i < args.length; ++i ) { 
                message += args[i].getClass() + ", ";
            }
            message = message.substring(0, message.lastIndexOf(",")) + ") not supported!";
            throw new UnsupportedOperationException(message);
        }
        return createProxy(realEm);
    }

    private Object persist(String methodName, Object[] args) {
        em.persist(args[0]);
        // OCRAM: processInstance.. and WorkItem.. 
        if( args[0] instanceof SessionInfo ) { 
            SessionInfo sessionInfo = (SessionInfo) args[0];
            marshalledDataMap.put(sessionInfo, sessionInfo.getData().clone());
            MarshalledData marshalledData = new MarshalledData(sessionInfo);
            em.persist(marshalledData);
            logger.info("-.-: " + marshalledData);
        }
        return null;
    }

    private Object merge(String methodName, Object[] args) {
        Object result = em.merge(args[0]);
        // OCRAM: processInstance.. and WorkItem.. 
        if( result instanceof SessionInfo ) { 
            // do stuff???
            // - if updatedEntity.rulesByteArray != entity.rulesByteArray
            // -> then.. ?
            //     ? save a new MarshalledData object
           MarshalledData marshalledData = new MarshalledData((SessionInfo) result);
           logger.info("-+-: " + marshalledData);
        }
        return result;
    }

    private Object joinTransaction() {
        em.joinTransaction();
        // OCRAM: processInstance.. and WorkItem.. 
        for( SessionInfo sessionInfo : marshalledDataMap.keySet()) { 
           byte [] origMarshalledBytes = marshalledDataMap.get(sessionInfo); 
           boolean newMarshalledData = ! Arrays.equals(origMarshalledBytes, sessionInfo.getData());

           byte [] lastMarshalledData = testMethodByteArrayMap.get(MarshallingTestUtil.getTestMethodName());
           if( lastMarshalledData != null && Arrays.equals(lastMarshalledData, sessionInfo.getData()) ) {
              newMarshalledData = false; 
           }
           if( newMarshalledData ) { 
               MarshalledData marshalledData = new MarshalledData(sessionInfo);
               em.persist(marshalledData);
               testMethodByteArrayMap.put(marshalledData.testMethodName, marshalledData.rulesByteArray);
               logger.info("-!-: " + marshalledData);
           }
        }
        return null;
    }

    private Object invoke( Method method, Object object, Object[] args) throws Throwable { 
        Object result = null;
        try { 
            result = method.invoke(object, args);
        } catch( InvocationTargetException ite ) { 
           
        }
        return result;
    }
}
