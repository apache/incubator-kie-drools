package org.kie.internal.query;

import java.util.List;

/**
 * Instances of this class can be built by various {@link ParametrizedQueryBuilder} implemetnations, 
 * including those in the jbpm-audit or jbpm-human-task-core modules. 
 * </p>
 * {@link ParametrizedQueryBuilder} implementations are responsible for allowing a user
 * to dynamically (at compile-time) specify the criteria to be used in querying various
 * data sources.
 * </p>
 * The result of the {@link ParametrizedQueryBuilder} is an instance of this class, which contains
 * the criteria specified in the {@link ParametrizedQueryBuilder} instance. When the 
 * {@link ParametrizedQuery#getResultList()} method is called, the specified query will 
 * be executed, returning a (possibly empty) list of the data that fulfills the query criteria. 
 * 
 * @param <T> The type being queried and of the (results) list being returned.
 */
public interface ParametrizedQuery<T> {

    /**
     * Execute the query and return the list of entities found by the query.
     * @return The query result
     */
    List<T> getResultList(); 
    
}
