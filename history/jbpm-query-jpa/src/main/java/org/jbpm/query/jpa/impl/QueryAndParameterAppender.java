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

package org.jbpm.query.jpa.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.kie.internal.query.QueryParameterIdentifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a utility class for dynamically creating JPA queries.
 * </p>
 * See the jbpm-human-task-core and jbpm-audit *query() method logic.
 * </p>
 * This class is <em>not</em> thread-safe and should only be used locally in a method.
 */
public class QueryAndParameterAppender {

    private static Logger logger = LoggerFactory.getLogger(QueryAndParameterAppender.class);

    private boolean noWhereClauseYet = true;
    private boolean noClauseAddedYet = true;
    private int nestedParentheses = 0;
    private boolean alreadyUsed = false;
    private final StringBuilder queryBuilder;
    private final Map<String, Object> queryParams;

    private int queryParamId = 0;

    public QueryAndParameterAppender(StringBuilder queryBuilder, Map<String, Object> params) {
        this.queryBuilder = queryBuilder;
        this.queryParams = params;
        this.noWhereClauseYet = ! queryBuilder.toString().contains("WHERE");
    }

    public boolean hasBeenUsed() {
        return ! this.noClauseAddedYet;
    }

    public void markAsUsed() {
        this.noClauseAddedYet = false;
    }

    public void addNamedQueryParam(String name, Object value) { 
        queryParams.put(name, value);
    }
   
    public void openParentheses() { 
        ++nestedParentheses;
        queryBuilder.append(" ( "); 
    }
    
    public void closeParentheses() { 
        queryBuilder.append(" ) "); 
        --nestedParentheses;
    }
   
    public int getParenthesesNesting() { 
        return nestedParentheses;
    }
    
    // "Normal" query parameters --------------------------------------------------------------------------------------------------

    public <T> void addQueryParameters( List<? extends Object> paramList, String listId, Class<T> type, String fieldName,
            String joinClause, boolean union ) {
        List<T> listIdParams;
        if( paramList != null && paramList.size() > 0 ) {
            Object inputObject = paramList.get(0);
            listIdParams = checkAndConvertListToType(paramList, inputObject, listId, type);
        } else {
            return;
        }
        String paramName = generateParamName();
        StringBuilder queryClause = new StringBuilder("( " + fieldName + " IN (:" + paramName + ")");
        if( joinClause != null ) {
            queryClause.append(" AND " + joinClause);
        }
        queryClause.append(" )");
        addToQueryBuilder(queryClause.toString(), union, paramName, listIdParams );
    }

    public <T> void addQueryParameters( Map<String, List<? extends Object>> inputParamsMap, String listId, Class<T> type,
            String fieldName, boolean union, String joinClause ) {
        List<? extends Object> inputParams = inputParamsMap.get(listId);
        addQueryParameters(inputParams, listId, type, fieldName, joinClause, union );
    }

    public <T> void addQueryParameters( List<? extends Object> inputParams, String listId, Class<T> type, String fieldName,
            boolean union ) {
        addQueryParameters(inputParams, listId, type, fieldName, null, union );
    }

    public <T> void addQueryParameters( Map<String, List<? extends Object>> inputParamsMap, String listId, Class<T> type,
            String fieldName, boolean union ) {
        List<? extends Object> inputParams = inputParamsMap.get(listId);
        addQueryParameters(inputParams, listId, type, fieldName, null, union );
    }

    // Range query parameters -----------------------------------------------------------------------------------------------------

    public <T> void addRangeQueryParameters(List<? extends Object> paramList, String listId, Class<T> type, String fieldName, String joinClause, boolean union ) {
        List<T> listIdParams;
        if( paramList != null && paramList.size() > 0 ) { 
            Object inputObject = paramList.get(0);
            if( inputObject == null ) { 
                inputObject = paramList.get(1);
                if( inputObject == null ) { 
                    return;
                }
            }  
            listIdParams = checkAndConvertListToType(paramList, inputObject, listId, type);
        } else { 
            return;
        }
        
        T min = listIdParams.get(0);
        T max = listIdParams.get(1);
        Map<String, T> paramNameMinMaxMap = new HashMap<String, T>(2);
        StringBuilder queryClause = new StringBuilder("( " );
        if( joinClause != null ) { 
           queryClause.append("( "); 
        } 
        queryClause.append(fieldName);
        if( min == null ) { 
          if( max == null ) { 
              return;
          } else { 
              // only max
              String maxParamName = generateParamName();
              queryClause.append(" <= :" + maxParamName + " " );
              paramNameMinMaxMap.put(maxParamName, max);
          }
        } else if( max == null ) { 
            // only min
            String minParamName = generateParamName();
            queryClause.append(" >= :" + minParamName + " ");
            paramNameMinMaxMap.put(minParamName, min);
        } else { 
            // both min and max
            String minParamName = generateParamName();
            String maxParamName = generateParamName();
            if( union ) { 
                queryClause.append(" >= :" + minParamName + " OR " + fieldName + " <= :" + maxParamName + " " );
            } else { 
                queryClause.append(" BETWEEN :" + minParamName + " AND :" + maxParamName + " " );
            }
            paramNameMinMaxMap.put(minParamName, min);
            paramNameMinMaxMap.put(maxParamName, max);
        } 
        if( joinClause != null ) { 
            queryClause.append(") and " + joinClause.trim() + " ");
        }
        queryClause.append(")");
        
        // add query string to query builder and fill params map
        internalAddToQueryBuilder(queryClause.toString(), union);
        for( Entry<String, T> nameMinMaxEntry : paramNameMinMaxMap.entrySet() ) { 
            addNamedQueryParam(nameMinMaxEntry.getKey(), nameMinMaxEntry.getValue());
        }
        queryBuilderModificationCleanup();
    }

    public <T> void addRangeQueryParameters( Map<String, List<? extends Object>> inputParamsMap, String listId, Class<T> type,
            String fieldName, boolean union, String joinClause ) {
        List<? extends Object> inputParams = inputParamsMap.get(listId);
        addRangeQueryParameters(inputParams, listId, type, fieldName, joinClause, union );
    }

    public <T> void addRangeQueryParameters( List<? extends Object> inputParams, String listId, Class<T> type, String fieldName,
            boolean union ) {
        addRangeQueryParameters(inputParams, listId, type, fieldName, null, union);
    }

    public <T> void addRangeQueryParameters( Map<String, List<? extends Object>> inputParamsMap, String listId, Class<T> type,
            String fieldName, boolean union ) {
        List<? extends Object> inputParams = inputParamsMap.get(listId);
        addRangeQueryParameters(inputParams, listId, type, fieldName, null, union);
    }

    // Regex query parameters -----------------------------------------------------------------------------------------------------

    public void addRegexQueryParameters( List<String> inputParams, String listId, String fieldName, boolean union ) {
        addRegexQueryParameters(inputParams, listId, fieldName, null, union);
    }

    public void addRegexQueryParameters( List<String> paramValList, String listId, String fieldName, String joinClause, 
            boolean union) {
        // setup
        if( paramValList == null || paramValList.isEmpty() ) {
            return;
        }
        List<String> regexList = new ArrayList<String>(paramValList.size());
        for( String input : paramValList ) {
            if( input == null || input.isEmpty() ) {
                continue;
            }
            String regex = input.replace('*', '%').replace('.', '_');
            regexList.add(regex);
        }

        // build query string
        Map<String, String> paramNameRegexMap = new HashMap<String, String>();
        StringBuilder queryClause = new StringBuilder("( ");
        if( joinClause != null ) {
            queryClause.append("( ");
        }
        for( int i = 0; i < regexList.size(); ++i ) {
            String paramName = generateParamName();
            queryClause.append(fieldName + " LIKE :" + paramName + " " );
            paramNameRegexMap.put(paramName, regexList.get(i));
            if( i + 1 < regexList.size() ) {
                queryClause.append(union ? "OR" : "AND").append(" ");
            }
        }
        if( joinClause != null ) {
            queryClause.append(") AND " + joinClause.trim() + " ");
        }
        queryClause.append(")");

        // add query string to query builder and fill params map
        internalAddToQueryBuilder(queryClause.toString(), union);
        for( Entry<String, String> nameRegexEntry : paramNameRegexMap.entrySet() ) {
            addNamedQueryParam(nameRegexEntry.getKey(), nameRegexEntry.getValue());
        }
        queryBuilderModificationCleanup();
    }

    public void addToQueryBuilder( String query, boolean union ) { 
        // modify query builder
        internalAddToQueryBuilder(query, union);
        // cleanup
        queryBuilderModificationCleanup();
    }
    
    public <T> void addToQueryBuilder( String query, boolean union, String paramName, List<T> paramValList  ) {
        // modify query builder
        internalAddToQueryBuilder(query, union);
        // add query parameters
        Set<T> paramVals = new HashSet<T>(paramValList);
        addNamedQueryParam(paramName, paramVals);
        // cleanup
        queryBuilderModificationCleanup();
    }

    private void internalAddToQueryBuilder( String query, boolean union ) {
        if( this.noClauseAddedYet ) {
            if( noWhereClauseYet ) {
                queryBuilder.append(" WHERE ");
            } else {
                queryBuilder.append(" AND ");
            }
            this.noClauseAddedYet = false;
        } else if( this.alreadyUsed ) {
            queryBuilder.append(union ? "\nOR " : "\nAND ");
        }
        queryBuilder.append(query);
    }

    public void queryBuilderModificationCleanup() {
        this.alreadyUsed = true;
    }
    
    public boolean whereClausePresent() { 
        return ! noWhereClauseYet;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> checkAndConvertListToType( List<?> inputList, Object inputObject, String listId, Class<T> type ) {
        if( logger.isDebugEnabled() ) { 
            debugQueryParametersIdentifiers();
        }
        assert type != null : listId + ": type is null!";
        assert inputObject != null : listId + ": input object is null!";
        if( type.equals(inputObject.getClass()) ) {
            return (List<T>) inputList;
        } else {
            throw new IllegalArgumentException(listId + " parameter is an instance of " + "List<"
                    + inputObject.getClass().getSimpleName() + "> instead of " + "List<" + type.getSimpleName() + ">");
        }
    }

    public String generateParamName() {
        int id = queryParamId++ % 26;
        char first = (char) ('A' + id);
        return new String(first + String.valueOf(((id + 1) / 26) + 1));
    }
    
    public StringBuilder getQueryBuilder() { 
        return queryBuilder;
    }

    public static void debugQueryParametersIdentifiers() { 
       try { 
          Field [] fields = QueryParameterIdentifiers.class.getDeclaredFields(); 
          Map<String, String> fieldValueMap = new TreeMap<String, String>(new Comparator<String>() {

            @Override
            public int compare( String o1, String o2 ) {
                int int1 = -1;
                try { 
                    int1 = Integer.parseInt(o1);
                } catch(Exception e) { 
                    // no op
                }
                int int2 = -1;
                try { 
                    int2 = Integer.parseInt(o2);
                } catch(Exception e) { 
                    // no op
                }
                if( int1 > -1 && int2 > -1 ) { 
                    return new Integer(int1).compareTo(int2);
                }
                if( int1 > -1 && int2 == -1 ) { 
                    return -1;
                } 
                if( int1 == -1 && int2 > -1 ) { 
                    return 1;
                }
                return o1.compareTo(o2);
            }
        });
          for( Field field : fields ) { 
             fieldValueMap.put(field.get(null).toString(), field.getName());
          }
          for( Entry<String, String> entry : fieldValueMap.entrySet() ) { 
             logger.debug(String.format("%-12s : %s", entry.getKey(), entry.getValue()));
          }
       } catch( Exception e ) { 
           // ignore 
       }
    }
}
