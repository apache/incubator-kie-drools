package org.drools.persistence.jpa;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.drools.KnowledgeBase;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.persistence.MarshallerBasedApplicationScopedPersistenceContext;
import org.drools.persistence.TransactionablePersistentContext;
import org.drools.persistence.info.SessionInfo;

public class JpaApplicationScopedPersistenceContext
        extends MarshallerBasedApplicationScopedPersistenceContext
    implements
    TransactionablePersistentContext {

    private EntityManager em;

    public JpaApplicationScopedPersistenceContext(EntityManager em,
                                                  KnowledgeBase kbase,
                                                  List<ObjectMarshallingStrategy> marshallingStrategies) {
        super( kbase,
               marshallingStrategies );
        this.em = em;
    }

    @Override
    protected byte[] internalLoadStatefulKnowledgeSession(long sessionId) {
        return em.find( SessionInfo.class,
                        sessionId ).getData();
    }

    @Override
    protected long internalSaveStatefulKnowledgeSession(byte[] byteArray) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setData( byteArray );
        em.persist( sessionInfo );
        return sessionInfo.getId();
    }

    @Override
    protected void internalUpdateStatefulKnowledgeSession(long id,
                                                          byte[] byteArray) {
        SessionInfo sessionInfo = em.find( SessionInfo.class,
                                           id );
        sessionInfo.setData( byteArray );
        em.persist( sessionInfo );
    }

    public void setLastModificationDate(long sessionId) {
        SessionInfo sessionInfo = em.find( SessionInfo.class,
                                           sessionId );
        sessionInfo.setLastModificationDate( Calendar.getInstance().getTime() );
        em.persist( sessionInfo );
    }

    public boolean isOpen() {
        return this.em.isOpen();
    }

    public void joinTransaction() {
        this.em.joinTransaction();
    }

    public void close() {
        this.em.close();
    }

}
