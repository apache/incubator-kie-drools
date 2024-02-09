/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.persistence.jpa.marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import org.drools.base.common.DroolsObjectInputStream;
import org.drools.persistence.api.TransactionAware;
import org.drools.persistence.api.TransactionManager;
import org.drools.serialization.protobuf.ProtobufProcessMarshallerWriteContext;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAPlaceholderResolverStrategy implements ObjectMarshallingStrategy, TransactionAware, Cacheable {
    private static Logger log = LoggerFactory.getLogger(JPAPlaceholderResolverStrategy.class);
    private EntityManagerFactory emf;
    private Set<String> managedClasses;
    private ClassLoader classLoader;

    private boolean closeEmf = false;
    private String name = JPAPlaceholderResolverStrategy.class.getName();
    
    private static final ThreadLocal<EntityPersister> persister = new ThreadLocal<>();
    
    public JPAPlaceholderResolverStrategy(Environment env) {
        this( (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY) );
    }

    public JPAPlaceholderResolverStrategy(EntityManagerFactory emf) {
        this.emf = emf;
        initializeManagedClasses( );
    }

    public JPAPlaceholderResolverStrategy(String persistenceUnit, ClassLoader cl) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        try {
            // override tccl so persistence unit can be found from within given class loader - e.g. kjar
            Thread.currentThread().setContextClassLoader(cl);

            this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
            initializeManagedClasses();
            this.closeEmf = true;

        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
        this.classLoader = cl;
    }
    
    public JPAPlaceholderResolverStrategy(String name, String persistenceUnit, ClassLoader cl) {
    	this( persistenceUnit, cl );
    	this.name = name;
    }
    
    public String getName(){
    	return this.name;
    }
    
    private void initializeManagedClasses(){
    	managedClasses = new HashSet<>();
    	if( emf != null ){
	    	Metamodel metamodel = emf.getMetamodel();
	     	if( metamodel != null ){
	     		Set<EntityType<?>> entities = metamodel.getEntities();
	     		for( EntityType<?> entity : entities ){
	     			managedClasses.add( entity.getJavaType().getCanonicalName() );
	     		}
	     	}  
    	}
    }
    public boolean accept(Object object) {
        return isEntity(object);
    }

    public void write(ObjectOutputStream os, Object object) throws IOException {
        Object id = getClassIdValue(object);
        EntityManager em = getEntityManager();
        if (id == null) {
            em.persist(object);
            id = getClassIdValue(object);
        } else {
            em.merge(object);
        }
        // since this is invoked by marshaller it's safe to call flush
        // and it's important to be flushed so subsequent unmarshall operations
        // will get update content especially when merged
        em.flush();
        os.writeUTF(object.getClass().getCanonicalName());
        os.writeObject(id);
    }

    public Object read(ObjectInputStream is) throws IOException, ClassNotFoundException {
        String canonicalName = is.readUTF();
        Object id = is.readObject();

        EntityManager em = getEntityManager();
        return em.find(Class.forName(canonicalName), id);
    }

    public byte[] marshal(Context context,
                          ObjectOutputStream os, 
                          Object object) throws IOException {
        Object id = getClassIdValue(object);
        String entityType = object.getClass().getCanonicalName();
        
        EntityPersister entityPersister = persister.get();

        if (!entityPersister.isPersited(object)) {
            EntityManager em = getEntityManager();
            if (id == null) {
                em.persist(object);
                id = getClassIdValue(object);
            }
            addMapping(id, entityType, object, os, em);
            em.merge(object);
            
            entityPersister.processed(object);
            
            // since this is invoked by marshaller it's safe to call flush
            // and it's important to be flushed so subsequent unmarshall operations
            // will get update content especially when merged
            em.flush();
        }
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( buff );
        oos.writeUTF(entityType);
        oos.writeObject(id);
        oos.close();
        return buff.toByteArray();
    }

    public Object unmarshal(Context context,
                            ObjectInputStream ois,
                            byte[] object,
                            ClassLoader classloader) throws IOException,
                                                    ClassNotFoundException {
        ClassLoader clToUse = classloader;
        if (this.classLoader != null) {
            clToUse = this.classLoader;
        }

        DroolsObjectInputStream is = new DroolsObjectInputStream( new ByteArrayInputStream( object ), clToUse );
        String canonicalName = is.readUTF();
        Object id = is.readObject();

        EntityManager em = getEntityManager();
        return em.find(Class.forName(canonicalName, true, (clToUse==null?this.getClass().getClassLoader():clToUse)), id);
    }
    
    public Context createContext() {
        // no need for context
        return null;
    }
    
    public Serializable getClassIdValue(Object o)  {
        return  (Serializable) emf.getPersistenceUnitUtil().getIdentifier( o );
    }
    
    /**
     * Changed implementation, using EntityManager Metamodel in spite of Reflection. 
     * @param o
     * @return
     */
    private boolean isEntity(Object o){
        Class<? extends Object> varClass = o.getClass();
        return managedClasses.contains( varClass.getCanonicalName() );
    }

    @Override
    public void onStart(TransactionManager txm) {
        if (persister.get() == null) {
            EntityManager em = emf.createEntityManager();
            persister.set(new EntityPersister(em));
        }
    }

    @Override
    public void onEnd(TransactionManager txm) {
        EntityPersister em = persister.get();
        if (em != null) {
            em.close();
            persister.set(null);
        }
    }

    protected EntityManager getEntityManager() {
        EntityPersister em = persister.get();
        if (em != null && em.getEntityManager() != null) {
            return em.getEntityManager();
        }
        return emf.createEntityManager();
    }

    @Override
    public void close() {
        if (closeEmf && this.emf != null) {
            this.emf.close();
            this.emf = null;
        }
    }

    protected void addMapping(Object entityId, String entityType, Object entity, ObjectOutputStream context, EntityManager em) {
        if (entityId instanceof Number && entity instanceof VariableEntity && context instanceof ProtobufProcessMarshallerWriteContext ) {

            ProtobufProcessMarshallerWriteContext processContext = (ProtobufProcessMarshallerWriteContext) context;
            VariableEntity variableEntity = (VariableEntity) entity;

            MappedVariable mappedVariable = new MappedVariable(((Number)entityId).longValue(), entityType, processContext.getProcessInstanceId(), processContext.getTaskId(), processContext.getWorkItemId());
            if (processContext.getState() == ProtobufProcessMarshallerWriteContext.STATE_ACTIVE) {
                variableEntity.addMappedVariables(mappedVariable);
            } else {
                MappedVariable toBeRemoved = variableEntity.findMappedVariables(mappedVariable);
                if (toBeRemoved != null) {
                    toBeRemoved = em.find(MappedVariable.class, toBeRemoved.getMappedVarId());
                    em.remove(toBeRemoved);

                    variableEntity.removeMappedVariables(toBeRemoved);
                }
            }
        }
    }
}
