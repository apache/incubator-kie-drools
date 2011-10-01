package org.drools.marshalling.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class CacheEntityManagerFactory implements EntityManagerFactory {

    HashMap<Long, MarshalledData> marshalledDataCache;
    
    public CacheEntityManagerFactory(List<MarshalledData> marshalledDataList) { 
        marshalledDataCache = new HashMap<Long, MarshalledData>();
        for(MarshalledData marshalledData : marshalledDataList ) { 
           marshalledDataCache.put(marshalledData.marshalledObjectId, marshalledData);
        }
    }
    
    public EntityManager createEntityManager() {
        return new CacheEntityManager(marshalledDataCache);
    }

    public EntityManager createEntityManager(Map map) {
        return new CacheEntityManager(marshalledDataCache);
    }

    public void close() {
        marshalledDataCache = null;
    }

    public boolean isOpen() {
        return (marshalledDataCache != null);
    }

}
