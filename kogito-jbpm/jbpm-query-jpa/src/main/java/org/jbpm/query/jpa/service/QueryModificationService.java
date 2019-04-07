/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
