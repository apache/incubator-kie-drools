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
package org.drools.persistence.infinispan.marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Id;

import org.drools.core.common.DroolsObjectInputStream;
import org.hibernate.search.annotations.Indexed;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfinispanPlaceholderResolverStrategy implements ObjectMarshallingStrategy {
    private static Logger log = LoggerFactory.getLogger(InfinispanPlaceholderResolverStrategy.class);
    private Environment env;
    
    public InfinispanPlaceholderResolverStrategy(Environment env) {
        this.env = env;
    }
    
    public boolean accept(Object object) {
        return isEntity(object) && isIndexed(object); //left for markup purposes
    }

	public void write(ObjectOutputStream os, Object object) throws IOException {
    	os.writeObject(getTypeString(object) + getClassIdValue(object));
    }

    public Object read(ObjectInputStream is) throws IOException, ClassNotFoundException {
        Object id = is.readObject();
        DefaultCacheManager cm = (DefaultCacheManager) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        Cache<?, ?> cache = cm.getCache("jbpm-configured-cache");
        return cache.get(id);
    }

    public byte[] marshal(Context context,
                          ObjectOutputStream os, 
                          Object object) throws IOException {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( buff );
        oos.writeObject(getTypeString(object) + getClassIdValue(object));
        
        oos.close();
        return buff.toByteArray();
    }

    public Object unmarshal(Context context,
                            ObjectInputStream ois,
                            byte[] object,
                            ClassLoader classloader) throws IOException,
                                                    ClassNotFoundException {
        DroolsObjectInputStream is = new DroolsObjectInputStream( new ByteArrayInputStream( object ), classloader );
        Object id = is.readObject();
        DefaultCacheManager cm = (DefaultCacheManager) env.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        Cache<?, ?> cache = cm.getCache("jbpm-configured-cache");
        return cache.get(id);
    }
    
    public Context createContext() {
        // no need for context
        return null;
    }
    
    public static String getTypeString(Object o) {
    	String typeValue = null;
    	try {
    		Field[] fields = o.getClass().getDeclaredFields();
    		for (int i = 0; i < fields.length && typeValue == null; i++) {
    			Field field = fields[i];
    			if (field.getName().equals("type")) {
    				try {
    					String methodName = "get" + Character.toUpperCase(field.getName().charAt(0))
                            + field.getName().substring(1);
                        typeValue = (String) o.getClass().getMethod(methodName, (Class[]) null).invoke(o, new Object[]{});
                    } catch (NoSuchMethodException e) {
                        typeValue = (String) field.get(o);
                    }
    			}
    		}
    	} catch (Exception e) {
    		log.error(e.getMessage(), e);
    	}
    	if (typeValue == null) {
    		typeValue = "";
    	}
    	return typeValue;
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
    
    private static boolean isIndexed(Object o) {
        Class<? extends Object> varClass = o.getClass();
        do {
        	Indexed idx = varClass.getAnnotation(Indexed.class);
        	if (idx != null) {
        		return true;
        	}
        } while ((varClass = varClass.getSuperclass()) != null);
        return false;
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

}
