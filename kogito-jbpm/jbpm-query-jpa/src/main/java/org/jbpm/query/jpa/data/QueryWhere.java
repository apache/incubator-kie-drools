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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.persistence.criteria.Predicate;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * THIS CLASS SHOULD NEVER BE EXPOSED IN THE PUBLIC API!!
 * </p>
 * EXTERNAL USE OF THIS CLASS IS **NOT** SUPPORTED!
 * </p>
 *
 * This object can be seen as a (dynamic) representation of the <code>WHERE</code> part of a query.
 * </p>
 * It has the following responsibilities: <ol>
 * <li>Hold a list of the added query criteria </li>
 * <li>Keep track of the criteria preferences:<ul>
 *   <li>Are we adding a range, a regexp or just a normal criteria?</li>
 *   <li>Is this the start or end of a group?</li></ul>
 * </li>
 * </ol>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
// @formatter:off
@JsonIgnoreProperties({"union","type",  // transient fields
                       "currentGroupCriteria", "ancestry", "currentParent",
                       "addedJoins"})
@JsonAutoDetect(fieldVisibility=Visibility.ANY,
                getterVisibility=Visibility.NONE,
                setterVisibility=Visibility.NONE,
                isGetterVisibility=Visibility.NONE)
// @formatter:on
public class QueryWhere {

    @XmlEnum
    public static enum QueryCriteriaType {
        @XmlEnumValue("N") NORMAL,
        @XmlEnumValue("L") REGEXP,
        @XmlEnumValue("R") RANGE,
        @XmlEnumValue("R") GROUP,
    }

    @XmlElement(name="queryCriteria")
    private List<QueryCriteria> criteria = new LinkedList<QueryCriteria>();

    @XmlElement
    private Boolean ascOrDesc = null;

    @XmlElement
    private String orderByListId = null;

    @XmlElement
    private Integer maxResults = null;

    @XmlElement
    private Integer offset = null;

    @JsonIgnore
    private transient boolean union = true;

    @JsonIgnore
    private transient QueryCriteriaType type = QueryCriteriaType.NORMAL;

    @JsonIgnore
    private transient List<QueryCriteria> currentCriteria = criteria;

    @JsonIgnore
    private transient Stack<Object> ancestry = new Stack<Object>();

    @JsonIgnore
    private transient Object currentParent = this;

    @JsonIgnore
    private transient Map<String, Predicate> joinPredicates = null;

    public QueryWhere() {
        // JAXB constructor
    }

    // add logic

    /**
     * This method should be used for<ol>
     * <li>Normal parameters</li>
     * <li>Regular expression parameters</li>
     * </ol>
     * This method should <b>not</b> be used for<ol>
     * <li>Range parameters</li>
     * </ol>
     * @param listId
     * @param param
     * @return
     */
    public <T> QueryCriteria addParameter( String listId, T... param ) {
        if( param.length == 0 ) {
            return null;
        }
        if( QueryCriteriaType.REGEXP.equals(this.type) && ! (param[0] instanceof String) ) {
            throw new IllegalArgumentException("Only String parameters may be used in regular expressions.");
        }
        QueryCriteria criteria =  new QueryCriteria(listId, this.union, this.type, param.length);
        for( T paramElem : param ) {
           criteria.addParameter(paramElem);
        }
        addCriteria(criteria);
        return criteria;
    }

    public <T> void addRangeParameter( String listId, T param, boolean start ) {
        QueryCriteriaType origType = this.type;
        this.type = QueryCriteriaType.RANGE;
        //should be the same as before!
        QueryCriteria criteria =  new QueryCriteria(listId, this.union, this.type, 2);
        int index = start ? 0 : 1;
        criteria.setParameter(index, param, 2);
        addCriteria(criteria);
        this.type = origType;
    }

    public <T> void addRangeParameters( String listId, T paramMin, T paramMax ) {
        QueryCriteriaType origType = this.type;
        this.type = QueryCriteriaType.RANGE;
        //should be the same as before!
        QueryCriteria criteria =  new QueryCriteria(listId, this.union, this.type, 2);
        criteria.addParameter(paramMin);
        criteria.addParameter(paramMax);
        addCriteria(criteria);
        this.type = origType;
    }

    private void addCriteria(QueryCriteria criteria) {
        if( this.currentCriteria.isEmpty() ) {
            criteria.setFirst(true);
        } else if( this.currentCriteria.size() == 1 ) {
           this.currentCriteria.get(0).setUnion(criteria.isUnion());
        }
        this.currentCriteria.add(criteria);
    }

    // group management

    public void newGroup() {
        // create parent
        QueryCriteria newCriteriaGroupParent = new QueryCriteria(this.union);
        addCriteria(newCriteriaGroupParent);

        //  add parent to parent stack
        ancestry.push(currentParent);
        currentParent = newCriteriaGroupParent;

        // set group criteria list to new list
        currentCriteria = newCriteriaGroupParent.getCriteria();
    }

    public void endGroup() {
       if( ancestry.isEmpty() ) {
           throw new IllegalStateException("Can not end group: no group has been started!");
       }
       // set current group criteria to point to correct list
       Object grandparent = ancestry.pop();
       if( grandparent instanceof QueryWhere ) {
           currentCriteria = ((QueryWhere) grandparent).getCriteria();
       } else {
           currentCriteria = ((QueryCriteria) grandparent).getCriteria();
       }
       currentParent = grandparent;
    }

    @JsonIgnore
    public void setAscending( String listId ) {
        this.ascOrDesc = true;
        this.orderByListId = listId;
    }

    @JsonIgnore
    public void setDescending( String listId ) {
        this.ascOrDesc = false;
        this.orderByListId = listId;
    }

    public List<QueryCriteria> getCurrentCriteria() {
        return currentCriteria;
    }

    // getters & setters

    public List<QueryCriteria> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<QueryCriteria> criteria) {
        this.criteria = criteria;
    }

    public void setParameters( List<QueryCriteria> parameters ) {
        this.criteria = parameters;
    }

    public void setAscOrDesc( Boolean ascendingOrDescending ) {
        this.ascOrDesc = ascendingOrDescending;
    }

    public Boolean getAscOrDesc() {
        return this.ascOrDesc;
    }

    public void setOrderByListId( String listId ) {
        this.orderByListId = listId;
    }

    public String getOrderByListId() {
        return this.orderByListId;
    }

    public void setCount( Integer maxResults ) {
        this.maxResults = maxResults;
    }

    public Integer getCount() {
        return this.maxResults;
    }

    public void setOffset( Integer offset ) {
        this.offset = offset;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public QueryCriteriaType getCriteriaType() {
       return this.type;
    }

    public void setToUnion() {
        this.union = true;
    }

    public void setToIntersection() {
        this.union = false;
    }

    public boolean isUnion() {
       return this.union;
    }

    public void setToLike() {
        this.type = QueryCriteriaType.REGEXP;
    }

    public boolean isLike() {
        return this.type.equals(QueryCriteriaType.REGEXP);
    }

    public void setToNormal() {
        this.type = QueryCriteriaType.NORMAL;
    }

    public void setToRange() {
        this.type = QueryCriteriaType.RANGE;
    }

    public boolean isRange() {
        return this.type.equals(QueryCriteriaType.RANGE);
    }

    public void setToGroup() {
        this.type = QueryCriteriaType.GROUP;
    }

    public Map<String, Predicate> getJoinPredicates() {
        if( this.joinPredicates == null ) {
          this.joinPredicates = new HashMap<String, Predicate>(3);
        }
        return this.joinPredicates;
    }

    // clear & clone

    public void clear() {
        this.union = true;
        this.type = QueryCriteriaType.NORMAL;
        this.ancestry.clear();
        if( this.criteria != null ) {
            this.criteria.clear();
        }
        this.currentCriteria = this.criteria;

        this.maxResults = null;
        this.offset = null;
        this.orderByListId = null;
        this.ascOrDesc = null;

       this.joinPredicates = null;
    }

    public QueryWhere(QueryWhere queryWhere) {
       this.union = queryWhere.union;
       this.type = queryWhere.type;
       if( queryWhere.criteria != null )  {
           this.criteria = new LinkedList<QueryCriteria>(queryWhere.criteria);
       }
       this.ascOrDesc = queryWhere.ascOrDesc;
       this.orderByListId = queryWhere.orderByListId;
       this.maxResults = queryWhere.maxResults;
       this.offset = queryWhere.offset;

       this.joinPredicates = queryWhere.joinPredicates;
    }


}