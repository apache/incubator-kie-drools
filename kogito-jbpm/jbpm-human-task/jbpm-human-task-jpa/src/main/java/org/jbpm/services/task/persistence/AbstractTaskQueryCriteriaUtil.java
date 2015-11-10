package org.jbpm.services.task.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;
import org.kie.internal.task.api.TaskPersistenceContext;

public class AbstractTaskQueryCriteriaUtil extends QueryCriteriaUtil {

    protected final JPATaskPersistenceContext persistenceContext;

    public AbstractTaskQueryCriteriaUtil(TaskPersistenceContext persistenceContext) {
        this.persistenceContext = (JPATaskPersistenceContext) persistenceContext;
    }

    public AbstractTaskQueryCriteriaUtil() {
        this.persistenceContext = null;
    }

    protected EntityManager getEntityManager() {
        return this.persistenceContext.getEntityManager();
    }

    protected Object joinTransaction(EntityManager em) {
        this.persistenceContext.joinTransaction();
        return true;
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    protected void closeEntityManager(EntityManager em, Object transaction) {
        // do nothing
    }

    @Override
    protected boolean initializeCriteriaAttributes() {
        return shouldBeImplementedInChildClass(boolean.class);
    }

    @Override
    protected <R, T> Predicate implSpecificCreatePredicateFromSingleCriteria( CriteriaQuery<R> query, CriteriaBuilder builder,
            Class queryType, QueryCriteria criteria, QueryWhere queryWhere ) {
        return shouldBeImplementedInChildClass(Predicate.class);
    }

    @Override
    protected <T> List<T> createQueryAndCallApplyMetaCriteriaAndGetResult(QueryWhere queryWhere, CriteriaQuery<T> criteriaQuery, CriteriaBuilder builder) {
        EntityManager em = getEntityManager();
        Object newTx = joinTransaction(em);
        Query query = em.createQuery(criteriaQuery);

        applyMetaCriteriaToQuery(query, queryWhere);

        // execute query
        List<T> result = query.getResultList();

        // depending on the context, this is done
        // 1. here
        // 1. *outside* of this class (this method is a no-op)
        closeEntityManager(em, newTx);

        return result;
    }

    private static <T> T shouldBeImplementedInChildClass(Class<T> returnClass) {
       String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
       throw new IllegalAccessError(AbstractTaskQueryCriteriaUtil.class.getSimpleName() + "." + methodName + " should be overridden in the extending class!");
    }
}
