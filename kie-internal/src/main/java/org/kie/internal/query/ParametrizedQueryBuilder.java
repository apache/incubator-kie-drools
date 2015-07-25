/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
     * are "OR" or "union" criteria. 
     * </p>
     * Since this is based on SQL, please remember that intersection operands
     * have precedence over union operands. 
     * </p>
     * In other words, <ul>
     * <li><code>A or B and C == A or (B and C)</code></li>
     * <li><code>A and B or C == (A and B) or C</code></li>
     * </ul>
     * </p>
     * This is the default mode for all query builders. 
     * @return the current query builder instance
     */
    public T union();
    
    /**
     * Query criteria which are added to the query after this method
     * are "AND" or "intersection" criteria. 
     * </p>
     * Since this is based on SQL, please remember that intersection operands
     * have precedence over union operands. 
     * </p>
     * In other words, <ul>
     * <li><code>A or B and C == A or (B and C)</code></li>
     * <li><code>A and B or C == (A and B) or C</code></li>
     * </ul>
     * </p>
     * @return the current query builder instance
     */
    public T intersect();

    /**
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
     * Limit the number of results returned by the query to the specified maximum.
     * @param maxResults The maximum number of results to return
     *
     * @return the current query builder instance
     */
    public T maxResults(int maxResults);
   
    /**
     * Limit the results returned by excluding the specified number of results (the offset),
     * from the start of the result list. 
     * </p>
     * Which results are excluded (offset) is dependent on the order of the results returned. 
     * See the orderBy(enum) methods as well as {@link #ascending()} and {@link #descending()}.
     * @param offset The number of elements skipped before the first element in the result
     * @return the current query builder instance
     */
    public T offset(int offset);
}
