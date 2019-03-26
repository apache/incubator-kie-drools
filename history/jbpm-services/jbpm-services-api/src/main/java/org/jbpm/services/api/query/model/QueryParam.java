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

package org.jbpm.services.api.query.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic data carrier to provide filtering capabilities on top of query definition.
 *
 */
public class QueryParam implements Serializable {
    
    private static final long serialVersionUID = -7751811350486978746L;
    
    public static final String MILLISECOND = "MILLISECOND";
    public static final String HUNDRETH = "HUNDRETH";
    public static final String TENTH = "TENTH";
    public static final String SECOND = "SECOND";
    public static final String MINUTE = "MINUTE";
    public static final String HOUR = "HOUR";
    public static final String DAY = "DAY";
    public static final String DAY_OF_WEEK = "DAY_OF_WEEK";
    public static final String WEEK = "WEEK";
    public static final String MONTH = "MONTH";
    public static final String QUARTER = "QUARTER";
    public static final String YEAR = "YEAR";
    public static final String DECADE = "DECADE";
    public static final String CENTURY = "CENTURY";
    public static final String MILLENIUM = "MILLENIUM";
    
    private String column;
    private String operator;
    private List<?> value;
    
    public QueryParam(String column, String operator, List<?> value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }
    
    public static QueryParam isNull(String column) {
        return new QueryParam(column, "IS_NULL", null);
    }
    
    public static QueryParam isNotNull(String column) {
        return new QueryParam(column, "NOT_NULL", null);
    }
    
    public static QueryParam equalsTo(String column, Comparable<?>...values) {
        return new QueryParam(column, "EQUALS_TO", Arrays.asList(values));
    }
    
    public static QueryParam notEqualsTo(String column, Comparable<?>...values) {
        return new QueryParam(column, "NOT_EQUALS_TO", Arrays.asList(values));
    }
    
    @SuppressWarnings("unchecked")
    public static QueryParam likeTo(String column, boolean caseSensitive, Comparable<?> value) {
        return new QueryParam(column, "LIKE_TO", Arrays.asList(value, caseSensitive));
    }
    
    @SuppressWarnings("unchecked")
    public static QueryParam greaterThan(String column, Comparable<?> value) {
        return new QueryParam(column, "GREATER_THAN", Arrays.asList(value));
    }
    
    @SuppressWarnings("unchecked")
    public static QueryParam greaterOrEqualTo(String column, Comparable<?> value) {
        return new QueryParam(column, "GREATER_OR_EQUALS_TO", Arrays.asList(value));
    }
    
    @SuppressWarnings("unchecked")
    public static QueryParam lowerThan(String column, Comparable<?> value) {
        return new QueryParam(column, "LOWER_THAN", Arrays.asList(value));
    }
    
    @SuppressWarnings("unchecked")
    public static QueryParam lowerOrEqualTo(String column, Comparable<?> value) {
        return new QueryParam(column, "LOWER_OR_EQUALS_TO", Arrays.asList(value));
    }
    
    @SuppressWarnings("unchecked")
    public static QueryParam between(String column, Comparable<?> start, Comparable<?> end) {
        return new QueryParam(column, "BETWEEN", Arrays.asList(start, end));
    }
        
    public static QueryParam in(String column, List<?> values) {
        return new QueryParam(column, "IN", values);
    }
    
    public static QueryParam notIn(String column, List<?> values) {
        return new QueryParam(column, "NOT_IN", values);
    }
    
    public static QueryParam count(String column) {
        return new QueryParam(column, "COUNT", Arrays.asList(column));
    }
    
    public static QueryParam distinct(String column) {
        return new QueryParam(column, "DISTINCT", Arrays.asList(column));
    }
    
    public static QueryParam average(String column) {
        return new QueryParam(column, "AVERAGE", Arrays.asList(column));
    }
    
    public static QueryParam sum(String column) {
        return new QueryParam(column, "SUM", Arrays.asList(column));
    }
    
    public static QueryParam min(String column) {
        return new QueryParam(column, "MIN", Arrays.asList(column));
    }
    
    public static QueryParam max(String column) {
        return new QueryParam(column, "MAX", Arrays.asList(column));
    }
    
    public static QueryParam[] groupBy(String column) {
        return new QueryParam[] {new QueryParam(column, "group", Arrays.asList(column)), new QueryParam(column, null, Arrays.asList(column))};
    }
    
    public static QueryParam[] groupBy(String column, String intervalSize, int maxInterval) {
        return new QueryParam[] {new QueryParam(column, "group", Arrays.asList(column, intervalSize, maxInterval)), new QueryParam(column, null, Arrays.asList(column))};
    }
    
    public String getColumn() {
        return column;
    }
    
    public void setColumn(String column) {
        this.column = column;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public List<?> getValue() {
        return value;
    }
    
    public void setValue(List<?> value) {
        this.value = value;
    }
    
    public static Builder getBuilder() {
        return new Builder();
    }
    
    public static class Builder {
        private List<QueryParam> parameters = new ArrayList<QueryParam>();
        
        public Builder append(QueryParam...params) {
            this.parameters.addAll(Arrays.asList(params));
            
            return this;
        }
        
        public QueryParam[] get() {
            return this.parameters.toArray(new QueryParam[this.parameters.size()]);
        }
    }

}
