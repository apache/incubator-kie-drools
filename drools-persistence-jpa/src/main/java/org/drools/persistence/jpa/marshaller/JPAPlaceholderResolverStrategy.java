/*
 * Copyright 2010 salaboy.
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
 * under the License.
 */
package org.drools.persistence.jpa.marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;

import org.drools.core.common.DroolsObjectInputStream;
import org.drools.persistence.TransactionAware;
import org.drools.persistence.TransactionManager;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAPlaceholderResolverStrategy implements ObjectMarshallingStrategy, TransactionAware, Cacheable {
    private static Logger log = LoggerFactory.getLogger(JPAPlaceholderResolverStrategy.class);
    private EntityManagerFactory emf;
    private ClassLoader classLoader;

    private boolean closeEmf = false;

    private static final ThreadLocal<EntityManager> persister = new ThreadLocal<EntityManager>();
    
    public JPAPlaceholderResolverStrategy(Environment env) {
        this.emf = (EntityManagerFactory) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
    }

    public JPAPlaceholderResolverStrategy(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public JPAPlaceholderResolverStrategy(String persistenceUnit, ClassLoader cl) {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();

        try {
            // override tccl so persistence unit can be found from within given class loader - e.g. kjar
            Thread.currentThread().setContextClassLoader(cl);

            this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
            this.closeEmf = true;

        } finally {
            Thread.currentThread().setContextClassLoader(tccl);
        }
        this.classLoader = cl;
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

        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( buff );
        oos.writeUTF(object.getClass().getCanonicalName());
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
    
    public static Serializable getClassIdValue(Object o)  {
        Class<? extends Object> varClass = o.getClass();
        Serializable idValue = null;
        try{
            do {
                Field[] fields = varClass.getDeclaredFields();
                for (int i = 0; i < fields.length && idValue == null; i++) {
                    Field field = fields[i];
                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                        try {
                            idValue = callIdMethod(o, "get"
                                    + Character.toUpperCase(field.getName().charAt(0))
                                    + field.getName().substring(1));
                        } catch (NoSuchMethodException e) {
                            idValue = (Serializable) field.get(o);
                        }
                    }
                }
            } while ((varClass = varClass.getSuperclass()) != null && idValue == null);
            if (idValue == null) {
                varClass = o.getClass();
                do {
                    Method[] methods = varClass.getMethods();
                    for (int i = 0; i < methods.length && idValue == null; i++) {
                        Method method = methods[i];
                        Id id = method.getAnnotation(Id.class);
                        if (id != null) {
                            idValue = (Serializable) method.invoke(o);
                        }
                    }
                } while ((varClass = varClass.getSuperclass()) != null && idValue == null);
            }
        }
        catch(Exception ex){
            log.error(ex.getMessage());
        }
        return idValue;
    }

    private static Serializable callIdMethod(Object target, String methodName) throws IllegalArgumentException,
            SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return (Serializable) target.getClass().getMethod(methodName, (Class[]) null).invoke(target, new Object[]{});
    }
    
    private static boolean isEntity(Object o){
        Class<? extends Object> varClass = o.getClass();
        do {
                Field[] fields = varClass.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    Id id = field.getAnnotation(Id.class);
                    if (id != null) {
                       return true;
                    }
                }
        } while ((varClass = varClass.getSuperclass()) != null);
        varClass = o.getClass();
        do {
                    Method[] methods = varClass.getMethods();
                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        Id id = method.getAnnotation(Id.class);
                        if (id != null) {
                            return true;
                        }
                    }
        } while ((varClass = varClass.getSuperclass()) != null );
        
        return false;
    }

    @Override
    public void onStart(TransactionManager txm) {
        if (persister.get() == null) {
            EntityManager em = emf.createEntityManager();
            persister.set(em);
        }
    }

    @Override
    public void onEnd(TransactionManager txm) {
        EntityManager em = persister.get();
        if (em != null) {
            em.close();
            persister.set(null);
        }
    }

    protected EntityManager getEntityManager() {
        EntityManager em = persister.get();
        if (em != null) {
            return em;
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
}
