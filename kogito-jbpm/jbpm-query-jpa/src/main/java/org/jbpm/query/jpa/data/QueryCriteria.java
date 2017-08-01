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

package org.jbpm.query.jpa.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jbpm.query.jpa.data.QueryWhere.QueryCriteriaType;
import org.kie.internal.query.QueryParameterIdentifiers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * This object contains the following information:
 * <ol>
 * <li>The listId, which refers to the field that this criteria applies to<ul>
 *   <li>See {@link QueryParameterIdentifiers}</li></ul>
 * </li>
 * <li>The values of the criteria, which will be applied to the listId field<ul>
 *   <li>For example, it could be a list of numbers "1, 22, 3"</li></ul>
 * </li>
 * <li>Whether this is a union ("OR") or intersection ("AND") critieria</li>
 * <li>The type of criteria: normal, like (JPQL regex) or range</li>
 * <li>The grouping information of the phrase (see below)</li>
 * </ol>
 * </p>
 * With regard to the grouping information in this class, we treat JPQL/SQL as a "prefix" language here, which means that
 * this class represents the following regular expression/BNF string:
 * <pre>
 *   [)]{0,} [OR|AND] [(]{0,} &lt;CRITERIA&gt;
 * </pre>
 * This structure is then represented by the following fields:
 * <pre>
 *   [endGroups] [union] [startGroupos] [values]
 * </pre>
 *
 * The main reason to include the grouping status in this object is that other data structures (nested lists, etc)
 * are much harder to de/serialize correctly.
 */
@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(value="parameters")
public class QueryCriteria {

    @XmlAttribute
    private String listId;

    @XmlAttribute
    private boolean union = true;

    @XmlAttribute
    private boolean first = false;

    @XmlAttribute
    private QueryCriteriaType type = QueryCriteriaType.NORMAL;

    @XmlElement(name="parameter")
    @JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
    private List<Object> values;

    @XmlElement(name="date-parameter")
    private List<Date> dateValues;

    @XmlElement
    private List<QueryCriteria> criteria;

    public QueryCriteria() {
        // default (JAXB/JSON) constructor
    }

    /**
     * Used when creating a group criteria
     * @param union Whether or not the group is part of an intersection or disjunction
     */
    public QueryCriteria(boolean union) {
        this.union = union;
        this.type = QueryCriteriaType.GROUP;
    }

    private QueryCriteria(String listId, QueryCriteriaType type) {
        this.listId = listId;
        this.type = type;
    }

    /**
     * Used for all other criteria
     * @param listId The {@link QueryParameterIdentifiers} list id
     * @param union Whether or not the criteria is part of an intersection or disjunction
     * @param type The type: {@link QueryCriteriaType#NORMAL}, {@link QueryCriteriaType#REGEXP}, or {@link QueryCriteriaType#RANGE},
     * @param valueListSize The size of the value list
     */
    public QueryCriteria(String listId, boolean union, QueryCriteriaType type, int valueListSize) {
        this(listId, type);
        this.union = union;
        this.values = new ArrayList<Object>(valueListSize);
    }

    public String getListId() {
        return listId;
    }

    public void setListId( String listId ) {
        this.listId = listId;
    }

    public boolean isUnion() {
        return union;
    }

    public void setUnion( boolean union ) {
        this.union = union;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst( boolean first ) {
        this.first = first;
    }


    public QueryCriteriaType getType() {
        return type;
    }

    public void setType( QueryCriteriaType type ) {
        this.type = type;
    }

    public List<Object> getValues() {
        if( this.values == null ) {
            this.values = new ArrayList<Object>();
        }
        return values;
    }

    public void setValues( List<Object> values ) {
        this.values = values;
    }

    public List<Date> getDateValues() {
        if( this.dateValues == null ) {
            this.dateValues = new ArrayList<Date>();
        }
        return dateValues;
    }

    public void setDateValues( List<Date> dateValues ) {
        this.dateValues = dateValues;
    }


    // other methods

    @JsonIgnore
    public boolean isGroupCriteria() {
        return this.type.equals(QueryCriteriaType.GROUP);
    }

    @JsonIgnore
    public boolean hasValues() {
        return ( this.values != null && ! this.values.isEmpty() );
    }

    @JsonIgnore
    public boolean hasDateValues() {
        return ( this.dateValues != null && ! this.dateValues.isEmpty() );
    }

    @JsonIgnore
    public boolean hasCriteria() {
        return ( this.criteria != null && ! this.criteria.isEmpty() );
    }

    public List<QueryCriteria> getCriteria() {
        if( this.criteria == null ) {
            this.criteria = new ArrayList<QueryCriteria>();
        }
        return criteria;
    }

    public void setCriteria( List<QueryCriteria> criteria ) {
        this.criteria = criteria;
    }

    /**
     * This method returns a list that should only be read
     * @return
     */
    public List<Object> getParameters() {
        List<Object> parameters = new ArrayList<Object>(getValues());
        if( this.dateValues != null && ! this.dateValues.isEmpty() ) {
           parameters.addAll(this.dateValues);
        }
        if( parameters.isEmpty() ) {
            return parameters;
        }
        return parameters;
    }

    void addParameter( Object value ) {
        if( value instanceof Date ) {
            getDateValues().add((Date) value);
        } else {
            getValues().add(value);
        }
    }

    @SuppressWarnings("unchecked")
    void setParameter( int index, Object value, int listSize ) {
        List addValues;
        if( value instanceof Date ) {
           addValues = getDateValues();
        } else {
            addValues = getValues();
        }
        while( addValues.size() <= index ) {
           addValues.add(null);
        }
        addValues.set(index, value); // throws NPE for (index > 1) if (list < index)
        while( addValues.size() < listSize ) {
           addValues.add(null);
        }
    }

    public void addCriteria( QueryCriteria criteria ) {
       getCriteria().add(criteria);
    }

    public QueryCriteria(QueryCriteria queryCriteria) {
        this.listId = queryCriteria.listId;
        this.union = queryCriteria.union;
        this.first = queryCriteria.first;
        this.type = queryCriteria.type;
        if( queryCriteria.values != null ) {
            this.values = new ArrayList<Object>(queryCriteria.values);
        }
        if( queryCriteria.dateValues != null ) {
            this.dateValues = new ArrayList<Date>(queryCriteria.dateValues);
        }
        if( queryCriteria.criteria != null ) {
            this.criteria = new ArrayList<QueryCriteria>(queryCriteria.criteria);
        }
    }

    private static SimpleDateFormat toStringSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String toString() {
       StringBuilder out = new StringBuilder();
       if( ! first ) {
           out.append(union ? "OR" : "AND").append(" ");
       }
       if( listId != null ) {
           out.append(listId);
       }
       if( this.values != null && ! this.values.isEmpty() ) {
           out.append(" =");
           if( type.equals(QueryCriteriaType.REGEXP) ) {
               out.append("~");
           }
           out.append(" ");
           if ( type.equals(QueryCriteriaType.RANGE) ) {
               out.append("[");
           }
           out.append(this.values.get(0));
           for( int i = 1; i < this.values.size(); ++i ) {
               out.append(", ") .append(this.values.get(i));
           }
           if ( type.equals(QueryCriteriaType.RANGE) ) {
               out.append("]");
           }
       } else if( this.dateValues != null && ! this.dateValues.isEmpty() ) {
           out.append(" =");
           if( type.equals(QueryCriteriaType.REGEXP) ) {
               out.append("~");
           }
           out.append(" ");
           if ( type.equals(QueryCriteriaType.RANGE) ) {
               out.append("[");
           }
           Date date = this.dateValues.get(0);
           String dateStr = date != null ? toStringSdf.format(date) : "null";
           out.append(dateStr);
           for( int i = 1; i < this.dateValues.size(); ++i ) {
               date = this.dateValues.get(i);
               dateStr = date != null ? toStringSdf.format(date) : "null";
               out.append(", ") .append(dateStr);
           }
           if ( type.equals(QueryCriteriaType.RANGE) ) {
               out.append("]");
           }
       }
       if( criteria != null ) {
           if( out.length() > 0 ) {
               out.append(" ");
           }
           out.append("(");
           int size = criteria.size();
           if( size > 0 ) {
               out.append(criteria.get(0).toString());
           }
           for( int i = 1; i < size; ++i ) {
               out.append(", ");
               out.append(criteria.get(i).toString());
           }
           out.append(")");
       }
       return out.toString();
    }
}
