package org.drools.marshalling.util;

import java.util.List;

import javax.persistence.EntityManager;

import org.drools.persistence.jta.JtaTransactionManager;

public class MarshallingTestUtil {

    @SuppressWarnings("unchecked")
    public static void goLookAtTheMarshallingDataAndDoStuff(EntityManager em) { 
        JtaTransactionManager txm = new JtaTransactionManager(null, null, null);
        boolean txOwner = txm.begin();
        
        List<Object> mdList = em.createQuery("SELECT m FROM MarshalledData m").getResultList();
        mdList.size();
        //for( Object resultObject : resultList ) { 
//            MarshalledData marshalledData = (MarshalledData) resultObject;
//        }
        
        List<Object> siList = em.createQuery("SELECT s FROM SessionInfo s").getResultList();
        siList.size();
//        for( Object resultObject : resultList ) { 
//            SessionInfo sessionInfo = (SessionInfo) resultObject;
//        }
        
        txm.commit(txOwner);
    }
    

}
