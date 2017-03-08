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
package org.drools.persistence.mapdb.marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.marshalling.impl.ProcessMarshallerWriteContext;
import org.drools.persistence.TransactionAware;
import org.drools.persistence.TransactionManager;
import org.drools.persistence.mapdb.MapDBEnvironmentName;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.internal.runtime.Cacheable;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Serializer;

public class MapDBPlaceholderResolverStrategy implements ObjectMarshallingStrategy, TransactionAware, Cacheable {

	private DB db;
    private ClassLoader classLoader;

    public MapDBPlaceholderResolverStrategy(Environment env) {
        this.db = (DB) env.get(MapDBEnvironmentName.DB_OBJECT);
    }

    public MapDBPlaceholderResolverStrategy(DB db) {
        this.db = db;
    }

    public boolean accept(Object object) {
    	return db.get(object.getClass().getSimpleName()) != null;
    }

    public void write(ObjectOutputStream os, Object object) throws IOException {
    	long id = db.getStore().preallocate();
    	String canonicalName = object.getClass().getCanonicalName();
        BTreeMap<Long, Object> map = getGenericMap(canonicalName);
        map.put(id, object);
		os.writeUTF(canonicalName);
        os.writeLong(id);
    }

	private BTreeMap<Long, Object> getGenericMap(String canonicalName) {
		BTreeMap<Long, Object> map = db.treeMap(canonicalName, Serializer.LONG, Serializer.JAVA).createOrOpen();
		return map;
	}

    public Object read(ObjectInputStream is) throws IOException, ClassNotFoundException {
        String canonicalName = is.readUTF();
        long id = is.readLong();
        return getGenericMap(canonicalName).get(id);
    }

    public byte[] marshal(Context context,
                          ObjectOutputStream os, 
                          Object object) throws IOException {
    	long id = db.getStore().preallocate();
        String entityType = object.getClass().getCanonicalName();
        getGenericMap(entityType).put(id, object);
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( buff );
        oos.writeUTF(entityType);
        oos.writeLong(id);
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
        long id = is.readLong();
        return getGenericMap(canonicalName).get(id);
    }
    
    public Context createContext() {
        // no need for context
        return null;
    }
    
    @Override
    public void onStart(TransactionManager txm) {
    }

    @Override
    public void onEnd(TransactionManager txm) {
    }

    @Override
    public void close() {
    }
}
