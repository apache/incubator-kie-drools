package org.kie.internal.query;


/**
 * This is the base interface for all {@link ParametrizedQueryBuilder} implementations. 
 * </p>
 * It includes the basic query functions.
 *
 * @param <T> The type of {@link ParametrizedQueryBuilder} instance being implemented. This type
 * is here to facilitate the building of a fluent interface. 
 */
public interface ParametrizedQueryBuilder<T> {

    /**
     * Query criteria which are added to the query after this method
     * are "OR" or "union" criteria. In other words, the query will return 
     * results which match any of the criteria added (as opposed to having 
     * to match <i>all</i> of the criteria added).
     * </p>
     * This is the default mode for all query builders. 
     * @return the current query builder instance
     */
    public T union();
    
    /**
     * Query criteria which are added to the query after this method
     * are "AND" or "intersection" criteria. In other words, the query 
     * will only return results which match all of the criteria added 
     * (as opposed to returning results that match <i>any</i> of the 
     * criteria added).
     * @return the current query builder instance
     */
    public T intersect();
 
    /**
     * Query criteria which are added to the query after this method
     * are regular expression (a.k.a "regex") criteria. In other words, 
     * the query will return results which match the regular expressions
     * specified. 
     * </p>
     * Only String criteria may be added after using this method. In order 
     * to go back to adding normal or non-regex criteria, use the {@link #equals()} 
     * method.
     * </p>
     * The following characters may be used:<ul>
     *  <li>.<ul>
     *    <li>This character matches any <em>single</em> character.</li>
     *   </ul></li>
     *   <li>*<ul>
     *    <li>This character matches <em>zero or more</em> characters.</li>
     *   </ul></li>
     *   </ul>
     * @return the current query builder instance
     */
    public T like();
    
    /**
     * Results retrieved using query criteria added after this method
     * is used, must <em>exactly</em> match the criteria given. 
     * </p>
     * If the {@link #like()} method has been used, using this method
     * will ensure that criteria added after this method are literally
     * interpreted, and <em>not</em> seen as regular expressions.
     * </p>
     * This is the default mode for all query builders. 
     * @return the current query builder instance
     */
    public T equals();
    
    /**
     * Clear all parameters and meta-criteria
     * @return the current query builder instance
     */
    public T clear();
    
    /**
     * If the {@link ParametrizedQueryBuilder} implementations contains an orderBy( enum ) 
     * method, this will set the ordering to ascending. 
     * </p>
     * Query results will be ascendingly ordered by default.
     * </p>
     * If there is no orderBy( enum ) method available, this method will not do anything. 
     * @return the current query builder instance
     */
    public T ascending();
    
    /**
     * If the {@link ParametrizedQueryBuilder} implementations contains an orderBy( enum ) 
     * method, this will set the ordering to descending. 
     * </p>
     * If there is no orderBy( enum ) method available, this method will not do anything. 
     * @return the current query builder instance
     */
    public T descending();
   
    /**
     * Limit the number of results returned by the query to the specified maximum.
     * @return the current query builder instance
     */
    public T maxResults(int maxResults);
   
    /**
     * Limit the results returned by excluding the specified number of results (the offset),
     * from the start of the result list. 
     * </p>
     * Which results are excluded (offset) is dependent on the order of the results returned. 
     * See the orderBy(enum) methods as well as {@link #ascending()} and {@link #descending()}.
     * @return the current query builder instance
     */
    public T offset(int offset);
}
