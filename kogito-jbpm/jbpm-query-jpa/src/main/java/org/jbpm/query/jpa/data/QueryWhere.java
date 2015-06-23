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

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
@JsonIgnoreProperties({"union","type", "currentGroupCriteria", "currentGroupParents", 
                       "like", "range"})
public class QueryWhere {

    @JsonIgnore
    private transient boolean union = true;
    
    @JsonIgnore
    private transient ParameterType type = ParameterType.NORMAL;
    
    @XmlEnum
    public static enum ParameterType { 
        @XmlEnumValue("N") NORMAL,
        @XmlEnumValue("L") REGEXP, 
        @XmlEnumValue("R") RANGE; 
    }

    @XmlElement(name="queryCriteria")
    private List<QueryCriteria> criteria = new LinkedList<QueryCriteria>();

    @JsonIgnore
    private transient List<QueryCriteria> currentGroupCriteria = criteria;
    
    @JsonIgnore
    private transient Stack<Object> currentGroupParents = new Stack<Object>();
    
    public QueryWhere() { 
        // JAXB constructor
    }
    
    // add logic
    
    public <T> QueryCriteria addAppropriateParam( String listId, T... param ) {
        if( param.length == 0 ) {
            return null;
        }
        if( ParameterType.REGEXP.equals(this.type) && ! (param[0] instanceof String) ) { 
            throw new IllegalArgumentException("Only String parameters may be used in regular expressions.");
        }
        QueryCriteria criteria = getAppropriateQueryCriteria(listId, param.length);
        for( T paramElem : param ) { 
           criteria.addParameter(paramElem); 
        }
        addCriteria(criteria);
        return criteria;
    }

    public <T> void addRangeParameter( String listId, T param, boolean start ) {
        ParameterType origType = this.type;
        this.type = ParameterType.RANGE;
        QueryCriteria criteria = getAppropriateQueryCriteria(listId, 2);
        int index = start ? 0 : 1;
        criteria.setParameter(index, param);
        addCriteria(criteria);
        this.type = origType;
    }
  
    private void addCriteria(QueryCriteria criteria) { 
        this.currentGroupCriteria.add(criteria);
    }
    
    private QueryCriteria getAppropriateQueryCriteria(String listId, int valueListSize) { 
        QueryCriteria criteria = new QueryCriteria(listId, this.union, this.type, valueListSize);
        // reset group status
        resetGroup();
        return criteria;
    }
 
    // group management
    
    public void startGroup() { 
        // retrieve or create new parent
        QueryCriteria newCriteriaGroupParent;
        if( currentGroupCriteria.isEmpty() ) { 
            newCriteriaGroupParent = new QueryCriteria();
            currentGroupCriteria.add(newCriteriaGroupParent);
        } else { 
            newCriteriaGroupParent = currentGroupCriteria.get(this.currentGroupCriteria.size()-1);
        }
        this.currentGroupParents.push(newCriteriaGroupParent);
        
        // set group criteria list to correct list
        currentGroupCriteria = newCriteriaGroupParent.getCriteria();
    }

    public void endGroup() { 
       if( currentGroupParents.isEmpty() ) { 
           throw new IllegalStateException("Can not end group: no group has been started!");
       }
       // get parent
       currentGroupParents.pop();
       
       // set current group criteria to point to correct list
       Object newCriteriaGroupParent = currentGroupParents.peek();
       if( newCriteriaGroupParent instanceof QueryWhere ) { 
          currentGroupCriteria = ((QueryWhere) newCriteriaGroupParent).getCriteria();
       } else { 
           currentGroupCriteria = ((QueryCriteria) newCriteriaGroupParent).getCriteria();
       }
    }

    private void resetGroup() { 
        this.currentGroupParents.clear();
    }
    
    // getters & setters
   
    public List<QueryCriteria> getCriteria() {
        return criteria;
    }

    public void setParameters( List<QueryCriteria> parameters ) {
        this.criteria = parameters;
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
        this.type = ParameterType.REGEXP;
    }

    public boolean isLike() {
        return this.type.equals(ParameterType.REGEXP);
    }

    public void setToNormal() {
        this.type = ParameterType.NORMAL;
    }

    public void setToRange() {
        this.type = ParameterType.RANGE;
    }

    public boolean isRange() {
        return this.type.equals(ParameterType.RANGE);
    }
  
    // clear & clone

    public void clear() { 
        this.union = true;
        this.type = ParameterType.NORMAL;
        resetGroup();
        if( this.criteria != null ) { 
            this.criteria.clear();
        }
    }
    
    public QueryWhere(QueryWhere queryParameters) { 
       this.union = queryParameters.union;
       this.type = queryParameters.type;
       if( queryParameters.criteria != null )  {
           this.criteria = new LinkedList<QueryCriteria>(queryParameters.criteria);
       }
    }

    
}