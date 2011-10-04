/*
 * Copyright 2011 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.marshalling.util;

import static org.drools.marshalling.util.MarshallingTestUtil.*;
import static org.drools.marshalling.util.MarshallingTestUtil.PROCESS_INSTANCE_INFO_CLASS_NAME;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.acl.Owner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object 
 * 
 *
 */
public class EntityManagerFactoryProxyFactory implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryProxyFactory.class);
    
    private EntityManagerFactory emf;
    // OCRAM: processInstance.. and WorkItem.. 
    private EntityManager em;
    private HashMap<SessionInfo, byte []> managedSessionInfoDataMap;
    private HashMap<WorkItemInfo, byte []> managedWorkItemInfoDataMap;
    private HashMap<Object, byte []> managedProcessInstanceInfoDataMap;
    
    private HashMap<String, byte[]> lastSessionMarshalledDataForTestMethodMap;
    private HashMap<String, byte[]> lastWorkItemMarshalledDataForTestMethodMap;
    private HashMap<String, byte[]> lastProcessInstanceMarshalledDataForTestMethodMap;
        
    /**
     * This method creates a proxy for either a {@link EntityManagerFactory} or a {@link EntityManager} instance. 
     * @param obj The original instance for which a proxy will be made.
     * @return Object a proxy instance of the given object.
     */
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
  
    /**
     * This method is used in the {@link #createProxy(Object)} method to retrieve all applicable interfaces
     *   that the proxy object must conform to. 
     * @param obj The object that will be proxied. 
     * @return Class<?> [] an array of all applicable interfaces.
     */
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
       
    /**
     * This is the constructor that follows the InvocationHandler design pattern, so to speak. <br/>
     * It saves the @{link {@link EntityManager} or {@link EntityManagerFactory} for use later. 
     * @param obj The object being proxied.
     */
    private EntityManagerFactoryProxyFactory(Object obj ) { 
        if( obj instanceof EntityManagerFactory ) { 
            this.emf = (EntityManagerFactory) obj;
        }
        else if( obj instanceof EntityManager ) { 
            this.em = (EntityManager) obj;
        }
        else { 
            throw new UnsupportedOperationException("This proxy is only for " 
                    + EntityManagerFactory.class.getSimpleName() + " and " + EntityManager.class.getSimpleName() + " instances." );
        }
    }

    private void lazyInitializeStateMaps(Object [] args) { 
        if( args == null || args.length == 0 ) { 
            return;
        }
        
        if( args[0] instanceof SessionInfo ) { 
            managedSessionInfoDataMap = new HashMap<SessionInfo, byte[]>();
            lastSessionMarshalledDataForTestMethodMap = new HashMap<String, byte[]>();
        }
        else if( args[0] instanceof WorkItemInfo ) { 
            managedWorkItemInfoDataMap = new HashMap<WorkItemInfo, byte[]>();
            lastWorkItemMarshalledDataForTestMethodMap = new HashMap<String, byte[]>();
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(args[0].getClass().getName()) ) { 
            managedProcessInstanceInfoDataMap = new HashMap<Object, byte[]>();
            lastProcessInstanceMarshalledDataForTestMethodMap = new HashMap<String, byte[]>();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String methodName = method.getName();
      
        lazyInitializeStateMaps(args);
        if( "createEntityManager".equals(methodName) ) { 
            return createEntityManager(methodName, args);
        }
        else if( "persist".equals(methodName) && args.length == 1 ) {
            em.persist(args[0]);
            String testMethodName = MarshallingTestUtil.getTestMethodName();
            if( testMethodName != null ) { 
                persist(testMethodName, args);
            }
            return result;
        }
        else if( "merge".equals(methodName) && args.length == 1 ) { 
            result = em.merge(args[0]);
            String testMethodName = MarshallingTestUtil.getTestMethodName();
            if( testMethodName != null ) { 
                merge(testMethodName, result);
            }
            return result;
        }
        else if( "joinTransaction".equals(methodName) && args == null) { 
            String testMethodName = MarshallingTestUtil.getTestMethodName();
            em.joinTransaction();
            if( testMethodName != null ) { 
                joinTransaction(testMethodName); 
            }
            return result;
        }
        else { 
            String className = this.emf != null ? emf.getClass().getSimpleName() : em.getClass().getSimpleName();
            // logger.trace( "><: " + className + "." + methodName );
            if( this.emf != null ) { 
                result = invoke(method, emf, args);
            }
            else if( this.em != null ) { 
                result = invoke(method, em, args);
            }
        }
        
        return result;
    }

    private Object invoke( Method method, Object object, Object[] args) throws Throwable { 
        Object result = null;
        try { 
            result = method.invoke(object, args);
        } catch( InvocationTargetException ite ) { 
           
        }
        return result;
    }

    /**
     * This method creates a proxy for an EntityManager generated by the real EntityManagerFactory.
     * @param methodName The name of the test method in which this method is called. 
     * @param args The arguments to the EntityManagerFactory.createEntityManager(...) method
     * @return Object a proxy of a EntityManager instance.
     */
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

    /**
     * This method stores a MarshalledData object for all objects that contain marshalled data.
     * @param methodName The name of the test method in which this happens. 
     * @param args The arguments to EntityManager.persist(...)
     */
    private void persist(String methodName, Object[] args) {
        MarshalledData marshalledData = null;
        if( args[0] instanceof SessionInfo ) { 
            SessionInfo sessionInfo = (SessionInfo) args[0];
            byte [] byteArray = sessionInfo.getData();
            managedSessionInfoDataMap.put(sessionInfo, byteArray != null ? byteArray.clone() : null );
            if( byteArray != null ) { 
                marshalledData = new MarshalledData(sessionInfo);
                em.persist(marshalledData);
                logger.info("-.-: " + marshalledData);
            }
        }
        else if( args[0] instanceof WorkItemInfo ) { 
            WorkItemInfo workItemInfo = (WorkItemInfo) args[0];
            byte [] byteArray = workItemInfo.getWorkItemByteArray();
            managedWorkItemInfoDataMap.put(workItemInfo, byteArray != null ? byteArray.clone() : null );
            if( byteArray != null ) { 
                marshalledData = new MarshalledData(workItemInfo);
                em.persist(marshalledData);
                logger.info("-.-: " + marshalledData);
            }
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(args[0].getClass().getName()) ) { 
            byte [] byteArray = MarshallingTestUtil.getProcessInstanceInfoByteArray(args[0]);
            managedProcessInstanceInfoDataMap.put(args[0], byteArray != null ? byteArray.clone() : null);
            if( byteArray != null ) { 
                marshalledData = new MarshalledData(args[0]);
                em.persist(marshalledData);
                logger.info("-.-: " + marshalledData);
            }
        }
    }

    private void merge(String testMethodName, Object updatedObject) {
        if( updatedObject instanceof SessionInfo ) { 
            updateSessionInfoMarshalledData((SessionInfo) updatedObject, testMethodName);
        }
        if( updatedObject instanceof WorkItemInfo ) { 
            updateWorkItemInfoMarshalledData((WorkItemInfo) updatedObject, testMethodName);
        }
        if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(updatedObject.getClass().getName()) ) { 
            updateSessionInfoMarshalledData((SessionInfo) updatedObject, testMethodName);
        }
    }

    private void joinTransaction(String testMethodName) {
        // Note: this method could be logically more efficient -- which would, IMHO, also make _less_ readable.
    
        // Update the marshalled data belonging to managed SessionInfo objects
        if( managedSessionInfoDataMap != null ) { 
            for( SessionInfo sessionInfo : managedSessionInfoDataMap.keySet()) { 
                updateSessionInfoMarshalledData(sessionInfo, testMethodName);
            }
        }
        // Update the marshalled data belonging to managed WorkItemInfo objects
        if( managedWorkItemInfoDataMap != null ) { 
        for( WorkItemInfo workItemInfo : managedWorkItemInfoDataMap.keySet() ) { 
            updateWorkItemInfoMarshalledData(workItemInfo, testMethodName);
        }
    
        }
        // Update the marshalled data belonging to managed WorkItemInfo objects
        if( managedProcessInstanceInfoDataMap != null ) { 
            for( Object processInstanceInfoObject : managedProcessInstanceInfoDataMap.keySet() ) { 
                updateProcessInstanceInfoMarshalledData(processInstanceInfoObject, testMethodName);
            }
        }
    }

    private void updateSessionInfoMarshalledData(SessionInfo sessionInfo, String testMethodName) { 
        byte [] origMarshalledBytes = managedSessionInfoDataMap.get(sessionInfo); 

        if( Arrays.equals(origMarshalledBytes, sessionInfo.getData()) ) {
            // If the marshalled data in this object has NOT been changed, skip this object.
            return; 
        }

        // Retrieve the most recent marshalled data for this object that was saved in this test method
        byte [] lastMarshalledData = lastSessionMarshalledDataForTestMethodMap.get(testMethodName);
        // ? If there has been no data persisted for this object for this test method (yet), 
        // ? Or if the most recently persisted data is NOT the same as what's now been persisted, 
        // ->  then it's "new" marshalled data, so save it in a MarshalledData object.
        if( lastMarshalledData == null || ! Arrays.equals(lastMarshalledData, sessionInfo.getData()) ) {
            MarshalledData marshalledData = new MarshalledData(sessionInfo);
            em.persist(marshalledData);
            lastSessionMarshalledDataForTestMethodMap.put(marshalledData.testMethodName, marshalledData.rulesByteArray);
            logger.info("-!-: " + marshalledData);
        }
    }

    private void updateWorkItemInfoMarshalledData(WorkItemInfo workItemInfo, String testMethodName) { 
        byte [] origMarshalledBytes = managedWorkItemInfoDataMap.get(workItemInfo); 

        if( Arrays.equals(origMarshalledBytes, workItemInfo.getWorkItemByteArray()) ) { 
            // If the marshalled data in this object has NOT been changed, skip this object.
            return; 
        }

        // Retrieve the most recent marshalled data for this object that was saved in this test method
        byte [] lastMarshalledData = lastWorkItemMarshalledDataForTestMethodMap.get(testMethodName);
        // ? If there has been no data persisted for this object for this test method (yet), 
        // ? Or if the most recently persisted data is NOT the same as what's now been persisted, 
        // ->  then it's "new" marshalled data, so save it in a MarshalledData object.
        if( lastMarshalledData == null || ! Arrays.equals(lastMarshalledData, workItemInfo.getWorkItemByteArray()) ) {
            MarshalledData marshalledData = new MarshalledData(workItemInfo);
            em.persist(marshalledData);
            lastWorkItemMarshalledDataForTestMethodMap.put(marshalledData.testMethodName, marshalledData.rulesByteArray);
            logger.info("-!-: " + marshalledData);
        }
    }
   
    private void updateProcessInstanceInfoMarshalledData(Object processInstanceInfoObject, String testMethodName) { 
        byte [] origMarshalledBytes = managedProcessInstanceInfoDataMap.get(processInstanceInfoObject);

        byte [] currMarshalledBytes = getProcessInstanceInfoByteArray(processInstanceInfoObject);
        if( Arrays.equals(origMarshalledBytes, currMarshalledBytes) ) { 
            // If the marshalled data in this object has NOT been changed, skip this object.
            return; 
        }

        // Retrieve the most recent marshalled data for this object that was saved in this test method
        byte [] lastMarshalledData = lastProcessInstanceMarshalledDataForTestMethodMap.get(testMethodName);
        // ? If there has been no data persisted for this object for this test method (yet), 
        // ? Or if the most recently persisted data is NOT the same as what's now been persisted, 
        // ->  then it's "new" marshalled data, so save it in a MarshalledData object.
        if( lastMarshalledData == null || ! Arrays.equals(lastMarshalledData, currMarshalledBytes) ) { 
            MarshalledData marshalledData = new MarshalledData(processInstanceInfoObject);
            em.persist(marshalledData);
            lastProcessInstanceMarshalledDataForTestMethodMap.put(marshalledData.testMethodName, marshalledData.rulesByteArray);
            logger.info("-!-: " + marshalledData);
        }
    }
}
