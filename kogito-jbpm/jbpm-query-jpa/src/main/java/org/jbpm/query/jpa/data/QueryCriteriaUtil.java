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

package org.jbpm.query.jpa.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.kie.internal.query.QueryParameterIdentifiers;

/**
 * This class should be moved to the (new) jbpm-query module
 */
public class QueryCriteriaUtil {

    /**
     * Determine if there are no ("purely") limiting criteria to the query
     * </p>
     * "Purely" limiting criteria are single criteria which apply to all results of the query. 
     * @param criteriaList The list of criteria to process
     * @return true if no criteria in the list apply to all of the results of the query
     */
    public static boolean noLimitingCriteria(List<QueryCriteria> criteriaList) { 
        // process first criteria differently
        QueryCriteria criteria = criteriaList.get(0);

        for( int i = 0; i < criteriaList.size(); ++i ) { 
            criteria = criteriaList.get(i);

            if( ! criteria.isUnion() ) { 
                return false;
            }
        }
        
        return true;
    }

    /**
     * Return the ("purely") limiting criteria to the query.
     * </p>
     * "Purely" limiting criteria are single criteria which apply to all results of the query. 
     * 
     * @param criteriaList The list of criteria to process
     * @return The list of purely limiting criteria in the given criteria and subcriteria of the the given criteria
     */
    public static List<QueryCriteria> getLimitingCriteria(List<QueryCriteria> criteriaList) { 
        List<QueryCriteria> allLimitingCriteria = new LinkedList<QueryCriteria>();
        for( QueryCriteria criteria : criteriaList ) { 
            if( ! criteria.isUnion() ) { 
                allLimitingCriteria.add(criteria);
                if( ! criteria.getCriteria().isEmpty() ) { 
                   allLimitingCriteria.addAll(getSubQueryLimitingCriteria(criteria.getCriteria()));
                }
            }
        }
    
        return allLimitingCriteria;
    }
  
    /**
     * Subquery criteria are handled differently: the presence of one union criteria immediately makes the whole 
     * list of subquery criteria non-limiting, because AND has precedence over OR in SQL. 
     * 
     * @param criteriaList The list of criteria to process
     * @return The list of purely limiting criteria in the given criteria and subcriteria of the the given criteria
     */
    private static List<QueryCriteria> getSubQueryLimitingCriteria(List<QueryCriteria> criteriaList) { 
        List<QueryCriteria> limitingCriteria = new LinkedList<QueryCriteria>();
        sortCriteriaUnionFirst(criteriaList);
        for( QueryCriteria criteria : criteriaList ) { 
            if( criteria.isUnion() ) { 
                return Collections.EMPTY_LIST;
            }  else { 
                limitingCriteria.add(criteria);
            }
        }
    
        return limitingCriteria; 
    }
    

    /**
     * Remove any criteria that satisfy both of the following: The criteria must<ul>
     * <li>have the same list id as the given listId string</li>
     * <li>apply to all results of the query</li>
     * </ul>
     * @param listId The listId for the criteria to remove 
     * @param criteriaList The list of {@link QueryCriteria} to process
     * @see {@link QueryParameterIdentifiers}
     */
    public static QueryCriteria removeLimitingCriteria(String listId, List<QueryCriteria> criteriaList) { 
        Iterator<QueryCriteria> iter = criteriaList.iterator();
        while( iter.hasNext() ) { 
            QueryCriteria criteria = iter.next();
            if( ! criteria.isUnion() ) { 
                if( listId.equals(criteria.getListId()) ) { 
                    iter.remove();
                    return criteria;
                }
                if( criteria.getCriteria().size() > 0 ) { 
                   return removeSubQueryLimitingCriteria(listId, criteria.getCriteria()); 
                }
            }
        }
        return null;
    }
    
    private static QueryCriteria removeSubQueryLimitingCriteria(String listId, List<QueryCriteria> criteriaList) { 
        Iterator<QueryCriteria> iter = criteriaList.iterator();
        sortCriteriaUnionFirst(criteriaList);
        while( iter.hasNext() ) { 
            QueryCriteria criteria = iter.next();
            if( criteria.isUnion() ) { 
                return null;
            } else { 
                if( listId.equals(criteria.getListId()) ) { 
                    iter.remove();
                    return criteria;
                }
                if( criteria.getCriteria().size() > 0 ) { 
                   return removeSubQueryLimitingCriteria(listId, criteria.getCriteria()); 
                }
            }
        }
        return null;
    }
   
    private static void sortCriteriaUnionFirst(List<QueryCriteria> criteriaList) { 
        Collections.sort(criteriaList, new Comparator<QueryCriteria>() {
            @Override
            public int compare( QueryCriteria o1, QueryCriteria o2 ) {
                if( o1.isUnion() && ! o2.isUnion() ) { 
                      return 1;
                } else if( o2.isUnion() && ! o1.isUnion() ) { 
                    return -1;
                }
                return 0;
            }
        }); 
    }
    
}