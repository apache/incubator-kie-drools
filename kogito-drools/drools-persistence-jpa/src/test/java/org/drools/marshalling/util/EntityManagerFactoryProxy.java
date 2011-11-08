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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
public class EntityManagerFactoryProxy implements InvocationHandler {

    private static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryProxy.class);
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    protected static ThreadLocal<HashMap<SessionInfo, byte []>> managedSessionInfoDataMap;
    protected static ThreadLocal<HashMap<WorkItemInfo, byte []>> managedWorkItemInfoDataMap;
    protected static ThreadLocal<HashMap<Object, byte []>> managedProcessInstanceInfoDataMap;
    
    protected static ThreadLocal<HashMap<Integer, byte[]>> sessionMarshalledDataMap;
    protected static ThreadLocal<HashMap<Long, byte[]>> workItemMarshalledDataMap;
    protected static ThreadLocal<HashMap<Long, byte[]>> processInstanceInfoMarshalledDataMap;
        
    /**
     * This method creates a proxy for either a {@link EntityManagerFactory} or a {@link EntityManager} instance. 
     * @param obj The original instance for which a proxy will be made.
     * @return Object a proxy instance of the given object.
     */
    public static Object newInstance( Object obj ) {
        if( obj instanceof EntityManagerFactory  || obj instanceof EntityManager ) { 
            return Proxy.newProxyInstance(
                    obj.getClass().getClassLoader(), 
                    getAllInterfaces(obj),
                    new EntityManagerFactoryProxy(obj));
        }
        else { 
            throw new UnsupportedOperationException("This proxy is only for " 
                    + EntityManagerFactory.class.getSimpleName() + " and " + EntityManager.class.getSimpleName() + " instances." );
        }
    }
  
    /**
     * This method is used in the {@link #newInstance(Object)} method to retrieve all applicable interfaces
     *   that the proxy object must conform to. 
     * @param obj The object that will be proxied. 
     * @return Class<?> [] an array of all applicable interfaces.
     */
    protected static Class<?> [] getAllInterfaces( Object obj ) { 
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
    private EntityManagerFactoryProxy(Object obj ) { 
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

    private synchronized void lazyInitializeStateMaps(Object [] args) { 
        if( args == null || args.length == 0 ) { 
            return;
        }
        
        if( args[0] instanceof SessionInfo ) { 
            if( managedSessionInfoDataMap == null ) { 
                managedSessionInfoDataMap = new ThreadLocal<HashMap<SessionInfo, byte[]>>();
                sessionMarshalledDataMap = new ThreadLocal<HashMap<Integer, byte[]>>();
            }
            if( managedSessionInfoDataMap.get() == null ) {
                managedSessionInfoDataMap.set(new HashMap<SessionInfo, byte[]>());
                sessionMarshalledDataMap.set(new HashMap<Integer, byte[]>());
            }
        }
        else if( args[0] instanceof WorkItemInfo ) { 
           if( managedWorkItemInfoDataMap == null ) {  
               managedWorkItemInfoDataMap = new ThreadLocal<HashMap<WorkItemInfo, byte[]>>();
               workItemMarshalledDataMap = new ThreadLocal<HashMap<Long, byte[]>>();
           }
           if( managedWorkItemInfoDataMap.get() == null ) { 
               managedWorkItemInfoDataMap.set(new HashMap<WorkItemInfo, byte[]>());
               workItemMarshalledDataMap.set(new HashMap<Long, byte[]>());
           }
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(args[0].getClass().getName()) ) { 
            if( managedProcessInstanceInfoDataMap == null ) { 
                managedProcessInstanceInfoDataMap = new ThreadLocal<HashMap<Object, byte[]>>();
                processInstanceInfoMarshalledDataMap = new ThreadLocal<HashMap<Long, byte[]>>();
            }
            if( managedProcessInstanceInfoDataMap.get() == null ) { 
                managedProcessInstanceInfoDataMap.set(new HashMap<Object, byte[]>());
                processInstanceInfoMarshalledDataMap.set(new HashMap<Long, byte[]>());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        String methodName = method.getName();
      
        logger.trace(methodName);
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
            em.joinTransaction();
            String testMethodName = MarshallingTestUtil.getTestMethodName();
            if( testMethodName != null ) { 
                updateManagedObjects(testMethodName, em); 
            }
            return result;
        }
        else if( "find".equals(methodName) && args.length == 2) {
            result = em.find((Class<?>) args[0], args[1]);
            find(result);
        }
        else { 
            Class<?> methodClass = method.getDeclaringClass();
            if( methodClass.equals(EntityManagerFactory.class) ) { 
                result = invoke(method, emf, args);
            }
            else if( methodClass.equals(EntityManager.class) ) { 
                result = invoke(method, em, args);
            }
            else { 
                RuntimeException re = new RuntimeException("Unexpected class " + methodClass + " for method " + methodName );
                re.fillInStackTrace();
                throw re;
            }
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
        return newInstance(realEm);
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
            managedSessionInfoDataMap.get().put(sessionInfo, byteArray != null ? byteArray.clone() : null );
            if( byteArray != null ) { 
                marshalledData = new MarshalledData(sessionInfo);
                em.persist(marshalledData);
                logger.trace("-.-: " + marshalledData);
            }
        }
        else if( args[0] instanceof WorkItemInfo ) { 
            WorkItemInfo workItemInfo = (WorkItemInfo) args[0];
            byte [] byteArray = getWorkItemByteArray(workItemInfo);
            managedWorkItemInfoDataMap.get().put(workItemInfo, byteArray != null ? byteArray.clone() : null );
            if( byteArray != null ) { 
                marshalledData = new MarshalledData(workItemInfo);
                em.persist(marshalledData);
                logger.trace("-.-: " + marshalledData);
            }
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(args[0].getClass().getName()) ) { 
            byte [] byteArray = MarshallingTestUtil.getProcessInstanceInfoByteArray(args[0]);
            managedProcessInstanceInfoDataMap.get().put(args[0], byteArray != null ? byteArray.clone() : null);
            Long id = MarshallingTestUtil.getProcessInstanceInfoId(args[0]);
            processInstanceInfoMarshalledDataMap.get().put(id, byteArray);
            if( byteArray != null ) { 
                marshalledData = new MarshalledData(args[0]);
                em.persist(marshalledData);
                logger.trace("-.-: " + marshalledData);
            }
        }
    }
    
    private void merge(String testMethodName, Object updatedObject) {
        if( updatedObject instanceof SessionInfo ) { 
            HashMap<SessionInfo, byte[]> updatedObjectsMap = new HashMap<SessionInfo, byte[]>();
            updateSessionInfoMarshalledData((SessionInfo) updatedObject, testMethodName, em, updatedObjectsMap);
            if( ! updatedObjectsMap.isEmpty() ) { 
                managedSessionInfoDataMap.get().put((SessionInfo) updatedObject, updatedObjectsMap.get(updatedObject));
            }
        }
        if( updatedObject instanceof WorkItemInfo ) { 
            HashMap<WorkItemInfo, byte[]> updatedObjectsMap = new HashMap<WorkItemInfo, byte[]>();
            updateWorkItemInfoMarshalledData((WorkItemInfo) updatedObject, testMethodName, em, updatedObjectsMap);
            if( ! updatedObjectsMap.isEmpty() ) { 
                managedWorkItemInfoDataMap.get().put((WorkItemInfo) updatedObject, updatedObjectsMap.get(updatedObject));
            }
        }
        if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(updatedObject.getClass().getName()) ) { 
            HashMap<Object, byte[]> updatedObjectsMap = new HashMap<Object, byte[]>();
            updateProcessInstanceInfoMarshalledData(updatedObject, testMethodName, em, updatedObjectsMap);
            if( ! updatedObjectsMap.isEmpty() ) { 
                managedProcessInstanceInfoDataMap.get().put(updatedObject, updatedObjectsMap.get(updatedObject));
            }
        }
    }

    private void find(Object result) { 
        if( result == null ) { 
            return;
        }
        
        if( result instanceof SessionInfo ) { 
            byte [] data = managedSessionInfoDataMap.get().get(result);
            if( data == null ) { 
                byte [] byteArray = ((SessionInfo) result).getData();
                managedSessionInfoDataMap.get().put((SessionInfo) result, byteArray);
            }
        }
        else if( result instanceof WorkItemInfo ) { 
            byte [] data = managedWorkItemInfoDataMap.get().get(result);
            if( data == null ) { 
                byte [] byteArray = getWorkItemByteArray((WorkItemInfo) result);
                managedWorkItemInfoDataMap.get().put((WorkItemInfo) result, byteArray);
            }
        }
        else if( PROCESS_INSTANCE_INFO_CLASS_NAME.equals(result.getClass().getName())) { 
            byte [] data = managedProcessInstanceInfoDataMap.get().get(result);
            if( data == null ) { 
                byte [] byteArray = MarshallingTestUtil.getProcessInstanceInfoByteArray(result);
                managedProcessInstanceInfoDataMap.get().put(result, byteArray);
            }
        }
    }

    protected static void updateManagedObjects(String testMethodName, EntityManager em) {
        // Update the marshalled data belonging to managed SessionInfo objects
        if( managedSessionInfoDataMap != null ) {  
            HashMap<SessionInfo, byte []> updatedObjectsMap = new HashMap<SessionInfo, byte[]>();
            for( SessionInfo sessionInfo : managedSessionInfoDataMap.get().keySet()) { 
                updateSessionInfoMarshalledData(sessionInfo, testMethodName, em, updatedObjectsMap);
            }
            for( SessionInfo sessionInfo : updatedObjectsMap.keySet() ) { 
                managedSessionInfoDataMap.get().put(sessionInfo, updatedObjectsMap.get(sessionInfo)); 
            }
        }
        // Update the marshalled data belonging to managed WorkItemInfo objects
        if( managedWorkItemInfoDataMap != null ) { 
            HashMap<WorkItemInfo, byte []> updatedObjectsMap = new HashMap<WorkItemInfo, byte[]>();
            for( WorkItemInfo workItemInfo : managedWorkItemInfoDataMap.get().keySet() ) { 
                updateWorkItemInfoMarshalledData(workItemInfo, testMethodName, em, updatedObjectsMap);
            }
            for( WorkItemInfo workItemInfo : updatedObjectsMap.keySet() ) { 
                managedWorkItemInfoDataMap.get().put(workItemInfo, updatedObjectsMap.get(workItemInfo)); 
            }
        }
        // Update the marshalled data belonging to managed ProcessInstanceInfo objects
        if( managedProcessInstanceInfoDataMap != null ) { 
            HashMap<Object, byte []> updatedObjectsMap = new HashMap<Object, byte[]>();
            for( Object processInstanceInfo : managedProcessInstanceInfoDataMap.get().keySet() ) { 
                updateProcessInstanceInfoMarshalledData(processInstanceInfo, testMethodName, em, updatedObjectsMap);
            }
            for( Object processInstanceInfoObject : updatedObjectsMap.keySet() ) { 
                managedProcessInstanceInfoDataMap.get().put(processInstanceInfoObject, 
                        updatedObjectsMap.get(processInstanceInfoObject)); 
            }
        }
    }

    private static void updateSessionInfoMarshalledData(SessionInfo sessionInfo, String testMethodName, 
            EntityManager em, HashMap<SessionInfo, byte []> updateManagedSessionInfoMap) { 
        byte [] origMarshalledBytes = managedSessionInfoDataMap.get().get(sessionInfo); 

        if( Arrays.equals(origMarshalledBytes, sessionInfo.getData()) ) {
            // If the marshalled data in this object has NOT been changed, skip this object.
            return; 
        }
        updateManagedSessionInfoMap.put(sessionInfo, sessionInfo.getData());

        // Retrieve the most recent marshalled data for this object that was saved in this test method
        byte [] thisMarshalledData = sessionMarshalledDataMap.get().get(testMethodName);
        // ? If there has been no data persisted for this object for this test method (yet), 
        // ? Or if the most recently persisted data is NOT the same as what's now been persisted, 
        // ->  then it's "new" marshalled data, so save it in a MarshalledData object.
        if( thisMarshalledData == null || ! Arrays.equals(thisMarshalledData, sessionInfo.getData()) ) {
            MarshalledData marshalledData = new MarshalledData(sessionInfo);
            em.persist(marshalledData);
            sessionMarshalledDataMap.get().put(sessionInfo.getId(), marshalledData.byteArray);
            logger.trace("-!-: " + marshalledData);
        }
    }

    private static void updateWorkItemInfoMarshalledData(WorkItemInfo workItemInfo, String testMethodName, 
            EntityManager em, HashMap<WorkItemInfo, byte []> updateManagedWorkItemInfoMap) { 
        byte [] origMarshalledBytes = managedWorkItemInfoDataMap.get().get(workItemInfo); 

        byte [] workItemByteArray = getWorkItemByteArray(workItemInfo);
        if( Arrays.equals(origMarshalledBytes, workItemByteArray) ) { 
            // If the marshalled data in this object has NOT been changed, skip this object.
            return; 
        }
        updateManagedWorkItemInfoMap.put(workItemInfo, workItemByteArray);
        
        // Retrieve the most recent marshalled data for this object that was saved in this test method
        byte [] thisMarshalledData = workItemMarshalledDataMap.get().get(testMethodName);
        // ? If there has been no data persisted for this object for this test method (yet), 
        // ? Or if the most recently persisted data is NOT the same as what's now been persisted, 
        // ->  then it's "new" marshalled data, so save it in a MarshalledData object.
        if( thisMarshalledData == null || ! Arrays.equals(thisMarshalledData, workItemByteArray) ) { 
            MarshalledData marshalledData = new MarshalledData(workItemInfo);
            em.persist(marshalledData);
            workItemMarshalledDataMap.get().put(workItemInfo.getId(), marshalledData.byteArray);
            logger.trace("-!-: " + marshalledData);
        }
    }
   
    private static void updateProcessInstanceInfoMarshalledData(Object processInstanceInfo, String testMethodName, 
            EntityManager em, HashMap<Object, byte []> updateManagedProcessInfoMap) { 
        byte [] origMarshalledBytes = managedProcessInstanceInfoDataMap.get().get(processInstanceInfo);
        byte [] currMarshalledBytes = getProcessInstanceInfoByteArray(processInstanceInfo);
        if( Arrays.equals(origMarshalledBytes, currMarshalledBytes) ) { 
            // If the marshalled data in this object has NOT been changed, skip this object.
            return; 
        }

        updateManagedProcessInfoMap.put(processInstanceInfo, currMarshalledBytes);
        
        // Retrieve the most recent marshalled data for this object that was saved in this test method
        Long id = MarshallingTestUtil.getProcessInstanceInfoId(processInstanceInfo);
        byte [] thisMarshalledData = processInstanceInfoMarshalledDataMap.get().get(id);
        // ? If there has been no data persisted for this object for this test method (yet), 
        // ? Or if the most recently persisted data is NOT the same as what's now been persisted, 
        // ->  then it's "new" marshalled data, so save it in a MarshalledData object.
        if( thisMarshalledData == null || ! Arrays.equals(thisMarshalledData, currMarshalledBytes) ) { 
            MarshalledData marshalledData = new MarshalledData(processInstanceInfo);
            em.persist(marshalledData);
            processInstanceInfoMarshalledDataMap.get().put(id, marshalledData.byteArray);
            logger.trace("-!-: " + marshalledData);
        }
    }
}
