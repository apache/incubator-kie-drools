package org.jbpm.query.jpa.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;

import org.jbpm.query.jpa.data.QueryCriteria;
import org.jbpm.query.jpa.data.QueryWhere;
import org.jbpm.query.jpa.impl.QueryCriteriaUtil;

/**
 * Implementations of this service are instantiated when available in order to
 * extend the capabilities of particular {@link QueryCriteriaUtil} implemenations.
 */
public interface QueryModificationService {

    /**
     * @param listId The id of the {@link QueryCriteria}
     * @return Whether or not this {@link QueryModificationService} can be used for the given listId.
     */
    public boolean accepts(String listId);

    /**
     * This optimizes the {@link QueryWhere} criteria.
     * @param queryWhere The {@link QueryWhere} instance with the abstract query information
     */
    public void optimizeCriteria( QueryWhere queryWhere );

    /**
     * Create a specific {@link Predicate} based on the given {@link QueryCriteria}.
     *
     * @param criteria The {@link QueryCriteria} with the abstract query criteria information.
     * @param query The {@link CriteriaQuery} instance being built.
     * @param builder The {@link CriteriaBuilder} used to create the {@link CriteriaQuery}.
     * @return The {@link Predicate} that will be added to the {@link CriteriaQuery} instance.
     */
    public <R> Predicate createPredicate(QueryCriteria criteria, CriteriaQuery<R> query, CriteriaBuilder builder);

}
